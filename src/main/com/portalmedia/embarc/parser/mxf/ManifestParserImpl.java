package com.portalmedia.embarc.parser.mxf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.tools.ant.Project;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/***
 * Parse MXF manifest and determine if it is a valid document
 * @author PortalMedia
 *
 */
public class ManifestParserImpl implements ManifestParser {
	private File schemaFile;
	private String manifestFileResource = "resources/RDD48-Manifest-20180827.xsd";

	public ManifestParserImpl() throws FileNotFoundException {
		try {
			InputStream in = Project.class.getClassLoader().getResourceAsStream(manifestFileResource);
			if (in == null) {
				throw new FileNotFoundException("Unable to find manifest schema file");
			}

			File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
			tempFile.deleteOnExit();

			try (FileOutputStream out = new FileOutputStream(tempFile)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}

			schemaFile = tempFile;
		} catch (Exception e) {
			throw new FileNotFoundException("Unable to find manifest schema file");
		}
	}

	/***
	 * Determine whether or not the input stream is a valid manifest or not
	 * @param xmlFileStream
	 * @return 	NOT_MANIFEST = Not INVALID_MANIFEST or VALID_MANIFEST
	 * 			INVALID_MANIFEST = Root node is "Manifest", but not valid schema
	 * 			VALID_MANIFEST = Follows Manifest XSD
	 */
	@Override
	public ManifestType isManifest(ByteBuffer bb) {
		
		File xmlFile = null;
		FileOutputStream fos = null;
		FileChannel channel = null;
		ManifestType type = ManifestType.INVALID_MANIFEST;
		try {
			xmlFile = File.createTempFile("mxf_manifest", null);
			fos = new FileOutputStream(xmlFile, false);
			channel = fos.getChannel();
			channel.write(bb);
			fos.close();
	        if(channel.isOpen()) channel.close();
		} catch (IOException e) {
			if(fos!=null) {
				try {
					fos.close();
				} catch (IOException e1) {
				}
			}
			if(channel!=null && channel.isOpen()) {
				try {
					channel.close();
				} catch (IOException e1) {
				}
			}
		}
	    try {
			// If it's root node is not "Manifest", return NOT_MANIFEST
			if(!isManifestRoot(xmlFile)) {
				type = ManifestType.NOT_MANIFEST;
			} else {
				// Otherwise, determine if manifest follows XSD
				type = isValidManifest(xmlFile) ? ManifestType.VALID_MANIFEST : ManifestType.INVALID_MANIFEST;
			}
			
		} catch (Exception e) {
		}
	    finally {
			if(xmlFile!=null && xmlFile.exists()) {
				xmlFile.delete();
			}
	    }

		
	    return type;
	}
	
	/**
	 * Determine if root node of XML file is "Manifest"
	 * @param file
	 * @return Boolean indicating if xml root node is Manifest
	 */
	private boolean isManifestRoot(File file) {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
		    Document doc = db.parse(file);
		    Element root = doc.getDocumentElement();
		    return root.getNodeName().equals("Manifest");
		} catch (Exception e) {
		}
	    return false;
	}
	
	/**
	 * Test XML file against Manifest XSD
	 * @param xmlFileStream
	 * @return
	 */
	private boolean isValidManifest(File xmlFileSource) {
		Source xmlFile = new StreamSource(xmlFileSource);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
		  Schema schema = schemaFactory.newSchema(schemaFile);
		  Validator validator = schema.newValidator();
		  validator.validate(xmlFile);
		  return true;
		} catch (SAXException e) {
		  System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
		} catch (IOException e) {}
		return false;
	}
}
