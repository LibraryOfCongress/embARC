package com.portalmedia.embarc.parser.mxf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.parser.FileInformation;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.StringMetadataColumn;

import tv.amwa.maj.exception.PropertyNotPresentException;
import tv.amwa.maj.io.mxf.BodyPartition;
import tv.amwa.maj.io.mxf.BodyPartitionPack;
import tv.amwa.maj.io.mxf.EssencePartition;
import tv.amwa.maj.io.mxf.FooterPartition;
import tv.amwa.maj.io.mxf.FooterPartitionPack;
import tv.amwa.maj.io.mxf.HeaderMetadata;
import tv.amwa.maj.io.mxf.HeaderPartitionPack;
import tv.amwa.maj.io.mxf.IndexTableSegment;
import tv.amwa.maj.io.mxf.MXFFactory;
import tv.amwa.maj.io.mxf.MXFFile;
import tv.amwa.maj.io.mxf.MXFStream;
import tv.amwa.maj.io.mxf.MXFStream.KeyAndConsumed;
import tv.amwa.maj.io.mxf.MXFStream.LengthAndConsumed;
import tv.amwa.maj.io.mxf.RandomIndexItem;
import tv.amwa.maj.io.mxf.impl.EssenceElementImpl;
import tv.amwa.maj.io.mxf.impl.FooterPartitionImpl;
import tv.amwa.maj.io.mxf.impl.GenericStreamPartitionPackImpl;
import tv.amwa.maj.io.mxf.impl.HeaderPartitionImpl;
import tv.amwa.maj.io.mxf.impl.RandomIndexItemImpl;
import tv.amwa.maj.io.mxf.impl.RandomIndexPackImpl;
import tv.amwa.maj.model.AAFFileDescriptor;
import tv.amwa.maj.model.AS07CoreDMSFramework;
import tv.amwa.maj.model.AS07DMSIdentifierSet;
import tv.amwa.maj.model.AS07GSPDMSObject;
import tv.amwa.maj.model.Component;
import tv.amwa.maj.model.ContentStorage;
import tv.amwa.maj.model.DataDefinition;
import tv.amwa.maj.model.DescriptiveFramework;
import tv.amwa.maj.model.DescriptiveMarker;
import tv.amwa.maj.model.MaterialPackage;
import tv.amwa.maj.model.MultipleDescriptor;
import tv.amwa.maj.model.Preface;
import tv.amwa.maj.model.Segment;
import tv.amwa.maj.model.Sequence;
import tv.amwa.maj.model.StaticTrack;
import tv.amwa.maj.model.TimelineTrack;
import tv.amwa.maj.model.Track;
import tv.amwa.maj.model.impl.AS07CoreDMSDeviceObjectsImpl;
import tv.amwa.maj.model.impl.AS07CoreDMSFrameworkImpl;
import tv.amwa.maj.model.impl.AS07DMSIdentifierSetImpl;
import tv.amwa.maj.model.impl.AS07DateTimeDescriptorImpl;
import tv.amwa.maj.model.impl.AS07GspBdDMSFrameworkImpl;
import tv.amwa.maj.model.impl.AS07GspTdDMSFrameworkImpl;
import tv.amwa.maj.model.impl.AncillaryPacketsDescriptorImpl;
import tv.amwa.maj.model.impl.CDCIDescriptorImpl;
import tv.amwa.maj.model.impl.DescriptiveMarkerImpl;
import tv.amwa.maj.model.impl.PictureDescriptorImpl;
import tv.amwa.maj.model.impl.RGBADescriptorImpl;
import tv.amwa.maj.model.impl.STLDescriptorImpl;
import tv.amwa.maj.model.impl.SoundDescriptorImpl;
import tv.amwa.maj.model.impl.TimedTextDescriptorImpl;
import tv.amwa.maj.model.impl.VBIDescriptorImpl;
import tv.amwa.maj.model.impl.WAVEPCMDescriptorImpl;
import tv.amwa.maj.record.AUID;
import tv.amwa.maj.record.impl.AUIDImpl;

