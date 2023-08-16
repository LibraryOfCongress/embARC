package com.portalmedia.embarc.parser.mxf;

import java.nio.ByteBuffer;

public interface ManifestParser {

	/***
	 * Determine whether or not the input stream is a valid manifest or not
	 * @param xmlFileStream
	 * @return 	NOT_MANIFEST = Not INVALID_MANIFEST or VALID_MANIFEST
	 * 			INVALID_MANIFEST = Root node is "Manifest", but not valid schema
	 * 			VALID_MANIFEST = Follows Manifest XSD
	 */
	ManifestType isManifest(ByteBuffer bb);

}