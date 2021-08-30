package com.portalmedia.embarc.parser.mxf;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import tv.amwa.maj.io.mxf.RandomIndexItem;
import tv.amwa.maj.io.mxf.impl.EssenceElementImpl;
import tv.amwa.maj.io.mxf.impl.FooterPartitionImpl;
import tv.amwa.maj.io.mxf.impl.GenericStreamPartitionPackImpl;
import tv.amwa.maj.io.mxf.impl.HeaderPartitionImpl;
import tv.amwa.maj.io.mxf.impl.RandomIndexItemImpl;
import tv.amwa.maj.io.mxf.impl.RandomIndexPackImpl;
import tv.amwa.maj.model.AS07CoreDMSFramework;
import tv.amwa.maj.model.Component;
import tv.amwa.maj.model.ContentStorage;
import tv.amwa.maj.model.DescriptiveFramework;
import tv.amwa.maj.model.DescriptiveMarker;
import tv.amwa.maj.model.MaterialPackage;
import tv.amwa.maj.model.Preface;
import tv.amwa.maj.model.Segment;
import tv.amwa.maj.model.Sequence;
import tv.amwa.maj.model.StaticTrack;
import tv.amwa.maj.model.Track;
import tv.amwa.maj.model.impl.AS07CoreDMSFrameworkImpl;
import tv.amwa.maj.model.impl.DescriptiveMarkerImpl;
import tv.amwa.maj.record.AUID;
import tv.amwa.maj.record.impl.AUIDImpl;

public class MXFWriterServiceImpl implements MXFWriterService {
	MXFFile mxfFile;
	
	MXFWriterServiceImpl(MXFFile mxfFile){
		mxfFile = mxfFile;
	}

	@Override
	public boolean writeFile(FileInformation<MXFMetadata> metadata, String outputFile) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void writeFile() {
		
	}

	private AS07CoreDMSFramework updateCore(AS07CoreDMSFramework core, FileInformation<MXFMetadata> metadata) {
		
		MXFMetadata m = metadata.getFileData();
		HashMap<MXFColumn, MetadataColumnDef>  columns = m.getCoreColumns();
		core.setAudioTrackLayout(AUIDImpl.parseFactory((columns.get(MXFColumn.AS_07_Core_DMS_AudioTrackLayout).toString())));
		core.setAudioTrackLayoutComment(columns.get(MXFColumn.AS_07_Core_DMS_AudioTrackLayoutComment).toString());
		/*
		try {columns.put(MXFColumn.AS_07_Core_DMS_AudioTrackPrimaryLanguage, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_AudioTrackPrimaryLanguage, core.getAudioTrackPrimaryLanguage()));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_AudioTrackSecondaryLanguage,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_AudioTrackSecondaryLanguage, core.getAudioTrackSecondaryLanguage()));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_Captions,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_Captions, core.getCaptions()));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_Devices, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_Devices, devices));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_Identifiers, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_Identifiers, identifiers));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_IntendedAFD,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_IntendedAFD, core.getIntendedAFD()));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_NatureOfOrganization,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_NatureOfOrganization, core.getNatureOfOrganization()));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_PictureFormat,new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_PictureFormat, core.getPictureFormat()));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationCode, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationCode, core.getResponsibleOrganizationCode()));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationName, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationName, core.getResponsibleOrganizationName()));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_SecondaryTitle, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_SecondaryTitle, core.getSecondaryTitle()));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_ShimName, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_ShimName, core.getShimName()));}catch(PropertyNotPresentException ex) {}
		try {columns.put(MXFColumn.AS_07_Core_DMS_WorkingTitle, new StringMetadataColumn(MXFColumn.AS_07_Core_DMS_WorkingTitle, core.getWorkingTitle()));}catch(PropertyNotPresentException ex) {}

		*/
		
		return core;
	}
/*
	public void writeFile(String outputFilePath, AS07CoreDMSFramework updatedCore) throws IOException {
		long footerOffset = getFooterOffset(updatedCore);
		EmbARCIdentification embarcIdent = getIdentification();
		MXFFile mxfFile = MXFFactory.readPartitions(filePath);
				
		long totalBytes = 0;
		long thisPartition = 0;
		long previousPartition = 0;
		long klvFillLength = 8192;
		ArrayList<RandomIndexItem> ripItems = new ArrayList<RandomIndexItem>();
		try(OutputStream outputStream = new FileOutputStream(outputFilePath)) {
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
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		mxfFile.close();
	}
	
	private EmbARCIdentification getIdentification() {
		EmbARCIdentification identificationSingleton = EmbARCIdentification.getInstance();
		return identificationSingleton;
	}

	private long getFooterOffset(AS07CoreDMSFramework updatedCore) throws IOException {
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
					}
					catch(Exception ex) {
						
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
	*/
	
}