public class MXFServiceImpl implements MXFService {
	MXFFile file = null;
	String filePath = null;
	String formatVersion = "";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getClass().getName());
	
	public MXFServiceImpl(String filePath) throws FileNotFoundException {
		if(!new File(filePath).exists()) {
			throw new FileNotFoundException(String.format("File %s not found", filePath));
		}
		this.filePath = filePath;
	}
	public EmbARCIdentification getIdentification() {
		EmbARCIdentification identificationSingleton = EmbARCIdentification.getInstance();
		return identificationSingleton;
	}
	public void readFile() {
		file = MXFFactory.readPartitions(filePath);
	}
	
	public MXFFile getFile() {
		if(file==null) readFile();
		return file;
	}
	
	private long getFooterOffset(AS07CoreDMSFramework updatedCore) throws IOException {
		MXFFile mxfFile = getFile();
		EmbARCIdentification embarcIdent = getIdentification();
		long totalBytes = 0;
		long thisPartition = 0;
		long previousPartition = 0;
		long klvFillLength = 8192;
		ArrayList<RandomIndexItem> ripItems = new ArrayList<RandomIndexItem>();
		if (!mxfFile.isOpen())
			throw new IOException("Cannot dump file " + filePath + " as the file could not be opened.");
		

		HeaderPartitionPack headerPartitionPack = mxfFile.getHeaderPartition().getPartitionPack();
		HeaderMetadata headerMetadata = mxfFile.getHeaderPartition().readHeaderMetadata();
		
		headerPartitionPack.setThisPartition(thisPartition);
		headerPartitionPack.setPreviousPartition(previousPartition);
		headerPartitionPack.setFooterPartition(0l);
		
		Preface preface = headerMetadata.getPreface();

		if(updatedCore!=null) {
			updatedCore.setLinkedGenerationID(embarcIdent.getIdentification().getLinkedGenerationID());
		}
		
		if(updatedCore!=null) {
			preface.appendIdentification(embarcIdent.getIdentification());
			ContentStorage contentStorage = preface.getContentStorageObject();
			Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
			
			for(tv.amwa.maj.model.Package p : packages) {
				if(p instanceof MaterialPackage) {
					for(Track t : p.getPackageTracks()) {
						if(t instanceof StaticTrack) {
							StaticTrack st = (StaticTrack)t;
							Segment ts = st.getTrackSegment();
							if(ts instanceof Sequence) {
								
								Sequence seq = (Sequence) ts;
								List<? extends Component> components = seq.getComponentObjects();
								
								for(Component c : components) {
									if(c instanceof DescriptiveMarkerImpl) {
										DescriptiveMarker m = (DescriptiveMarker) c;
										try {
											DescriptiveFramework df = m.getDescriptiveFrameworkObject();
											
											if(df!=null && df instanceof AS07CoreDMSFramework) {
												m.setDescriptiveFrameworkObject(updatedCore);
											}	
										}
										catch(PropertyNotPresentException pnp) {
											LOGGER.log(Level.INFO, pnp.toString());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		
		int headerSize = (int) (headerPartitionPack.getEncodedSize());
		headerSize = headerSize < 65536 ? 65546 : headerSize;
		
		ByteArrayOutputStream headerBytes = new ByteArrayOutputStream(headerSize);
		ByteArrayOutputStream metadataBytes = new ByteArrayOutputStream(headerSize);

		MXFStream.writeHeaderMetadata(metadataBytes, preface);

		headerPartitionPack.setHeaderByteCount(metadataBytes.size() + klvFillLength);
		
		MXFStream.writeFill(metadataBytes, klvFillLength);
		
		MXFStream.writePartitionPack(headerBytes, headerPartitionPack);
		
		totalBytes += headerBytes.size();
		totalBytes += metadataBytes.size();
		
		RandomIndexItem hRipItem = new RandomIndexItemImpl(headerPartitionPack.getBodySID(), thisPartition);
		ripItems.add(hRipItem);
		
		
		EssencePartition hContainerPartition = (EssencePartition)mxfFile.getPartitionAt(0);
		if(hContainerPartition!=null) {
			for ( EssenceElementImpl element = (EssenceElementImpl) hContainerPartition.readEssenceElement() ;
					element != null ;
					element = (EssenceElementImpl) hContainerPartition.readEssenceElement() ) {
				
				
				ByteBuffer bb = element.getData();
				byte[] elementByteArray = bb.array();
				
				ByteArrayOutputStream elementBytes = new ByteArrayOutputStream(elementByteArray.length);
				
				MXFStream.writeEssenceElement(elementBytes, element.getEssenceTrackIdentifier(), bb.array());
				totalBytes += elementBytes.size();
			    
			}
		}
		
		for ( int x = 1 ; x < mxfFile.countPartitions()-1; x++ ) {
			BodyPartition originalPartition = (BodyPartition)mxfFile.getPartitionAt(x);
			BodyPartition partition = originalPartition.clone();
			if(partition!=null) {
				
				if (partition instanceof FooterPartitionImpl) continue;
				if (partition instanceof HeaderPartitionImpl) continue;
				previousPartition = thisPartition;
				thisPartition = totalBytes;
				
				ByteArrayOutputStream partitionBytes;
				BodyPartitionPack opp = originalPartition.getPartitionPack().clone();
				BodyPartitionPack partitionPack = partition.getPartitionPack().clone();
				
				if (partitionPack instanceof GenericStreamPartitionPackImpl) {
					GenericStreamPartitionPackImpl gsp = (GenericStreamPartitionPackImpl) partitionPack;

					gsp.setThisPartition(thisPartition);
					gsp.setPreviousPartition(previousPartition);
					gsp.setFooterPartition(0l);
					RandomIndexItem ripItem = new RandomIndexItemImpl(gsp.getBodySID(), thisPartition);
					ripItems.add(ripItem);
					
					int partitionSize = (int) gsp.getEncodedSize();
					partitionSize = partitionSize < 65536 ? 65546 : partitionSize;
					
					partitionBytes = new ByteArrayOutputStream(partitionSize);

					MXFStream.writePartitionPack(partitionBytes, gsp);
					try {
						IndexTableSegment it = partition.readIndexTableSegment();
						if(it!=null) {
							MXFStream.writeIndexTableSegment(partitionBytes, it);
						}
					} catch(Exception ex) {
						LOGGER.log(Level.WARNING, ex.toString(), ex);
					}
					
					long bytesAdded = ReadGenericStream(opp.getThisPartition() + opp.getEncodedSize() + 20, partitionBytes);
					
				}
				else {
					partitionPack.setThisPartition(thisPartition);
					partitionPack.setPreviousPartition(previousPartition);
					partitionPack.setFooterPartition(0l);
					RandomIndexItem ripItem = new RandomIndexItemImpl(partitionPack.getBodySID(), thisPartition);
					ripItems.add(ripItem);
					
					int partitionSize = (int) partitionPack.getEncodedSize();
					partitionSize = partitionSize < 65536 ? 65546 : partitionSize;
					
					partitionBytes = new ByteArrayOutputStream(partitionSize);
					

					MXFStream.writePartitionPack(partitionBytes, partitionPack);
					
					try {
						IndexTableSegment it = partition.readIndexTableSegment();
						if(it!=null) {
							MXFStream.writeIndexTableSegment(partitionBytes, it);
						}
					} catch(Exception ex) {
						LOGGER.log(Level.WARNING, ex.toString(), ex);
						ex.printStackTrace();
					}
					
				}
				
				totalBytes += partitionBytes.size();
									
				EssencePartition containerPartition = (EssencePartition) originalPartition;
				if(containerPartition!=null) {
					for ( EssenceElementImpl element = (EssenceElementImpl) containerPartition.readEssenceElement() ;
							element != null ;
							element = (EssenceElementImpl) containerPartition.readEssenceElement() ) {
						
						
						ByteBuffer bb = element.getData();
						byte[] elementByteArray = bb.array();
						
						ByteArrayOutputStream elementBytes = new ByteArrayOutputStream(elementByteArray.length);
						
						MXFStream.writeEssenceElement(elementBytes, element.getEssenceTrackIdentifier(), bb.array());
						totalBytes += elementBytes.size();
					    
					}
				}
			}
		}
		return totalBytes;
				
	}
	public Preface getPreface() {
		MXFFile mxfFile = getFile();
		HeaderMetadata headerMetadata = mxfFile.getHeaderPartition().readHeaderMetadata();
		
		return headerMetadata.getPreface();
	}
	public MXFFileWriteResult writeFile(String outputFilePath, HashMap<MXFColumn, MetadataColumnDef> coreColumns) throws IOException {
		MXFFileWriteResult result = new MXFFileWriteResult();
		try {
			AS07CoreDMSFramework dms = new AS07CoreDMSFrameworkImpl();
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_ShimName)) {
				dms.setShimName(coreColumns.get(MXFColumn.AS_07_Core_DMS_ShimName).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationName)) {
				dms.setResponsibleOrganizationName(coreColumns.get(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationName).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationCode)) {
				dms.setResponsibleOrganizationCode(coreColumns.get(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationCode).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_PictureFormat)) {
				dms.setPictureFormat(coreColumns.get(MXFColumn.AS_07_Core_DMS_PictureFormat).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_Captions)) {
				dms.setCaptions(coreColumns.get(MXFColumn.AS_07_Core_DMS_Captions).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_IntendedAFD)) {
				dms.setIntendedAFD(coreColumns.get(MXFColumn.AS_07_Core_DMS_IntendedAFD).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_AudioTrackLayout)) {
				String value = coreColumns.get(MXFColumn.AS_07_Core_DMS_AudioTrackLayout).toString();
				AudioTrackLayoutValues atlv = new AudioTrackLayoutValues();
				AUID i = AUIDImpl.parseFactory(atlv.getUl(value));
				dms.setAudioTrackLayout(i);
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_AudioTrackLayoutComment)) {
				dms.setAudioTrackLayoutComment(coreColumns.get(MXFColumn.AS_07_Core_DMS_AudioTrackLayoutComment).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_AudioTrackSecondaryLanguage)) {
				dms.setAudioTrackSecondaryLanguage(coreColumns.get(MXFColumn.AS_07_Core_DMS_AudioTrackSecondaryLanguage).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_AudioTrackPrimaryLanguage)) {
				dms.setAudioTrackPrimaryLanguage(coreColumns.get(MXFColumn.AS_07_Core_DMS_AudioTrackPrimaryLanguage).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_NatureOfOrganization)) {
				dms.setNatureOfOrganization(coreColumns.get(MXFColumn.AS_07_Core_DMS_NatureOfOrganization).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_SecondaryTitle)) {
				dms.setSecondaryTitle(coreColumns.get(MXFColumn.AS_07_Core_DMS_SecondaryTitle).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_WorkingTitle)) {
				dms.setWorkingTitle(coreColumns.get(MXFColumn.AS_07_Core_DMS_WorkingTitle).toString());
			}
			if(coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_Identifiers)) {
				IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
				dms.setIdentifiers(idSetHelper.createIdentifierListFromString(coreColumns.get(MXFColumn.AS_07_Core_DMS_Identifiers).toString()));
			}
			if (coreColumns.containsKey(MXFColumn.AS_07_Core_DMS_Devices)) {
				DeviceSetHelper deviceSetHelper = new DeviceSetHelper();
				dms.setDevices(deviceSetHelper.createDeviceListFromString(coreColumns.get(MXFColumn.AS_07_Core_DMS_Devices).toString()));
			}

			return writeFile(outputFilePath, dms);
		} catch(Exception ex) {
			LOGGER.log(Level.WARNING, ex.toString(), ex);
			ex.printStackTrace();
			result.setException(ex);
			result.setSuccess(false);
			return result;
		}
	}

	public MXFFileWriteResult writeFile(String outputFilePath, AS07CoreDMSFramework updatedCore) throws IOException {
		MXFFileWriteResult result = new MXFFileWriteResult();
		try {
			long footerOffset = getFooterOffset(updatedCore);
			EmbARCIdentification embarcIdent = getIdentification();
			MXFFile mxfFile = MXFFactory.readPartitions(filePath);
					
			long totalBytes = 0;
			long thisPartition = 0;
			long previousPartition = 0;
			long klvFillLength = 8192;
			ArrayList<RandomIndexItem> ripItems = new ArrayList<RandomIndexItem>();
			String tempFilePath = outputFilePath + ".tmp";
			try(OutputStream outputStream = new FileOutputStream(tempFilePath)) {
				if (!mxfFile.isOpen())
					throw new IOException("Cannot dump file " + filePath + " as the file could not be opened.");
				
				if(updatedCore!=null) {
					updatedCore.setLinkedGenerationID(embarcIdent.getIdentification().getLinkedGenerationID());
				}
		
				HeaderPartitionPack headerPartitionPack = mxfFile.getHeaderPartition().getPartitionPack();
				HeaderMetadata headerMetadata = mxfFile.getHeaderPartition().readHeaderMetadata();
				
				headerPartitionPack.setThisPartition(thisPartition);
				headerPartitionPack.setPreviousPartition(previousPartition);
				headerPartitionPack.setFooterPartition(footerOffset);
				
				Preface preface = headerMetadata.getPreface();
				
				if(updatedCore!=null) {
					preface.appendIdentification(embarcIdent.getIdentification());
					ContentStorage contentStorage = preface.getContentStorageObject();
					Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
					
					for(tv.amwa.maj.model.Package p : packages) {
						if(p instanceof MaterialPackage) {
							for(Track t : p.getPackageTracks()) {
								if(t instanceof StaticTrack) {
									StaticTrack st = (StaticTrack)t;
									Segment ts = st.getTrackSegment();
									if(ts instanceof Sequence) {
										
										Sequence seq = (Sequence) ts;
										List<? extends Component> components = seq.getComponentObjects();
										
										for(Component c : components) {
											if(c instanceof DescriptiveMarkerImpl) {
												DescriptiveMarker m = (DescriptiveMarker) c;
												try {
													DescriptiveFramework df = m.getDescriptiveFrameworkObject();
													
													if(df!=null && df instanceof AS07CoreDMSFramework) {
														m.setDescriptiveFrameworkObject(updatedCore);
													}	
												}
												catch(PropertyNotPresentException pnp) {
													LOGGER.log(Level.INFO, pnp.toString());
												}
											}
										}
									}
								}
							}
						}
					}
				}
				
				int headerSize = (int) (headerPartitionPack.getEncodedSize());
				headerSize = headerSize < 65536 ? 65546 : headerSize;
				
				ByteArrayOutputStream headerBytes = new ByteArrayOutputStream(headerSize);
				ByteArrayOutputStream metadataBytes = new ByteArrayOutputStream(headerSize);
	
				
				
				MXFStream.writeHeaderMetadata(metadataBytes, preface);
	
				headerPartitionPack.setHeaderByteCount(metadataBytes.size() + klvFillLength);
				
				MXFStream.writeFill(metadataBytes, klvFillLength);
				
				MXFStream.writePartitionPack(headerBytes, headerPartitionPack);
				
				
				headerBytes.writeTo(outputStream);
				metadataBytes.writeTo(outputStream);
				totalBytes += headerBytes.size();
				totalBytes += metadataBytes.size();
				
				RandomIndexItem hRipItem = new RandomIndexItemImpl(headerPartitionPack.getBodySID(), thisPartition);
				ripItems.add(hRipItem);
				
				EssencePartition hContainerPartition = (EssencePartition)mxfFile.getPartitionAt(0);
				if(hContainerPartition!=null) {
					for ( EssenceElementImpl element = (EssenceElementImpl) hContainerPartition.readEssenceElement() ;
							element != null ;
							element = (EssenceElementImpl) hContainerPartition.readEssenceElement() ) {
						
						
	
						ByteBuffer bb = element.getData();
						byte[] elementByteArray = bb.array();
						
						ByteArrayOutputStream elementBytes = new ByteArrayOutputStream(elementByteArray.length);
						
						MXFStream.writeEssenceElement(elementBytes, element.getEssenceTrackIdentifier(), bb.array());
						elementBytes.writeTo(outputStream);
						totalBytes += elementBytes.size();
					    
					}
				}
				
				int essenceCount = 0;
				for ( int x = 1 ; x < mxfFile.countPartitions()-1; x++ ) {
					BodyPartition originalPartition = (BodyPartition)mxfFile.getPartitionAt(x);
					BodyPartition partition = originalPartition.clone();
					if(partition!=null) {
						
						if (partition instanceof FooterPartitionImpl) continue;
						if (partition instanceof HeaderPartitionImpl) continue;
						previousPartition = thisPartition;
						thisPartition = totalBytes;
						
						ByteArrayOutputStream partitionBytes;
						BodyPartitionPack opp = originalPartition.getPartitionPack().clone();
						BodyPartitionPack partitionPack = partition.getPartitionPack().clone();
						
						if (partitionPack instanceof GenericStreamPartitionPackImpl) {
							GenericStreamPartitionPackImpl gsp = (GenericStreamPartitionPackImpl) partitionPack;
	
							gsp.setThisPartition(thisPartition);
							gsp.setPreviousPartition(previousPartition);
							gsp.setFooterPartition(footerOffset);
							RandomIndexItem ripItem = new RandomIndexItemImpl(gsp.getBodySID(), thisPartition);
							ripItems.add(ripItem);
							
							int partitionSize = (int) gsp.getEncodedSize();
							partitionSize = partitionSize < 65536 ? 65546 : partitionSize;
							
							partitionBytes = new ByteArrayOutputStream(partitionSize);
	
							MXFStream.writePartitionPack(partitionBytes, gsp);
							long bytesAdded = ReadGenericStream(opp.getThisPartition() + opp.getEncodedSize() + 20, partitionBytes);
							
						}
						else {
							partitionPack.setThisPartition(thisPartition);
							partitionPack.setPreviousPartition(previousPartition);
							partitionPack.setFooterPartition(footerOffset);
							RandomIndexItem ripItem = new RandomIndexItemImpl(partitionPack.getBodySID(), thisPartition);
							ripItems.add(ripItem);
							
							int partitionSize = (int) partitionPack.getEncodedSize();
							partitionSize = partitionSize < 65536 ? 65546 : partitionSize;
							
							partitionBytes = new ByteArrayOutputStream(partitionSize);
							
	
							MXFStream.writePartitionPack(partitionBytes, partitionPack);
							
							IndexTableSegment it = partition.readIndexTableSegment();
							if(it!=null) {
								MXFStream.writeIndexTableSegment(partitionBytes, it);
							}
							
						}
											
						partitionBytes.writeTo(outputStream);
						//sids.put(partitionPack.getPreviousPartition(), totalBytes);
						
						totalBytes += partitionBytes.size();
											
						EssencePartition containerPartition = (EssencePartition) originalPartition;
						
						if(containerPartition!=null) {
							essenceCount = essenceCount + 1;
							for ( EssenceElementImpl element = (EssenceElementImpl) containerPartition.readEssenceElement() ;
									element != null ;
									element = (EssenceElementImpl) containerPartition.readEssenceElement() ) {
								
								
								ByteBuffer bb = element.getData();
								byte[] elementByteArray = bb.array();
								
								ByteArrayOutputStream elementBytes = new ByteArrayOutputStream(elementByteArray.length);
								
								MXFStream.writeEssenceElement(elementBytes, element.getEssenceTrackIdentifier(), bb.array());
								elementBytes.writeTo(outputStream);
								totalBytes += elementBytes.size();
							}
						}
						
					}
				}
				System.out.println("Essence Count: " + essenceCount);
				if (mxfFile.getFooterPartition() != null) {
		
					FooterPartition footerPartition = mxfFile.getFooterPartition();
					FooterPartitionPack footerPartitionPack = footerPartition.getPartitionPack();
	
					previousPartition = thisPartition;
					thisPartition = totalBytes;
	
					RandomIndexItem ripItem = new RandomIndexItemImpl(footerPartitionPack.getBodySID(), thisPartition);
					ripItems.add(ripItem);
					
					footerPartitionPack.setThisPartition(thisPartition);
					footerPartitionPack.setPreviousPartition(previousPartition);
					footerPartitionPack.setFooterPartition(thisPartition);
					
					int footerSize = (int) footerPartitionPack.getEncodedSize();
					footerSize = footerSize < 65536 ? 65546 : footerSize;
					ByteArrayOutputStream footerBytes = new ByteArrayOutputStream(footerSize);
					MXFStream.writePartitionPack(footerBytes, footerPartitionPack);
					
					footerBytes.writeTo(outputStream);
				}
				
				RandomIndexPackImpl ripOriginal = (RandomIndexPackImpl)mxfFile.getRandomIndexPack();
				System.out.println(ripOriginal);
				System.out.println("********************************");
				ripOriginal.clear();
	
			    RandomIndexItem list2[] = new RandomIndexItem[ripItems.size()];
				ripOriginal.setPartitionIndex(ripItems.toArray(list2));
				System.out.println(ripOriginal);
				if (ripOriginal != null) {
					MXFStream.writeRandomIndexPack(outputStream, ripOriginal);
				}
	
				Path p = Paths.get(outputFilePath);
				Path p2 = Paths.get(tempFilePath);
				if(Files.exists(p)) Files.delete(p);
				
				Files.copy(p2, Paths.get(outputFilePath));
				
	
				if(Files.exists(p2)) Files.delete(p2);
			}
			catch(Exception ex) {
				LOGGER.log(Level.WARNING, ex.toString(), ex);
				ex.printStackTrace();
				Path p = Paths.get(tempFilePath);
				if(Files.exists(p)) Files.delete(p);
				result.setException(ex);
				result.setSuccess(false);
				return result;
			}
			mxfFile.close();
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, ex.toString(), ex);
			ex.printStackTrace();
			result.setException(ex);
			result.setSuccess(false);
			return result;
		}
		result.setSuccess(true);
		return result;
	}
	
	public long ReadGenericStream(long offset, OutputStream outputStream) {
		// Read length
		InputStream is;
		try {
			is = new FileInputStream(filePath);
			MXFStream.skipForward(is, offset);
			KeyAndConsumed key = MXFStream.readKey(is);
			LengthAndConsumed length = MXFStream.readBERLength(is);
			ByteBuffer bb = MXFStream.readValue(is, length.getLength());
			
			MXFStream.writeKey(outputStream, key.getKey());
			MXFStream.writeBERLength(outputStream, length.getLength(), (int)length.getConsumed());
			MXFStream.writeValue(outputStream, bb);
			
			return key.getConsumed() + length.getConsumed() + length.getLength();
			
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);
			e.printStackTrace();
		}
		return 0;
	}
	@Override
	public boolean DownloadGenericStream(int streamId, String outputFile) {
		ByteBuffer bb = GetGenericStream(streamId);
		Assert.assertNotNull(bb);
		// TODO:  Figure out extension type
		try {
			File file = new File(outputFile);
			FileChannel channel = new FileOutputStream(file, false).getChannel();
        
			channel.write(bb);
	        // close the channel
	        channel.close();
	        return true;
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public ByteBuffer GetGenericStream(int streamId) {

		MXFFile mxfFile = MXFFactory.readPartitions(filePath);
		
		for ( int x = 1 ; x < mxfFile.countPartitions()-1; x++ ) {
			BodyPartition originalPartition = (BodyPartition)mxfFile.getPartitionAt(x);
			BodyPartition partition = originalPartition.clone();
			if(partition!=null) {
				
				if (partition instanceof FooterPartitionImpl) continue;
				if (partition instanceof HeaderPartitionImpl) continue;
				
				ByteArrayOutputStream partitionBytes;
				BodyPartitionPack opp = originalPartition.getPartitionPack().clone();
				if(opp.getBodySID()==streamId) {
					ByteBuffer bb = GetGenericStream(opp.getThisPartition() + opp.getEncodedSize() + 20);
					return bb;
				}
			}
		}
		return null;
	}
	private ByteBuffer GetGenericStream(long offset) {
		// Read length
		InputStream is;
		try {
			is = new FileInputStream(filePath);
			MXFStream.skipForward(is, offset);
			KeyAndConsumed key = MXFStream.readKey(is);
			LengthAndConsumed length = MXFStream.readBERLength(is);
			ByteBuffer bb = MXFStream.readValue(is, length.getLength());
			
			return bb;		
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);
			e.printStackTrace();
		}
		return null;
	}
	private void addContainersForDescriptor(
			AAFFileDescriptor descriptor) {
		
		if (descriptor instanceof MultipleDescriptor) {
			for ( AAFFileDescriptor nestedDescriptor : ((MultipleDescriptor) descriptor).getFileDescriptors() ) {
				addContainersForDescriptor(nestedDescriptor);
			}
		}
		else
			fileDescriptors.add(descriptor);
	}

	List<AAFFileDescriptor> fileDescriptors = new ArrayList<AAFFileDescriptor>();
	public MXFFileDescriptorResult getDescriptors() {
		MXFFileDescriptorResult result = new MXFFileDescriptorResult();
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		
		Set<? extends tv.amwa.maj.model.Package> packages = preface.getPackages();
		
		for ( tv.amwa.maj.model.Package packageItem : packages ) {
			if (packageItem instanceof tv.amwa.maj.model.SourcePackage) {
				tv.amwa.maj.model.EssenceDescriptor packageDescriptor =
					((tv.amwa.maj.model.SourcePackage) packageItem).getEssenceDescription();
				if (packageDescriptor instanceof AAFFileDescriptor) 
					//fileDescriptors.add((AAFFileDescriptor)packageDescriptor);
					addContainersForDescriptor((AAFFileDescriptor) packageDescriptor);
			}
		}
		
		for(AAFFileDescriptor fd : fileDescriptors) {
			
			if(fd instanceof CDCIDescriptorImpl) {
				CDCIDescriptorImpl cdci = (CDCIDescriptorImpl) fd;
				result.addCDCIDescriptor(cdci);
			}
			else if (fd instanceof WAVEPCMDescriptorImpl) {
				WAVEPCMDescriptorImpl wave = (WAVEPCMDescriptorImpl) fd;
				result.addWavePCMDescriptors(wave);
				
			}
			else if (fd instanceof TimedTextDescriptorImpl) {
				TimedTextDescriptorImpl desc = (TimedTextDescriptorImpl) fd;
				result.addTimedTextDescriptor(desc);
			}
			else if (fd instanceof AncillaryPacketsDescriptorImpl) {
				AncillaryPacketsDescriptorImpl desc = (AncillaryPacketsDescriptorImpl) fd;
				result.addAncillaryPacketsDescriptors(desc);
			}
			else if (fd instanceof AS07DateTimeDescriptorImpl) {
				AS07DateTimeDescriptorImpl desc = (AS07DateTimeDescriptorImpl) fd;
				result.addAS07DateTimeDescriptor(desc);
			}
			else if (fd instanceof RGBADescriptorImpl) {
				RGBADescriptorImpl desc = (RGBADescriptorImpl) fd;
				result.addRGBADescriptor(desc);
			}
			else if (fd instanceof STLDescriptorImpl) {
				STLDescriptorImpl desc = (STLDescriptorImpl) fd;
				result.addSTLDescriptor(desc);
			}
			else if (fd instanceof VBIDescriptorImpl) {
				VBIDescriptorImpl desc = (VBIDescriptorImpl) fd;
				result.addVBIDescriptor(desc);
			}
			else if (fd instanceof PictureDescriptorImpl) {
				PictureDescriptorImpl desc = (PictureDescriptorImpl) fd;
				result.addPictureDescriptor(desc);
			}
			else if (fd instanceof SoundDescriptorImpl) {
				SoundDescriptorImpl desc = (SoundDescriptorImpl) fd;
				result.addSoundDescriptor(desc);
			}
		}
		return result;
	}
	public List<AS07GspTdDMSFrameworkImpl> getAS07GspTdDMSFramework() {
		List<AS07GspTdDMSFrameworkImpl> toReturn = new ArrayList<AS07GspTdDMSFrameworkImpl>();
		if(file==null) {
			file = MXFFactory.readPartitions(filePath);
		}
		
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		ContentStorage contentStorage = preface.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
		
		for(tv.amwa.maj.model.Package p : packages) {
			if(p instanceof MaterialPackage) {
				for(Track t : p.getPackageTracks()) {
					if(t instanceof StaticTrack) {
						StaticTrack st = (StaticTrack)t;
						Segment ts = st.getTrackSegment();
						if(ts instanceof Sequence) {
							
							Sequence seq = (Sequence) ts;
							List<? extends Component> components = seq.getComponentObjects();
							
							for(Component c : components) {
								if(c instanceof DescriptiveMarkerImpl) {
									DescriptiveMarker m = (DescriptiveMarker) c;
									try {
										DescriptiveFramework df = m.getDescriptiveFrameworkObject();

										if(df!=null && df instanceof AS07GspTdDMSFrameworkImpl) {
											toReturn.add((AS07GspTdDMSFrameworkImpl) df);
										}	
									}
									catch(PropertyNotPresentException pnp) {
										LOGGER.log(Level.INFO, pnp.toString());
									}
								}
							}
						}
					}
				}
			}
		}
		
		return toReturn;
	}
	
	public List<AS07GSPDMSObject> getAS07GSPDMSObjects() {
		List<AS07GSPDMSObject> toReturn = new ArrayList<AS07GSPDMSObject>();
		if(file==null) {
			file = MXFFactory.readPartitions(filePath);
		}
		
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		ContentStorage contentStorage = preface.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
		
		for(tv.amwa.maj.model.Package p : packages) {
			if(p instanceof MaterialPackage) {
				for(Track t : p.getPackageTracks()) {
					if(t instanceof StaticTrack) {
						StaticTrack st = (StaticTrack)t;
						Segment ts = st.getTrackSegment();
						if(ts instanceof Sequence) {
							
							Sequence seq = (Sequence) ts;
							List<? extends Component> components = seq.getComponentObjects();
							
							for(Component c : components) {
								if(c instanceof DescriptiveMarkerImpl) {
									DescriptiveMarker m = (DescriptiveMarker) c;
									try {
										DescriptiveFramework df = m.getDescriptiveFrameworkObject();
										if(df!=null && df instanceof AS07GspTdDMSFrameworkImpl) {
											AS07GSPDMSObject obj = ((AS07GspTdDMSFrameworkImpl) df).getTextBasedObject();
											toReturn.add(obj);
										}	
									}
									catch(PropertyNotPresentException pnp) {
										LOGGER.log(Level.INFO, pnp.toString());
									}
								}
							}
						}
					}
				}
			}
		}
		
		return toReturn;
	}
	public List<AS07GspBdDMSFrameworkImpl> getAS07GspBdDMSFramework() {
		List<AS07GspBdDMSFrameworkImpl> toReturn = new ArrayList<AS07GspBdDMSFrameworkImpl>();
		if(file==null) {
			file = MXFFactory.readPartitions(filePath);
		}
		
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		ContentStorage contentStorage = preface.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
		
		for(tv.amwa.maj.model.Package p : packages) {
			if(p instanceof MaterialPackage) {
				for(Track t : p.getPackageTracks()) {
					if(t instanceof StaticTrack) {
						StaticTrack st = (StaticTrack)t;
						Segment ts = st.getTrackSegment();
						if(ts instanceof Sequence) {
							
							Sequence seq = (Sequence) ts;
							List<? extends Component> components = seq.getComponentObjects();
							
							for(Component c : components) {
								if(c instanceof DescriptiveMarkerImpl) {
									DescriptiveMarker m = (DescriptiveMarker) c;
									try {
										DescriptiveFramework df = m.getDescriptiveFrameworkObject();
										if(df!=null && df instanceof AS07GspBdDMSFrameworkImpl) {
											toReturn.add((AS07GspBdDMSFrameworkImpl) df);
										}	
									}
									catch(PropertyNotPresentException pnp) {
										LOGGER.log(Level.INFO, pnp.toString());
									}
								}
							}
						}
					}
				}
			}
		}
		
		return toReturn;
	}
	public AS07CoreDMSFramework getAS07CoreDMSFramework() {
		getFile();
		
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		ContentStorage contentStorage = preface.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
		
		for(tv.amwa.maj.model.Package p : packages) {
			if(p instanceof MaterialPackage) {
				for(Track t : p.getPackageTracks()) {
					if(t instanceof StaticTrack) {
						StaticTrack st = (StaticTrack)t;
						Segment ts = st.getTrackSegment();
						if(ts instanceof Sequence) {
							
							Sequence seq = (Sequence) ts;
							List<? extends Component> components = seq.getComponentObjects();
							
							for(Component c : components) {
								if(c instanceof DescriptiveMarkerImpl) {
									DescriptiveMarker m = (DescriptiveMarker) c;
									try {
										DescriptiveFramework df = m.getDescriptiveFrameworkObject();
										if(df!=null && df instanceof AS07CoreDMSFramework) {
											return (AS07CoreDMSFramework) df;
										}	
									}
									catch(PropertyNotPresentException pnp) {
										LOGGER.log(Level.INFO, pnp.toString());
									}
								}
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	public void setAS07CoreDMSFramework(AS07CoreDMSFramework dms) {
		getFile();
		
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		ContentStorage contentStorage = preface.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
		
		for(tv.amwa.maj.model.Package p : packages) {
			if(p instanceof MaterialPackage) {
				for(Track t : p.getPackageTracks()) {
					if(t instanceof StaticTrack) {
						StaticTrack st = (StaticTrack)t;
						Segment ts = st.getTrackSegment();
						if(ts instanceof Sequence) {
							
							Sequence seq = (Sequence) ts;
							List<? extends Component> components = seq.getComponentObjects();
							
							for(Component c : components) {
								if(c instanceof DescriptiveMarkerImpl) {
									DescriptiveMarker m = (DescriptiveMarker) c;
									try {
										DescriptiveFramework df = m.getDescriptiveFrameworkObject();
										
										if(df!=null && df instanceof AS07CoreDMSFramework) {
											m.setDescriptiveFrameworkObject(dms);
										}	
									}
									catch(PropertyNotPresentException pnp) {
										LOGGER.log(Level.INFO, pnp.toString());
									}
								}
							}
						}
					}
				}
			}
		}
		
	}
	public boolean hasAS07CoreDMSFramework() {
		if(file==null) {
			file = MXFFactory.readPartitions(filePath);
		}
		
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		ContentStorage contentStorage = preface.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
		
		for(tv.amwa.maj.model.Package p : packages) {
			if(p instanceof MaterialPackage) {
				for(Track t : p.getPackageTracks()) {
					
					if(t instanceof StaticTrack) {
						StaticTrack st = (StaticTrack)t;
						Segment ts = st.getTrackSegment();
						if(ts instanceof Sequence) {
							
							Sequence seq = (Sequence) ts;
							List<? extends Component> components = seq.getComponentObjects();
							
							for(Component c : components) {
								if(c instanceof DescriptiveMarkerImpl) {
									DescriptiveMarker m = (DescriptiveMarker) c;
									try {
										DescriptiveFramework df = m.getDescriptiveFrameworkObject();
										if(df!=null && df instanceof AS07CoreDMSFramework) {
											return true;
										}	
									}
									catch(PropertyNotPresentException pnp) {
										LOGGER.log(Level.INFO, pnp.toString());
									}
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	public FileInformation<MXFMetadata> getMetadata(){
		File tf = new File(filePath);
		if(file==null) {
			file = MXFFactory.readPartitions(filePath);
		}
		
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		
		
		MXFMetadata metadata = new MXFMetadata();
		MXFFileDescriptorResult descriptors = getDescriptors();
		
		metadata.setFileDescriptors(descriptors);

		int otherTrackCount = 0;
		otherTrackCount += descriptors.getAncillaryPacketsDescriptors().size();
		otherTrackCount += descriptors.getTimedTextDescriptor().size();
		otherTrackCount += descriptors.getAS07DateTimeDescriptor().size();
		metadata.setOtherTrackCount(otherTrackCount);

		metadata.setSoundTrackCount(getSoundCount());
		metadata.setPictureTrackCount(getPictureCount());
		
		metadata.setFileSize(tf.length());
		metadata.setFormat("MXF");
		// TODO: Populate this with value
		metadata.setProfile(preface.getOperationalPattern().toString());
		
		metadata.setVersion(preface.getFormatVersion().toString());

		HashMap<MXFColumn, MetadataColumnDef> columns = new LinkedHashMap<MXFColumn, MetadataColumnDef>();
		AS07CoreDMSFramework core = getAS07CoreDMSFramework();		
		if(core==null) {
			core = new AS07CoreDMSFrameworkImpl();
		}
		IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
		String identifiers = idSetHelper.identifiersToString(core.getIdentifiers());

		String devices = "";
		try {
			DeviceSetHelper deviceSetHelper = new DeviceSetHelper();
			List<AS07CoreDMSDeviceObjectsImpl> devicesList = core.getDevices();
			if (devicesList != null) {
				devices = deviceSetHelper.devicesToString(devicesList);
			}
		} catch(PropertyNotPresentException ex) {
			// optional field, ignore PNPE
		}

		String atlValue = "";
		if(core.getAudioTrackLayout()!=null) {
			AUID audioTrackLayout = core.getAudioTrackLayout();
			String atl = audioTrackLayout.toString().replace("urn:smpte:ul:", "");
			AudioTrackLayoutValues atlv = new AudioTrackLayoutValues();
			atlValue = atlv.getDescription(atl);
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_AudioTrackLayout,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_AudioTrackLayout, atlValue));
		} catch(Exception ex) {
			// required field, log PNPE Info
			LOGGER.log(Level.INFO, "AS_07_Core_DMS_AudioTrackLayout Property Not Present");
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_AudioTrackLayoutComment,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_AudioTrackLayoutComment, core.getAudioTrackLayoutComment()));
		} catch(Exception ex) {
			// optional field, ignore PNPE
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_AudioTrackPrimaryLanguage, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_AudioTrackPrimaryLanguage, core.getAudioTrackPrimaryLanguage()));
		}catch(PropertyNotPresentException ex) {
			// optional field, ignore PNPE
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_AudioTrackSecondaryLanguage,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_AudioTrackSecondaryLanguage, core.getAudioTrackSecondaryLanguage()));
		}catch(PropertyNotPresentException ex) {
			// optional field, ignore PNPE
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_Captions,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_Captions, core.getCaptions()));
		}catch(PropertyNotPresentException ex) {
			// required field, log PNPE Info
			LOGGER.log(Level.INFO, "AS_07_Core_DMS_Captions Property Not Present");
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_Devices, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_Devices, devices));
		}catch(PropertyNotPresentException ex) {
			// optional field, ignore PNPE
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_Identifiers, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_Identifiers, identifiers));
		}catch(PropertyNotPresentException ex) {
			// required field, log PNPE Info
			LOGGER.log(Level.INFO, "AS_07_Core_DMS_Identifiers Property Not Present");
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_IntendedAFD,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_IntendedAFD, core.getIntendedAFD()));
		}catch(PropertyNotPresentException ex) {
			// required field, log PNPE Info
			LOGGER.log(Level.INFO, "AS_07_Core_DMS_IntendedAFD Property Not Present");
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_NatureOfOrganization,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_NatureOfOrganization, core.getNatureOfOrganization()));
		}catch(PropertyNotPresentException ex) {
			// optional field, ignore PNPE
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_PictureFormat,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_PictureFormat, core.getPictureFormat()));
		}catch(PropertyNotPresentException ex) {
			// required field, log PNPE Info
			LOGGER.log(Level.INFO, "AS_07_Core_DMS_PictureFormat Property Not Present");
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationCode, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationCode, core.getResponsibleOrganizationCode()));
		}catch(PropertyNotPresentException ex) {
			// optional field, ignore PNPE
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationName, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationName, core.getResponsibleOrganizationName()));
		}catch(PropertyNotPresentException ex) {
			// required field, log PNPE Info
			LOGGER.log(Level.INFO, "AS_07_Core_DMS_ResponsibleOrganizationName Property Not Present");
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_SecondaryTitle, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_SecondaryTitle, core.getSecondaryTitle()));
		}catch(PropertyNotPresentException ex) {
			// optional field, ignore PNPE
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_ShimName, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_ShimName, core.getShimName()));
		}catch(PropertyNotPresentException ex) {
			// required field, log PNPE Info
			LOGGER.log(Level.INFO, "AS_07_Core_DMS_ShimName Property Not Present");
		}

		try {
			columns.put(MXFColumn.AS_07_Core_DMS_WorkingTitle, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_WorkingTitle, core.getWorkingTitle()));
		}catch(PropertyNotPresentException ex) {
			// optional field, ignore PNPE
		}

		metadata.setCoreColumns(columns);
		
		List<AS07GspBdDMSFrameworkImpl> bds = this.getAS07GspBdDMSFramework();

		HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> bdColumns = new HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>>();
		
		for(AS07GspBdDMSFrameworkImpl bd : bds) {
			LinkedHashMap<MXFColumn, MetadataColumnDef> cols = new LinkedHashMap<MXFColumn, MetadataColumnDef>();
			
			AS07GSPDMSObject textBasedObject = bd.getTextBasedObject();
			
			List<AS07DMSIdentifierSetImpl> identSet = textBasedObject.getIdentifiers();
//			String objIdentifiers = identSet == null ? "" : StringUtils.join(identSet, ',');
			String objIdentifiers = idSetHelper.identifiersToString(identSet);
			cols.put(MXFColumn.AS_07_Object_Identifiers, new StringMetadataColumn(MXFColumn.AS_07_Object_Identifiers, objIdentifiers));	
			
			try {cols.put(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, new StringMetadataColumn(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, textBasedObject.getTextBasedMetadataPayloadSchemeID().toString()));}catch(PropertyNotPresentException ex) {}

			try {cols.put(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, new StringMetadataColumn(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, textBasedObject.getRfc5646TextLanguageCode()));}catch(PropertyNotPresentException ex) {}
			try {cols.put(MXFColumn.AS_07_Object_MIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_MIMEMediaType, textBasedObject.getMimeMediaType()));}catch(PropertyNotPresentException ex) {}
			try {cols.put(MXFColumn.AS_07_Object_TextMIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_TextMIMEMediaType, textBasedObject.getTextMimeMediaType()));}catch(PropertyNotPresentException ex) {}
			try {cols.put(MXFColumn.AS_07_Object_DataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_DataDescription, textBasedObject.getDataDescriptions()));}catch(PropertyNotPresentException ex) {}
			try {cols.put(MXFColumn.AS_07_Object_TextDataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_TextDataDescription, textBasedObject.getTextDataDescriptions()));}catch(PropertyNotPresentException ex) {}
			try {cols.put(MXFColumn.AS_07_Object_Note, new StringMetadataColumn(MXFColumn.AS_07_Object_Note, textBasedObject.getNote()));}catch(PropertyNotPresentException ex) {}
			try {cols.put(MXFColumn.AS_07_Object_GenericStreamID, new StringMetadataColumn(MXFColumn.AS_07_Object_GenericStreamID, Integer.toString(textBasedObject.getGenericStreamId())));}catch(PropertyNotPresentException ex) {}

			try {bdColumns.put(Integer.toString(textBasedObject.getGenericStreamId()), cols);}catch(PropertyNotPresentException ex) {}
		}
		metadata.setBDCount(bdColumns.size());
		metadata.setBDColumns(bdColumns);
		try {
			List<AS07GspTdDMSFrameworkImpl> tds = this.getAS07GspTdDMSFramework();
		
			HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> tdColumns = new HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>>();
			for(AS07GspTdDMSFrameworkImpl td : tds) {
				LinkedHashMap<MXFColumn, MetadataColumnDef> cols = new LinkedHashMap<MXFColumn, MetadataColumnDef>();
				try {cols.put(MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode, new StringMetadataColumn(MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode, td.getPrimaryRFC5646LanguageCode()));}catch(PropertyNotPresentException ex) {}
				
				AS07GSPDMSObject textBasedObject = td.getTextBasedObject();
				
				List<AS07DMSIdentifierSetImpl> identSet = textBasedObject.getIdentifiers();
//				String objIdentifiers = StringUtils.join(identSet, ',');
				String objIdentifiers = idSetHelper.identifiersToString(identSet);
				
				try {cols.put(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, new StringMetadataColumn(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, textBasedObject.getTextBasedMetadataPayloadSchemeID().toString()));}catch(PropertyNotPresentException ex) {}
				try {cols.put(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, new StringMetadataColumn(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, textBasedObject.getRfc5646TextLanguageCode()));}catch(PropertyNotPresentException ex) {}
				try {cols.put(MXFColumn.AS_07_Object_MIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_MIMEMediaType, textBasedObject.getMimeMediaType()));}catch(PropertyNotPresentException ex) {}
				try {cols.put(MXFColumn.AS_07_Object_TextMIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_TextMIMEMediaType, textBasedObject.getTextMimeMediaType()));}catch(PropertyNotPresentException ex) {}
				try {cols.put(MXFColumn.AS_07_Object_DataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_DataDescription, textBasedObject.getDataDescriptions()));}catch(PropertyNotPresentException ex) {}
				try {cols.put(MXFColumn.AS_07_Object_TextDataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_TextDataDescription, textBasedObject.getTextDataDescriptions()));}catch(PropertyNotPresentException ex) {}
				try {cols.put(MXFColumn.AS_07_Object_Note, new StringMetadataColumn(MXFColumn.AS_07_Object_Note, textBasedObject.getNote()));}catch(PropertyNotPresentException ex) {}
				try {cols.put(MXFColumn.AS_07_Object_GenericStreamID, new StringMetadataColumn(MXFColumn.AS_07_Object_GenericStreamID, Integer.toString(textBasedObject.getGenericStreamId())));}catch(PropertyNotPresentException ex) {}
				try {cols.put(MXFColumn.AS_07_Object_Identifiers, new StringMetadataColumn(MXFColumn.AS_07_Object_Identifiers, objIdentifiers));}catch(PropertyNotPresentException ex) {}

				try {tdColumns.put(Integer.toString(textBasedObject.getGenericStreamId()), cols);}catch(PropertyNotPresentException ex) {}
			}
			metadata.setTDCount(tdColumns.size());
			metadata.setTDColumns(tdColumns);
		} catch(PropertyNotPresentException ex) {
			LOGGER.log(Level.WARNING, ex.toString(), ex);
		}
		FileInformation<MXFMetadata> fileInformation = new FileInformation<MXFMetadata>();
		
		fileInformation.setName(tf.getName());
		fileInformation.setPath(filePath);

//		byte[] imageData;
//		try {
//			imageData = DPXFileListHelper.getBytesFromFile(filePath, 0);
//			// Get the CRC32 of the image
//			final String hash = DPXFileListHelper.getCrc32Hash(imageData);
//			fileInformation.setHash(hash);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		metadata.setHasAS07CoreDMSFramework(this.hasAS07CoreDMSFramework());
		fileInformation.setFileData(metadata);

		return fileInformation;
	}
	public int getPictureCount() {
		int count = 0;
		if(file==null) {
			file = MXFFactory.readPartitions(filePath);
		}
		
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		ContentStorage contentStorage = preface.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
		
		for(tv.amwa.maj.model.Package p : packages) {
			if(p instanceof MaterialPackage) {
				for(Track t : p.getPackageTracks()) {
					if(t instanceof TimelineTrack) {
						TimelineTrack st = (TimelineTrack)t;
					
						Segment ts = st.getTrackSegment();
						
						DataDefinition d = ts.getComponentDataDefinition();
						if(d.isPictureKind()) count++;
						
					}
				}
			}
			
		}
		return count;
	}
	public int getSoundCount() {
		int count = 0;
		if(file==null) {
			file = MXFFactory.readPartitions(filePath);
		}
		
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		ContentStorage contentStorage = preface.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
		
		for(tv.amwa.maj.model.Package p : packages) {
			if(p instanceof MaterialPackage) {
				for(Track t : p.getPackageTracks()) {
					if(t instanceof TimelineTrack) {
						TimelineTrack st = (TimelineTrack)t;
					
						Segment ts = st.getTrackSegment();
						
						DataDefinition d = ts.getComponentDataDefinition();
						if(d.isSoundKind()) count++;
						
					}
				}
			}
			
		}
		return count;
	}
	public boolean hasAS07DMSIdentifierSet() {
		if(file==null) {
			file = MXFFactory.readPartitions(filePath);
		}
		
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		ContentStorage contentStorage = preface.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
		
		for(tv.amwa.maj.model.Package p : packages) {
			if(p instanceof MaterialPackage) {
				for(Track t : p.getPackageTracks()) {
					if(t instanceof StaticTrack) {
						StaticTrack st = (StaticTrack)t;
						Segment ts = st.getTrackSegment();
						if(ts instanceof Sequence) {
							
							Sequence seq = (Sequence) ts;
							List<? extends Component> components = seq.getComponentObjects();
							
							for(Component c : components) {
								if(c instanceof DescriptiveMarkerImpl) {
									DescriptiveMarker m = (DescriptiveMarker) c;
									try {
										DescriptiveFramework df = m.getDescriptiveFrameworkObject();
										if(df!=null && df instanceof AS07DMSIdentifierSet) {
											return true;
										}	
									}
									catch(PropertyNotPresentException pnp) {
										LOGGER.log(Level.WARNING, pnp.toString(), pnp);
									}
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}
}
