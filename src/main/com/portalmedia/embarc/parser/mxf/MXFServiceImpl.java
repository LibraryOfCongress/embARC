package com.portalmedia.embarc.parser.mxf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.parser.FileInformation;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.StringMetadataColumn;

import tv.amwa.maj.constant.CommonConstants;
import tv.amwa.maj.exception.PropertyNotPresentException;
import tv.amwa.maj.industry.MetadataObject;
import tv.amwa.maj.industry.PropertyValue;
import tv.amwa.maj.industry.Warehouse;
import tv.amwa.maj.io.mxf.BodyPartition;
import tv.amwa.maj.io.mxf.BodyPartitionPack;
import tv.amwa.maj.io.mxf.EssencePartition;
import tv.amwa.maj.io.mxf.FooterPartition;
import tv.amwa.maj.io.mxf.FooterPartitionPack;
import tv.amwa.maj.io.mxf.HeaderMetadata;
import tv.amwa.maj.io.mxf.HeaderPartitionPack;
import tv.amwa.maj.io.mxf.IndexTableSegment;
import tv.amwa.maj.io.mxf.MXFBuilder;
import tv.amwa.maj.io.mxf.MXFConstants;
import tv.amwa.maj.io.mxf.MXFFactory;
import tv.amwa.maj.io.mxf.MXFFile;
import tv.amwa.maj.io.mxf.MXFStream;
import tv.amwa.maj.io.mxf.MXFStream.KeyAndConsumed;
import tv.amwa.maj.io.mxf.MXFStream.LengthAndConsumed;
import tv.amwa.maj.io.mxf.Partition;
import tv.amwa.maj.io.mxf.PartitionPack;
import tv.amwa.maj.io.mxf.PrimerPack;
import tv.amwa.maj.io.mxf.RandomIndexItem;
import tv.amwa.maj.io.mxf.RandomIndexPack;
import tv.amwa.maj.io.mxf.UL;
import tv.amwa.maj.io.mxf.impl.EssenceElementImpl;
import tv.amwa.maj.io.mxf.impl.FooterPartitionImpl;
import tv.amwa.maj.io.mxf.impl.GenericStreamPartitionPackImpl;
import tv.amwa.maj.io.mxf.impl.HeaderPartitionImpl;
import tv.amwa.maj.io.mxf.impl.MXFFileImpl;
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
import tv.amwa.maj.meta.ClassDefinition;
import tv.amwa.maj.meta.PropertyDefinition;
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
	
	public Preface getPreface() {
		MXFFile mxfFile = getFile();
		HeaderMetadata headerMetadata = mxfFile.getHeaderPartition().readHeaderMetadata();
		
		return headerMetadata.getPreface();
	}
	/**
	 * Applies a set of Core DMS column edits onto an existing framework object.
	 */
	private void applyCoreDMSColumnEdits(AS07CoreDMSFramework dms, HashMap<MXFColumn, MetadataColumnDef> coreColumns) {
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
	}

	public MXFFileWriteResult writeFile(String outputFilePath, HashMap<MXFColumn, MetadataColumnDef> coreColumns) throws IOException {
		return applyCoreDMSEdit(outputFilePath, (marker, dms) -> applyCoreDMSColumnEdits(dms, coreColumns));
	}

	public MXFFileWriteResult writeFile(String outputFilePath, AS07CoreDMSFramework updatedCore) throws IOException {
		if (updatedCore == null) {
			return copyFileUnchanged(outputFilePath);
		}
		return applyCoreDMSEdit(outputFilePath, (marker, dms) -> marker.setDescriptiveFrameworkObject(updatedCore));
	}

	private MXFFileWriteResult copyFileUnchanged(String outputFilePath) {
		MXFFileWriteResult result = new MXFFileWriteResult();
		try {
			if (!outputFilePath.equals(filePath)) {
				Path source = Paths.get(filePath);
				Path target = Paths.get(outputFilePath);
				if (Files.exists(target)) Files.delete(target);
				Files.copy(source, target);
			}
			result.setSuccess(true);
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, ex.toString(), ex);
			result.setException(ex);
			result.setSuccess(false);
		}
		return result;
	}

	private interface CoreDMSEditor {
		void apply(DescriptiveMarker marker, AS07CoreDMSFramework existingFramework);
	}

	private static class PartitionEditPlan {
		long metadataOffset;
		long oldHeaderByteCount;
		long newHeaderByteCount;
		long delta;
		byte[] contentBytes;
	}

	private static class StrongReferenceVectorEncoding {
		Map<String, byte[]> rawPropertyOverrides = new HashMap<String, byte[]>();
		List<byte[]> extraTopLevelKlvBlocks = new ArrayList<byte[]>();
	}

	private static final long MIN_FILL_SIZE = 20L;

	/**
	 * Finds the DescriptiveMarker carrying an AS07CoreDMSFramework in the given Preface, or null
	 * if none is present.
	 */
	private static DescriptiveMarker findCoreDMSMarker(Preface preface) {
		if (preface == null) return null;
		try {
			ContentStorage contentStorage = preface.getContentStorageObject();
			Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
			for (tv.amwa.maj.model.Package p : packages) {
				if (p instanceof MaterialPackage) {
					for (Track t : p.getPackageTracks()) {
						if (t instanceof StaticTrack) {
							StaticTrack st = (StaticTrack) t;
							Segment ts = st.getTrackSegment();
							if (ts instanceof Sequence) {
								Sequence seq = (Sequence) ts;
								for (Component c : seq.getComponentObjects()) {
									if (c instanceof DescriptiveMarkerImpl) {
										DescriptiveMarker m = (DescriptiveMarker) c;
										try {
											DescriptiveFramework df = m.getDescriptiveFrameworkObject();
											if (df != null && df instanceof AS07CoreDMSFramework) return m;
										} catch (PropertyNotPresentException pnp) {
											// not present on this marker, keep looking
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.log(Level.INFO, ex.toString());
		}
		return null;
	}

	private static long computeNewHeaderByteCount(long contentSize, long oldHeaderByteCount, long kagSize) {
		long fillIfSame = oldHeaderByteCount - contentSize;
		if (fillIfSame == 0 || fillIfSame >= MIN_FILL_SIZE) {
			return oldHeaderByteCount;
		}
		long grownTarget = contentSize + calculateKAGFillerSize(contentSize, kagSize);
		if (grownTarget <= oldHeaderByteCount) {
			grownTarget = oldHeaderByteCount + Math.max(kagSize, MIN_FILL_SIZE);
		}
		return grownTarget;
	}

	private static byte[] buildMetadataRegionBytes(PartitionEditPlan plan) throws IOException {
		long fillNeeded = plan.newHeaderByteCount - plan.contentBytes.length;
		if (fillNeeded == 0) return plan.contentBytes;
		ByteArrayOutputStream out = new ByteArrayOutputStream(plan.contentBytes.length + (int) fillNeeded);
		out.write(plan.contentBytes);
		MXFStream.writeFill(out, fillNeeded);
		return out.toByteArray();
	}

	private static byte[] getClassKeyBytes(Class<?> implClass) {
		ClassDefinition classDef = Warehouse.lookForClass(implClass);
		AUID classAUID = classDef.getAUID();
		ByteBuffer keyBuffer = ByteBuffer.allocate(16);
		MXFBuilder.writeKey((UL) classAUID, keyBuffer);
		keyBuffer.flip();
		byte[] keyBytes = new byte[16];
		keyBuffer.get(keyBytes);
		return keyBytes;
	}

	private static boolean bytesMatchAt(byte[] data, int offset, byte[] pattern) {
		if (offset + pattern.length > data.length) return false;
		for (int i = 0; i < pattern.length; i++) {
			if (data[offset + i] != pattern[i]) return false;
		}
		return true;
	}

	private static long detectPartitionPackPadding(RandomAccessFile source, long offset) throws IOException {
		byte[] probe = readRawBytes(source, offset, 32);
		ByteBuffer buffer = ByteBuffer.wrap(probe);
		UL key;
		try {
			key = MXFBuilder.readKey(buffer);
		} catch (Exception ex) {
			return 0;
		}
		if (key == null || !MXFBuilder.isKLVFill(key)) return 0;
		long length;
		try {
			length = MXFBuilder.readBERLength(buffer);
		} catch (Exception ex) {
			return 0;
		}
		if (length < 0) return 0;
		return buffer.position() + length;
	}

	private static int[] findTopLevelKlv(byte[] data, byte[] classKeyBytes) {
		int limit = data.length;
		int position = 0;
		while (position + 16 <= limit) {
			boolean matches = bytesMatchAt(data, position, classKeyBytes);
			ByteBuffer lengthBuffer = ByteBuffer.wrap(data, position + 16, Math.min(9, limit - position - 16));
			int beforePosition = lengthBuffer.position();
			long bodyLength;
			try {
				bodyLength = MXFBuilder.readBERLength(lengthBuffer);
			} catch (Exception ex) {
				return null;
			}
			if (bodyLength < 0 || bodyLength > Integer.MAX_VALUE) return null;
			int consumed = lengthBuffer.position() - beforePosition;
			int bodyStart = position + 16 + consumed;
			int bodyEnd = bodyStart + (int) bodyLength;
			if (bodyEnd > limit || bodyEnd < bodyStart) return null;
			if (matches) return new int[] { position, bodyStart, bodyEnd };
			position = bodyEnd;
		}
		return null;
	}

	private static int findRealMetadataEnd(byte[] data) {
		int limit = data.length;
		int position = 0;
		int lastRealEnd = 0;
		while (position + 16 <= limit) {
			UL key;
			try {
				key = MXFBuilder.readKey(ByteBuffer.wrap(data, position, 16));
			} catch (Exception ex) {
				break;
			}
			ByteBuffer lengthBuffer = ByteBuffer.wrap(data, position + 16, Math.min(9, limit - position - 16));
			int beforePosition = lengthBuffer.position();
			long bodyLength;
			try {
				bodyLength = MXFBuilder.readBERLength(lengthBuffer);
			} catch (Exception ex) {
				break;
			}
			if (bodyLength < 0) break;
			int consumed = lengthBuffer.position() - beforePosition;
			int bodyStart = position + 16 + consumed;
			long bodyEndLong = (long) bodyStart + bodyLength;
			if (bodyEndLong > limit) break;
			int bodyEnd = (int) bodyEndLong;
			if (key == null || !MXFBuilder.isKLVFill(key)) {
				lastRealEnd = bodyEnd;
			}
			position = bodyEnd;
		}
		return lastRealEnd;
	}

	private static AUID findInstanceUID(byte[] data, int bodyStart, int bodyEnd) {
		int position = bodyStart;
		while (position + 4 <= bodyEnd) {
			int tag = ((data[position] & 0xFF) << 8) | (data[position + 1] & 0xFF);
			int length = ((data[position + 2] & 0xFF) << 8) | (data[position + 3] & 0xFF);
			int valueStart = position + 4;
			int valueEnd = valueStart + length;
			if (valueEnd > bodyEnd) break;
			if (tag == (MXFConstants.InstanceTag & 0xFFFF) && length == 16) {
				byte[] uidBytes = new byte[16];
				System.arraycopy(data, valueStart, uidBytes, 0, 16);
				return new AUIDImpl(uidBytes);
			}
			position = valueEnd;
		}
		return null;
	}

	private static byte[] encodeCoreDMSLocalSet(AS07CoreDMSFramework framework, AUID instanceID, PrimerPack primerPack,
			Map<String, byte[]> rawPropertyOverrides)
			throws IOException, tv.amwa.maj.exception.InsufficientSpaceException {
		return encodeLocalSet(AS07CoreDMSFrameworkImpl.class, framework, instanceID, primerPack, rawPropertyOverrides, true);
	}

	// Used for the sub-objects a strong reference vector points to (e.g. an AS07DMSIdentifierSetImpl
	// entry of Identifiers): plain properties only, no overrides needed. Empty string sub-fields are
	// still written (omitEmptyStrings=false): IdentifierSetHelper/DeviceSetHelper encode a sub-object
	// as a fixed number of comma-separated positions, and dropping one would shift every field after
	// it when the record is parsed back on read.
	private static byte[] encodeSubObjectLocalSet(Class<?> implClass, MetadataObject obj, AUID instanceID,
			PrimerPack primerPack) throws IOException, tv.amwa.maj.exception.InsufficientSpaceException {
		return encodeLocalSet(implClass, obj, instanceID, primerPack, java.util.Collections.<String, byte[]>emptyMap(), false);
	}

	private static byte[] encodeLocalSet(Class<?> implClass, MetadataObject obj, AUID instanceID, PrimerPack primerPack,
			Map<String, byte[]> rawPropertyOverrides, boolean omitEmptyStrings)
			throws IOException, tv.amwa.maj.exception.InsufficientSpaceException {
		ClassDefinition classDef = Warehouse.lookForClass(implClass);
		SortedMap<? extends PropertyDefinition, ? extends PropertyValue> properties = classDef.getProperties(obj);

		for (PropertyDefinition property : properties.keySet()) {
			if (property.getAUID().equals(CommonConstants.ObjectClassID)) continue;
			if (primerPack.lookupLocalTag(property.getAUID()) == null) {
				primerPack.addLocalTagEntry(findFreeLocalTag(primerPack), property.getAUID());
			}
		}

		ByteArrayOutputStream bodyOut = new ByteArrayOutputStream();
		bodyOut.write(shortToBytes(MXFConstants.InstanceTag));
		bodyOut.write(shortToBytes((short) 16));
		bodyOut.write(instanceID.getAUIDValue());

		Set<String> written = new HashSet<String>();
		for (PropertyDefinition property : properties.keySet()) {
			if (property.getAUID().equals(CommonConstants.ObjectClassID)) continue;
			byte[] override = rawPropertyOverrides.get(property.getName());
			if (override != null) {
				bodyOut.write(override);
				written.add(property.getName());
				continue;
			}
			PropertyValue value = properties.get(property);
			// A field the user (or a stale default, e.g. IntendedAFD) has left as an empty string
			// is treated as absent rather than serialized as present-but-empty: some MAJ getters
			// default to "" instead of null and would otherwise resurrect a property that was
			// never actually set in the file, on every unrelated edit.
			if (omitEmptyStrings && "".equals(value.getValue())) continue;
			Short localTag = primerPack.lookupLocalTag(property.getAUID());
			long predictedLength = value.getType().lengthAsBytes(value);
			ByteBuffer valueBuffer = ByteBuffer.allocate((int) predictedLength + 64);
			value.getType().writeAsBytes(value, valueBuffer);
			int actualLength = valueBuffer.position();
			bodyOut.write(shortToBytes(localTag));
			bodyOut.write(shortToBytes((short) actualLength));
			bodyOut.write(valueBuffer.array(), 0, actualLength);
		}
		// An overridden strong reference vector may not have round-tripped into `properties`
		// (its getter throws internally for an empty-but-present list), so write it directly.
		for (Map.Entry<String, byte[]> entry : rawPropertyOverrides.entrySet()) {
			if (!written.contains(entry.getKey())) bodyOut.write(entry.getValue());
		}

		byte[] bodyBytes = bodyOut.toByteArray();
		ByteArrayOutputStream out = new ByteArrayOutputStream(bodyBytes.length + 24);
		MXFStream.writeKey(out, (UL) classDef.getAUID());
		MXFStream.writeBERLength(out, bodyBytes.length, 4);
		out.write(bodyBytes);
		return out.toByteArray();
	}

	private static byte[] buildStrongReferenceVectorBatchValue(List<AUID> uids) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(8 + uids.size() * 16);
		byte[] header = new byte[8];
		header[0] = (byte) ((uids.size() >> 24) & 0xFF);
		header[1] = (byte) ((uids.size() >> 16) & 0xFF);
		header[2] = (byte) ((uids.size() >> 8) & 0xFF);
		header[3] = (byte) (uids.size() & 0xFF);
		header[4] = 0; header[5] = 0; header[6] = 0; header[7] = 16;
		out.write(header, 0, 8);
		for (AUID uid : uids) out.write(uid.getAUIDValue(), 0, 16);
		return out.toByteArray();
	}

	private static byte[] shortToBytes(short value) {
		return new byte[] { (byte) ((value >> 8) & 0xFF), (byte) (value & 0xFF) };
	}

	private static short findFreeLocalTag(PrimerPack primerPack) throws IOException {
		for (int candidate = 0xFFFE; candidate > 0x8000; candidate--) {
			short tag = (short) candidate;
			if (primerPack.lookupUID(tag) == null) return tag;
		}
		throw new IOException("Could not find a free local tag to add a new Core DMS property.");
	}

	// Strong reference vector properties: the generic writer below mints a fresh random UID for
	// each entry instead of the sub-object's real one, so unchanged values must reuse the original TLV bytes.
	private static final String[] STRONG_REFERENCE_VECTOR_PROPERTIES = { "Identifiers", "Devices" };

	private static String safeIdentifiersString(AS07CoreDMSFramework framework) {
		try {
			return new IdentifierSetHelper().identifiersToString(framework.getIdentifiers());
		} catch (Exception ex) {
			return "";
		}
	}

	private static String safeDevicesString(AS07CoreDMSFramework framework) {
		try {
			return new DeviceSetHelper().devicesToString(framework.getDevices());
		} catch (Exception ex) {
			return "";
		}
	}

	private static PropertyDefinition findPropertyDefinition(ClassDefinition classDef, String name) {
		for (PropertyDefinition property : classDef.getAllPropertyDefinitions()) {
			if (property.getName().equals(name)) return property;
		}
		return null;
	}

	private static byte[] extractTLVBytes(byte[] data, int start, int end, short targetTag) {
		int position = start;
		while (position + 4 <= end) {
			int tag = ((data[position] & 0xFF) << 8) | (data[position + 1] & 0xFF);
			int length = ((data[position + 2] & 0xFF) << 8) | (data[position + 3] & 0xFF);
			int valueEnd = position + 4 + length;
			if (valueEnd > end) return null;
			if (tag == (targetTag & 0xFFFF)) {
				byte[] tlv = new byte[valueEnd - position];
				System.arraycopy(data, position, tlv, 0, tlv.length);
				return tlv;
			}
			position = valueEnd;
		}
		return null;
	}

	private static Class<?> subObjectImplClass(String propertyName) {
		return propertyName.equals("Identifiers") ? AS07DMSIdentifierSetImpl.class : AS07CoreDMSDeviceObjectsImpl.class;
	}

	private static List<? extends MetadataObject> currentSubObjects(String propertyName, AS07CoreDMSFramework framework) {
		try {
			return propertyName.equals("Identifiers") ? framework.getIdentifiers() : framework.getDevices();
		} catch (Exception ex) {
			return java.util.Collections.emptyList();
		}
	}

	// Handles both cases for Identifiers/Devices: when unchanged, the original TLV bytes are
	// reused verbatim (see STRONG_REFERENCE_VECTOR_PROPERTIES); when genuinely edited, fresh
	// sub-object KLV blocks are built (each with its own generated instance UID) and a matching
	// strong reference vector value is written to point at them.
	private static StrongReferenceVectorEncoding planStrongReferenceVectorEncoding(byte[] originalMetadataBytes,
			int frameworkBodyStart, int frameworkBodyEnd, PrimerPack primerPack, ClassDefinition classDef,
			String identifiersBeforeEdit, String devicesBeforeEdit, AS07CoreDMSFramework effectiveFramework,
			Map<String, List<AUID>> sharedSubObjectUIDs)
			throws IOException, tv.amwa.maj.exception.InsufficientSpaceException {
		StrongReferenceVectorEncoding encoding = new StrongReferenceVectorEncoding();
		for (String propertyName : STRONG_REFERENCE_VECTOR_PROPERTIES) {
			String beforeValue = propertyName.equals("Identifiers") ? identifiersBeforeEdit : devicesBeforeEdit;
			String afterValue = propertyName.equals("Identifiers")
					? safeIdentifiersString(effectiveFramework) : safeDevicesString(effectiveFramework);
			PropertyDefinition property = findPropertyDefinition(classDef, propertyName);
			if (property == null) continue;

			if (beforeValue.equals(afterValue)) {
				Short tag = primerPack.lookupLocalTag(property.getAUID());
				if (tag == null) continue;
				byte[] raw = extractTLVBytes(originalMetadataBytes, frameworkBodyStart, frameworkBodyEnd, tag);
				if (raw != null) encoding.rawPropertyOverrides.put(propertyName, raw);
				continue;
			}

			List<? extends MetadataObject> newItems = currentSubObjects(propertyName, effectiveFramework);
			if (newItems.isEmpty()) continue; // property becomes absent; nothing to write or add

			Class<?> implClass = subObjectImplClass(propertyName);
			// Header and footer metadata are independently re-planned copies of the same edit, so
			// the same conceptual new entry must reuse one UID across both instead of getting a
			// fresh random one from each - otherwise the two partitions silently diverge.
			List<AUID> reusableUIDs = sharedSubObjectUIDs.get(propertyName);
			boolean canReuse = reusableUIDs != null && reusableUIDs.size() == newItems.size();
			List<AUID> subInstanceIDs = new ArrayList<AUID>();
			for (int i = 0; i < newItems.size(); i++) {
				AUID subInstanceID = canReuse ? reusableUIDs.get(i) : new AUIDImpl();
				encoding.extraTopLevelKlvBlocks.add(
						encodeSubObjectLocalSet(implClass, newItems.get(i), subInstanceID, primerPack));
				subInstanceIDs.add(subInstanceID);
			}
			if (!canReuse) sharedSubObjectUIDs.put(propertyName, subInstanceIDs);

			Short tag = primerPack.lookupLocalTag(property.getAUID());
			if (tag == null) {
				tag = findFreeLocalTag(primerPack);
				primerPack.addLocalTagEntry(tag, property.getAUID());
			}
			byte[] batchValue = buildStrongReferenceVectorBatchValue(subInstanceIDs);
			ByteArrayOutputStream tlv = new ByteArrayOutputStream(4 + batchValue.length);
			tlv.write(shortToBytes(tag));
			tlv.write(shortToBytes((short) batchValue.length));
			tlv.write(batchValue);
			encoding.rawPropertyOverrides.put(propertyName, tlv.toByteArray());
		}
		return encoding;
	}

	private PartitionEditPlan planPartitionEdit(Partition partition, CoreDMSEditor editor, RandomAccessFile source,
			Map<String, List<AUID>> sharedSubObjectUIDs)
			throws IOException, tv.amwa.maj.exception.InsufficientSpaceException {
		HeaderMetadata headerMetadata = partition.readHeaderMetadata();
		if (headerMetadata == null) return null;
		Preface preface = headerMetadata.getPreface();
		DescriptiveMarker marker = findCoreDMSMarker(preface);
		if (marker == null) return null;

		AS07CoreDMSFramework existingFramework = (AS07CoreDMSFramework) marker.getDescriptiveFrameworkObject();
		// existingFramework is typically mutated in place by editor.apply(), so its strong
		// reference vector properties must be snapshotted as strings before the edit is applied.
		String identifiersBeforeEdit = safeIdentifiersString(existingFramework);
		String devicesBeforeEdit = safeDevicesString(existingFramework);
		editor.apply(marker, existingFramework);

		DescriptiveFramework effectiveDf = marker.getDescriptiveFrameworkObject();
		if (!(effectiveDf instanceof AS07CoreDMSFramework)) return null;
		AS07CoreDMSFramework effectiveFramework = (AS07CoreDMSFramework) effectiveDf;
		EmbARCIdentification embarcIdent = getIdentification();
		effectiveFramework.setLinkedGenerationID(embarcIdent.getIdentification().getLinkedGenerationID());

		PartitionPack pack = partition.getPartitionPack();
		long partitionPackKlvSize = pack.getEncodedSize() + 20;
		long partitionPackEnd = pack.getThisPartition() + partitionPackKlvSize;
		long metadataOffset = partitionPackEnd + detectPartitionPackPadding(source, partitionPackEnd);
		long oldHeaderByteCount = pack.getHeaderByteCount();
		if (oldHeaderByteCount <= 0 || oldHeaderByteCount > Integer.MAX_VALUE) return null;

		byte[] originalMetadataBytes = readRawBytes(source, metadataOffset, oldHeaderByteCount);

		byte[] frameworkClassKey = getClassKeyBytes(AS07CoreDMSFrameworkImpl.class);
		int[] frameworkKlv = findTopLevelKlv(originalMetadataBytes, frameworkClassKey);
		if (frameworkKlv == null) return null;
		AUID instanceID = findInstanceUID(originalMetadataBytes, frameworkKlv[1], frameworkKlv[2]);
		if (instanceID == null) return null;

		PrimerPack primerPack = headerMetadata.getPrimerPack().clone();
		int tagCountBefore = primerPack.countLocalTagEntries();
		ClassDefinition classDef = Warehouse.lookForClass(AS07CoreDMSFrameworkImpl.class);
		StrongReferenceVectorEncoding strongRefEncoding = planStrongReferenceVectorEncoding(originalMetadataBytes,
				frameworkKlv[1], frameworkKlv[2], primerPack, classDef,
				identifiersBeforeEdit, devicesBeforeEdit, effectiveFramework, sharedSubObjectUIDs);
		byte[] newLocalSetBytes = encodeCoreDMSLocalSet(effectiveFramework, instanceID, primerPack,
				strongRefEncoding.rawPropertyOverrides);
		boolean primerPackGrew = primerPack.countLocalTagEntries() != tagCountBefore;

		int frameworkStart = frameworkKlv[0];
		int frameworkEnd = frameworkKlv[2];
		int realMetadataEnd = findRealMetadataEnd(originalMetadataBytes);
		if (realMetadataEnd < frameworkEnd) realMetadataEnd = frameworkEnd;

		ByteArrayOutputStream contentStream = new ByteArrayOutputStream(originalMetadataBytes.length + newLocalSetBytes.length);
		if (primerPackGrew) {
			int[] primerKlv = findTopLevelKlv(originalMetadataBytes, getClassKeyBytes(tv.amwa.maj.io.mxf.impl.PrimerPackImpl.class));
			if (primerKlv == null || primerKlv[0] >= frameworkStart) {
				throw new IOException("Could not locate the primer pack while extending it for a new Core DMS property.");
			}
			int newPrimerPackSize = (int) tv.amwa.maj.io.mxf.impl.PrimerPackImpl.lengthAsBytes(primerPack);
			ByteBuffer primerBuffer = ByteBuffer.allocate(newPrimerPackSize);
			tv.amwa.maj.io.mxf.impl.PrimerPackImpl.writeAsBytes(primerPack, primerBuffer);

			contentStream.write(originalMetadataBytes, 0, primerKlv[0]);
			contentStream.write(primerBuffer.array());
			contentStream.write(originalMetadataBytes, primerKlv[2], frameworkStart - primerKlv[2]);
		} else {
			contentStream.write(originalMetadataBytes, 0, frameworkStart);
		}
		contentStream.write(newLocalSetBytes);
		for (byte[] extraKlvBlock : strongRefEncoding.extraTopLevelKlvBlocks) {
			contentStream.write(extraKlvBlock);
		}
		contentStream.write(originalMetadataBytes, frameworkEnd, realMetadataEnd - frameworkEnd);
		byte[] contentBytes = contentStream.toByteArray();

		PartitionEditPlan plan = new PartitionEditPlan();
		plan.metadataOffset = metadataOffset;
		plan.oldHeaderByteCount = oldHeaderByteCount;
		plan.newHeaderByteCount = computeNewHeaderByteCount(contentBytes.length, oldHeaderByteCount, pack.getKagSize());
		plan.delta = plan.newHeaderByteCount - oldHeaderByteCount;
		plan.contentBytes = contentBytes;
		return plan;
	}

	private MXFFileWriteResult applyCoreDMSEdit(String outputFilePath, CoreDMSEditor editor) {
		MXFFileWriteResult result = new MXFFileWriteResult();
		MXFFile mxfFile = null;
		try {
			mxfFile = MXFFactory.readPartitions(filePath);
			if (!mxfFile.isOpen())
				throw new IOException("Cannot open file " + filePath + " for reading.");
			if (mxfFile.getRunInSize() != 0)
				throw new IOException("Files with a run-in are not supported by the safe Core DMS rewrite path.");

			// Header and footer metadata are duplicate copies of each other, so a freshly created
			// sub-object (e.g. a new Identifiers entry) must reuse the same instance UID in both
			// partitions rather than getting a fresh random one from each independent plan.
			Map<String, List<AUID>> sharedSubObjectUIDs = new HashMap<String, List<AUID>>();
			PartitionEditPlan headerPlan;
			PartitionEditPlan footerPlan;
			try (RandomAccessFile source = new RandomAccessFile(filePath, "r")) {
				headerPlan = planPartitionEdit(mxfFile.getHeaderPartition(), editor, source, sharedSubObjectUIDs);
				FooterPartition footerPartition = mxfFile.getFooterPartition();
				footerPlan = footerPartition != null
						? planPartitionEdit(footerPartition, editor, source, sharedSubObjectUIDs) : null;
			}

			if (headerPlan == null && footerPlan == null) {
				throw new IOException("Could not locate an AS_07 Core DMS framework to update.");
			}

			long headerDelta = headerPlan != null ? headerPlan.delta : 0;
			long footerDelta = footerPlan != null ? footerPlan.delta : 0;

			if (headerDelta == 0 && footerDelta == 0) {
				result = applyInPlace(outputFilePath, headerPlan, footerPlan);
			} else {
				result = applyWithRelocate(outputFilePath, mxfFile, headerPlan, footerPlan, headerDelta);
			}
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, ex.toString(), ex);
			result.setException(ex);
			result.setSuccess(false);
		} finally {
			if (mxfFile != null) mxfFile.close();
		}
		return result;
	}

	/**
	 * Fast path: the new metadata for every changed partition fits within the byte count already
	 * reserved on disk. Nothing else in the file moves, so only the metadata bytes themselves are
	 * overwritten via a direct seek + write.
	 */
	private MXFFileWriteResult applyInPlace(String outputFilePath, PartitionEditPlan headerPlan, PartitionEditPlan footerPlan) {
		MXFFileWriteResult result = new MXFFileWriteResult();
		try {
			if (!outputFilePath.equals(filePath)) {
				Path source = Paths.get(filePath);
				Path target = Paths.get(outputFilePath);
				if (Files.exists(target)) Files.delete(target);
				Files.copy(source, target);
			}

			MXFFile outFile = MXFFactory.readPartitions(outputFilePath);
			MXFFileImpl outFileImpl = (MXFFileImpl) outFile;
			try {
				if (headerPlan != null) writePlanInPlace(outFileImpl, headerPlan);
				if (footerPlan != null) writePlanInPlace(outFileImpl, footerPlan);
			} finally {
				outFile.close();
			}
			result.setSuccess(true);
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, ex.toString(), ex);
			result.setException(ex);
			result.setSuccess(false);
		}
		return result;
	}

	private static void writePlanInPlace(MXFFileImpl mxfFileImpl, PartitionEditPlan plan) throws IOException {
		byte[] regionBytes = buildMetadataRegionBytes(plan);
		if (regionBytes.length != plan.oldHeaderByteCount) {
			throw new IOException("Computed metadata region size does not match the space reserved on disk.");
		}
		mxfFileImpl.seek(plan.metadataOffset);
		ByteBuffer buffer = ByteBuffer.wrap(regionBytes);
		while (buffer.hasRemaining()) {
			int written = mxfFileImpl.write(buffer);
			if (written <= 0) {
				throw new IOException("Failed to write the full metadata region in place.");
			}
		}
	}

	/**
	 * Grow path: at least one partition's new metadata no longer fits in its existing budget.
	 * Rebuilds the file in a single forward pass, but every byte that is not part of a changed
	 * metadata region is copied raw (via FileChannel.transferTo) straight from the source file.
	 * Essence elements and index tables are never parsed, decoded or re-encoded. Every partition
	 * pack after the resized header is re-serialized only to update its ThisPartition,
	 * PreviousPartition and FooterPartition offsets (all fixed-size fields, so this never changes
	 * a partition pack's encoded length); the random index pack's entries are updated the same way.
	 */
	private MXFFileWriteResult applyWithRelocate(String outputFilePath, MXFFile mxfFile,
			PartitionEditPlan headerPlan, PartitionEditPlan footerPlan, long headerDelta) {

		MXFFileWriteResult result = new MXFFileWriteResult();
		String tempFilePath = outputFilePath + ".tmp";
		try {
			FooterPartition footerPartitionObj = mxfFile.getFooterPartition();
			if (footerPartitionObj == null) {
				throw new IOException("Cannot safely relocate a file with no footer partition.");
			}
			PartitionPack footerPackOriginal = footerPartitionObj.getPartitionPack();
			long oldFooterOffset = footerPackOriginal.getThisPartition();
			long newFooterOffset = oldFooterOffset + headerDelta;

			long originalFileLength = new File(filePath).length();
			RandomIndexPack rip = mxfFile.getRandomIndexPack();
			long ripLength = rip != null ? rip.getLength() : 0;
			long oldRipStart = originalFileLength - ripLength;

			try (RandomAccessFile source = new RandomAccessFile(filePath, "r");
				 FileOutputStream fos = new FileOutputStream(tempFilePath)) {

				FileChannel srcChannel = source.getChannel();
				WritableByteChannel destChannel = fos.getChannel();

				// ---- Header partition ----
				PartitionPack headerPackInfo = mxfFile.getHeaderPartition().getPartitionPack();
				long headerPackKlvSize = headerPackInfo.getEncodedSize() + 20;
				long headerOldHeaderByteCount = headerPackInfo.getHeaderByteCount();
				long headerPaddingSize = detectPartitionPackPadding(source, headerPackKlvSize);
				long headerMetadataOffset = headerPackKlvSize + headerPaddingSize;

				byte[] headerMetadataRegion;
				long headerNewHeaderByteCount;
				if (headerPlan != null) {
					headerNewHeaderByteCount = headerPlan.newHeaderByteCount;
					headerMetadataRegion = buildMetadataRegionBytes(headerPlan);
				} else {
					headerNewHeaderByteCount = headerOldHeaderByteCount;
					headerMetadataRegion = readRawBytes(source, headerMetadataOffset, headerOldHeaderByteCount);
				}

				byte[] headerPackBytes = patchPartitionPackOffsets(source, 0, headerPackKlvSize,
						0L, 0L, newFooterOffset, headerNewHeaderByteCount);
				fos.write(headerPackBytes);
				if (headerPaddingSize > 0) {
					copyRawBytes(srcChannel, destChannel, headerPackKlvSize, headerPaddingSize);
				}
				fos.write(headerMetadataRegion);

				int partitionCount = mxfFile.countPartitions();
				long headerContentStart = headerMetadataOffset + headerOldHeaderByteCount;
				long headerContentEnd = (partitionCount > 2)
						? ((BodyPartition) mxfFile.getPartitionAt(1)).getPartitionPack().getThisPartition()
						: oldFooterOffset;
				long headerContentLength = headerContentEnd - headerContentStart;
				if (headerContentLength < 0) {
					throw new IOException("Unexpected negative header content length while relocating header partition.");
				}
				copyRawBytes(srcChannel, destChannel, headerContentStart, headerContentLength);

				// ---- Body partitions (raw copy, only offset fields are patched) ----
				for (int i = 1; i < partitionCount - 1; i++) {
					BodyPartition originalPartition = (BodyPartition) mxfFile.getPartitionAt(i);
					PartitionPack bppInfo = originalPartition.getPartitionPack();

					long oldThisPartition = bppInfo.getThisPartition();
					long oldPreviousPartition = bppInfo.getPreviousPartition();
					long partitionPackKlvSize = bppInfo.getEncodedSize() + 20;

					byte[] bppBytes = patchPartitionPackOffsets(source, oldThisPartition, partitionPackKlvSize,
							oldThisPartition + headerDelta,
							oldPreviousPartition == 0 ? 0 : oldPreviousPartition + headerDelta,
							newFooterOffset, null);
					fos.write(bppBytes);

					long contentStart = oldThisPartition + partitionPackKlvSize;
					long contentEnd = (i + 1 < partitionCount - 1)
							? ((BodyPartition) mxfFile.getPartitionAt(i + 1)).getPartitionPack().getThisPartition()
							: oldFooterOffset;
					long contentLength = contentEnd - contentStart;
					if (contentLength < 0) {
						throw new IOException("Unexpected negative partition content length while relocating body partition " + i + ".");
					}
					copyRawBytes(srcChannel, destChannel, contentStart, contentLength);
				}

				// ---- Footer partition ----
				long footerPackKlvSize = footerPackOriginal.getEncodedSize() + 20;
				long footerOldHeaderByteCount = footerPackOriginal.getHeaderByteCount();
				long oldFooterPrevious = footerPackOriginal.getPreviousPartition();
				long footerPaddingSize = detectPartitionPackPadding(source, oldFooterOffset + footerPackKlvSize);
				long footerMetadataOffset = oldFooterOffset + footerPackKlvSize + footerPaddingSize;

				byte[] footerMetadataRegion;
				long footerNewHeaderByteCount;
				if (footerPlan != null) {
					footerNewHeaderByteCount = footerPlan.newHeaderByteCount;
					footerMetadataRegion = buildMetadataRegionBytes(footerPlan);
				} else {
					footerNewHeaderByteCount = footerOldHeaderByteCount;
					footerMetadataRegion = readRawBytes(source, footerMetadataOffset, footerOldHeaderByteCount);
				}

				byte[] footerPackBytes = patchPartitionPackOffsets(source, oldFooterOffset, footerPackKlvSize,
						newFooterOffset, oldFooterPrevious == 0 ? 0 : oldFooterPrevious + headerDelta,
						newFooterOffset, footerNewHeaderByteCount);
				fos.write(footerPackBytes);
				if (footerPaddingSize > 0) {
					copyRawBytes(srcChannel, destChannel, oldFooterOffset + footerPackKlvSize, footerPaddingSize);
				}
				fos.write(footerMetadataRegion);

				long footerContentStart = footerMetadataOffset + footerOldHeaderByteCount;
				long footerContentLength = oldRipStart - footerContentStart;
				if (footerContentLength < 0) {
					throw new IOException("Unexpected negative footer content length while relocating footer partition.");
				}
				copyRawBytes(srcChannel, destChannel, footerContentStart, footerContentLength);

				// ---- Random Index Pack ----
				// getPartitionIndex() returns defensive clones, so the shifted items must be
				// written back explicitly via setPartitionIndex() rather than mutated in place.
				if (rip != null) {
					RandomIndexItem[] items = rip.getPartitionIndex();
					for (RandomIndexItem item : items) {
						long offset = item.getByteOffset();
						if (offset != 0) {
							item.setByteOffset(offset + headerDelta);
						}
					}
					rip.setPartitionIndex(items);
					MXFStream.writeRandomIndexPack(fos, rip);
				}
			}

			Path finalPath = Paths.get(outputFilePath);
			Path tempPath = Paths.get(tempFilePath);
			if (Files.exists(finalPath)) Files.delete(finalPath);
			Files.move(tempPath, finalPath);

			result.setSuccess(true);
		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, ex.toString(), ex);
			try {
				Path tempPath = Paths.get(tempFilePath);
				if (Files.exists(tempPath)) Files.delete(tempPath);
			} catch (IOException ignore) {
			}
			result.setException(ex);
			result.setSuccess(false);
		}
		return result;
	}

	private static byte[] readRawBytes(RandomAccessFile source, long offset, long length) throws IOException {
		byte[] buffer = new byte[(int) length];
		source.seek(offset);
		source.readFully(buffer);
		return buffer;
	}

	private static final int PP_VALUE_START = 20;
	private static final int PP_THIS_PARTITION_OFFSET = PP_VALUE_START + 8;
	private static final int PP_PREVIOUS_PARTITION_OFFSET = PP_VALUE_START + 16;
	private static final int PP_FOOTER_PARTITION_OFFSET = PP_VALUE_START + 24;
	private static final int PP_HEADER_BYTE_COUNT_OFFSET = PP_VALUE_START + 32;

	private static byte[] patchPartitionPackOffsets(RandomAccessFile source, long packStart, long packKlvSize,
			long newThisPartition, long newPreviousPartition, long newFooterPartition, Long newHeaderByteCount) throws IOException {
		byte[] raw = readRawBytes(source, packStart, packKlvSize);
		putUInt64BE(raw, PP_THIS_PARTITION_OFFSET, newThisPartition);
		putUInt64BE(raw, PP_PREVIOUS_PARTITION_OFFSET, newPreviousPartition);
		putUInt64BE(raw, PP_FOOTER_PARTITION_OFFSET, newFooterPartition);
		if (newHeaderByteCount != null) {
			putUInt64BE(raw, PP_HEADER_BYTE_COUNT_OFFSET, newHeaderByteCount);
		}
		return raw;
	}

	private static void putUInt64BE(byte[] data, int offset, long value) {
		for (int i = 7; i >= 0; i--) {
			data[offset + i] = (byte) (value & 0xFF);
			value >>>= 8;
		}
	}

	private static void copyRawBytes(FileChannel srcChannel, WritableByteChannel destChannel, long offset, long length) throws IOException {
		long position = offset;
		long remaining = length;
		while (remaining > 0) {
			long transferred = srcChannel.transferTo(position, remaining, destChannel);
			if (transferred <= 0) {
				throw new IOException("Failed to copy raw bytes from source file at offset " + position + ".");
			}
			position += transferred;
			remaining -= transferred;
		}
	}
	@Override
	public boolean DownloadGenericStream(int streamId, String outputFile) {
		ByteBuffer bb = GetGenericStream(streamId);
		Assert.assertNotNull(bb);
		// TODO:  Figure out extension type
		try {
			File file = new File(outputFile);
			try(FileOutputStream fileOutputStream = new FileOutputStream(file, false)){
				FileChannel channel = fileOutputStream.getChannel();
				channel.write(bb);
			}
	        return true;
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);
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
				
				BodyPartitionPack opp = originalPartition.getPartitionPack().clone();
				if(opp.getBodySID()==streamId) {
					ByteBuffer bb = GetGenericStream(opp.getThisPartition() + opp.getEncodedSize() + 20 + opp.getHeaderByteCount() + opp.getIndexByteCount());
					return bb;
				}
			}
		}
		return null;
	}
	private ByteBuffer GetGenericStream(long offset) {
		// Read length
		try (InputStream is = new FileInputStream(filePath)){
			MXFStream.skipForward(is, offset);
			@SuppressWarnings("unused")
			KeyAndConsumed key = MXFStream.readKey(is);
			LengthAndConsumed length = MXFStream.readBERLength(is);
			ByteBuffer bb = MXFStream.readValue(is, length.getLength());
			return bb;
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);
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
	private AS07CoreDMSFramework getAS07CoreDMSFrameworkHeader() {
		if(file==null) System.out.println("File is null");
		if(file.getHeaderPartition()==null) System.out.println("Header partition is null");
		HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
		
		Preface preface = fromTheHeader.getPreface();
		ContentStorage contentStorage = preface.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packages = contentStorage.getPackages();
		AS07CoreDMSFramework coreFramework = null;
		int count =  1;
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
											coreFramework = (AS07CoreDMSFramework) df;
											System.out.println("Found  framework" + count);
											count  = count  +  1;
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
		return coreFramework;

	}
	private AS07CoreDMSFramework getAS07CoreDMSFrameworkFooter() {

		AS07CoreDMSFramework coreFramework = null;
		HeaderMetadata fromTheFooter = file.getFooterPartition().readHeaderMetadata();
		
		if(fromTheFooter == null) return coreFramework;
		
		Preface prefaceF = fromTheFooter.getPreface();
		ContentStorage contentStorageF = prefaceF.getContentStorageObject();
		Set<? extends tv.amwa.maj.model.Package> packagesF = contentStorageF.getPackages();
		for(tv.amwa.maj.model.Package p : packagesF) {
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
											coreFramework = (AS07CoreDMSFramework) df;
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
		return coreFramework;
	}
	public AS07CoreDMSFramework getAS07CoreDMSFramework() {
		getFile();

		AS07CoreDMSFramework coreFramework = getAS07CoreDMSFrameworkFooter();
		if(coreFramework == null) coreFramework = getAS07CoreDMSFrameworkHeader();
		
		return coreFramework;
	}

	/**
	 * Computes the size of a KLV filler needed to align the next item to a KAG boundary.
	 * Returns 0 if already aligned. The minimum returned size is 20 bytes (the smallest
	 * KLV fill item MXFStream can write).
	 */
	private static long calculateKAGFillerSize(long currentSize, long kagSize) {
		if (kagSize <= 0) return 0;
		long remainder = currentSize % kagSize;
		if (remainder == 0) return 0;
		long filler = kagSize - remainder;
		if (filler < 20) filler += kagSize;
		return filler;
	}

	public void setAS07CoreDMSFramework(AS07CoreDMSFramework dms) {
		AS07CoreDMSFramework coreFrameworkHeader = getAS07CoreDMSFrameworkHeader();
		AS07CoreDMSFramework coreFrameworkFooter = getAS07CoreDMSFrameworkFooter();
		EmbARCIdentification embarcIdent = getIdentification();
		if(coreFrameworkHeader!=null) {
			HeaderMetadata fromTheHeader = file.getHeaderPartition().readHeaderMetadata();
			
			Preface preface = fromTheHeader.getPreface();

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
		if(coreFrameworkFooter!=null) {
			HeaderMetadata fromTheFooter = file.getFooterPartition().readHeaderMetadata();
			
			Preface preface = fromTheFooter.getPreface();
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
		String identifiers = "";
		try {
			// getIdentifiers() throws NullPointerException, not PropertyNotPresentException, when empty.
			identifiers = idSetHelper.identifiersToString(core.getIdentifiers());
		} catch (Exception ex) {
			LOGGER.log(Level.INFO, "AS_07_Core_DMS_Identifiers Property Not Present");
		}

		String devices = "";
		try {
			DeviceSetHelper deviceSetHelper = new DeviceSetHelper();
			List<AS07CoreDMSDeviceObjectsImpl> devicesList = core.getDevices();
			if (devicesList != null) {
				devices = deviceSetHelper.devicesToString(devicesList);
			}
		} catch(PropertyNotPresentException ex) {
			LOGGER.log(Level.WARNING, ex.toString(), ex);
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
			
			AS07GSPDMSObject binaryObject = bd.getTextBasedObject();
			
			try {cols.put(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, new StringMetadataColumn(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, binaryObject.getTextBasedMetadataPayloadSchemeID().toString()));
			}catch(PropertyNotPresentException ex) {
				cols.put(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, new StringMetadataColumn(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, "PROPERTY NOT PRESENT"));
			}

			try {cols.put(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, new StringMetadataColumn(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, binaryObject.getRfc5646TextLanguageCode()));}
			catch(PropertyNotPresentException ex) {

				cols.put(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, new StringMetadataColumn(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, "PROPERTY NOT PRESENT"));
				
			}
			try {cols.put(MXFColumn.AS_07_Object_MIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_MIMEMediaType, binaryObject.getMimeMediaType()));}
			catch(PropertyNotPresentException ex) {

				cols.put(MXFColumn.AS_07_Object_MIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_MIMEMediaType, "PROPERTY NOT PRESENT"));
				
			}
			try {cols.put(MXFColumn.AS_07_Object_TextMIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_TextMIMEMediaType, binaryObject.getTextMimeMediaType()));}
			catch(PropertyNotPresentException ex) {
				cols.put(MXFColumn.AS_07_Object_TextMIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_TextMIMEMediaType, "PROPERTY NOT PRESENT"));
			}
			try {cols.put(MXFColumn.AS_07_Object_DataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_DataDescription, binaryObject.getDataDescriptions()));}
			catch(PropertyNotPresentException ex) {
				cols.put(MXFColumn.AS_07_Object_DataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_DataDescription, "PROPERTY NOT PRESENT"));
			}
			try {cols.put(MXFColumn.AS_07_Object_TextDataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_TextDataDescription, binaryObject.getTextDataDescriptions()));}
			catch(PropertyNotPresentException ex) {
				cols.put(MXFColumn.AS_07_Object_TextDataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_TextDataDescription, "PROPERTY NOT PRESENT"));
			}
			try {cols.put(MXFColumn.AS_07_Object_Note, new StringMetadataColumn(MXFColumn.AS_07_Object_Note, binaryObject.getNote()));}
			catch(PropertyNotPresentException ex) {
				cols.put(MXFColumn.AS_07_Object_Note, new StringMetadataColumn(MXFColumn.AS_07_Object_Note, "PROPERTY NOT PRESENT"));
			}
			try {cols.put(MXFColumn.AS_07_Object_GenericStreamID, new StringMetadataColumn(MXFColumn.AS_07_Object_GenericStreamID, Integer.toString(binaryObject.getGenericStreamId())));}
			catch(PropertyNotPresentException ex) {
				cols.put(MXFColumn.AS_07_Object_GenericStreamID, new StringMetadataColumn(MXFColumn.AS_07_Object_GenericStreamID, "PROPERTY NOT PRESENT"));
			}
			
			List<AS07DMSIdentifierSetImpl> identSet = binaryObject.getIdentifiers();
			String objIdentifiers = idSetHelper.identifiersToString(identSet);
			cols.put(MXFColumn.AS_07_Object_Identifiers, new StringMetadataColumn(MXFColumn.AS_07_Object_Identifiers, objIdentifiers));	

			try {bdColumns.put(Integer.toString(binaryObject.getGenericStreamId()), cols);}
			catch(PropertyNotPresentException ex) {
			}
		}
		metadata.setBDCount(bdColumns.size());
		metadata.setBDColumns(bdColumns);
		try {
			List<AS07GspTdDMSFrameworkImpl> tds = this.getAS07GspTdDMSFramework();
		
			HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> tdColumns = new HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>>();
			for(AS07GspTdDMSFrameworkImpl td : tds) {
				LinkedHashMap<MXFColumn, MetadataColumnDef> cols = new LinkedHashMap<MXFColumn, MetadataColumnDef>();
				try {cols.put(MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode, new StringMetadataColumn(MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode, td.getPrimaryRFC5646LanguageCode()));}
				catch(PropertyNotPresentException ex) {
					cols.put(MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode, new StringMetadataColumn(MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode, "PROPERTY NOT PRESENT"));
				}
				
				AS07GSPDMSObject textBasedObject = td.getTextBasedObject();
				
				List<AS07DMSIdentifierSetImpl> identSet = textBasedObject.getIdentifiers();
				String objIdentifiers = idSetHelper.identifiersToString(identSet);
				
				try {cols.put(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, new StringMetadataColumn(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, 
						textBasedObject.getTextBasedMetadataPayloadSchemeID().toString()));}
				catch(PropertyNotPresentException ex) {
					cols.put(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, new StringMetadataColumn(MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier, "PROPERTY NOT PRESENT"));
				}
				try {cols.put(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, new StringMetadataColumn(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, textBasedObject.getRfc5646TextLanguageCode()));}
				catch(PropertyNotPresentException ex) {
					cols.put(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, new StringMetadataColumn(MXFColumn.AS_07_Object_RFC5646TextLanguageCode, "PROPERTY NOT PRESENT"));
				}
				try {
					String mimeType = textBasedObject.getMimeMediaType();
					cols.put(MXFColumn.AS_07_Object_MIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_MIMEMediaType, textBasedObject.getMimeMediaType()));
					if(mimeType.equals("text/xml")) {
						ManifestParser mfParser = new ManifestParserImpl();
						ByteBuffer bb = GetGenericStream(textBasedObject.getGenericStreamId());
						if (bb != null) {
							ManifestType mfType = mfParser.isManifest(bb);
							cols.put(MXFColumn.AS_07_Manifest, new StringMetadataColumn(MXFColumn.AS_07_Manifest, mfType == ManifestType.NOT_MANIFEST ? "false" : "true"));
							cols.put(MXFColumn.AS_07_Manifest_Valid, new StringMetadataColumn(MXFColumn.AS_07_Manifest_Valid, mfType == ManifestType.VALID_MANIFEST ? "true" : "false"));
						} else {
							cols.put(MXFColumn.AS_07_Manifest, new StringMetadataColumn(MXFColumn.AS_07_Manifest, "false"));
							cols.put(MXFColumn.AS_07_Manifest_Valid, new StringMetadataColumn(MXFColumn.AS_07_Manifest_Valid, "false"));
						}
					}
				
				}catch(PropertyNotPresentException ex) {} catch (FileNotFoundException e) {
					LOGGER.log(Level.WARNING, e.toString(), e);
				}
				try {cols.put(MXFColumn.AS_07_Object_TextMIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_TextMIMEMediaType, textBasedObject.getTextMimeMediaType()));}
				catch(PropertyNotPresentException ex) {
					cols.put(MXFColumn.AS_07_Object_TextMIMEMediaType, new StringMetadataColumn(MXFColumn.AS_07_Object_TextMIMEMediaType, "PROPERTY NOT PRESENT"));
				}
				try {cols.put(MXFColumn.AS_07_Object_DataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_DataDescription, textBasedObject.getDataDescriptions()));}
				catch(PropertyNotPresentException ex) {
					cols.put(MXFColumn.AS_07_Object_DataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_DataDescription, "PROPERTY NOT PRESENT"));
				}
				try {cols.put(MXFColumn.AS_07_Object_TextDataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_TextDataDescription, textBasedObject.getTextDataDescriptions()));}
				catch(PropertyNotPresentException ex) {
					cols.put(MXFColumn.AS_07_Object_TextDataDescription, new StringMetadataColumn(MXFColumn.AS_07_Object_TextDataDescription, "PROPERTY NOT PRESENT"));
				}
				try {cols.put(MXFColumn.AS_07_Object_Note, new StringMetadataColumn(MXFColumn.AS_07_Object_Note, textBasedObject.getNote()));}
				catch(PropertyNotPresentException ex) {
					cols.put(MXFColumn.AS_07_Object_Note, new StringMetadataColumn(MXFColumn.AS_07_Object_Note, "PROPERTY NOT PRESENT"));
				}
				try {cols.put(MXFColumn.AS_07_Object_GenericStreamID, new StringMetadataColumn(MXFColumn.AS_07_Object_GenericStreamID, Integer.toString(textBasedObject.getGenericStreamId())));}
				catch(PropertyNotPresentException ex) {
					cols.put(MXFColumn.AS_07_Object_GenericStreamID, new StringMetadataColumn(MXFColumn.AS_07_Object_GenericStreamID, "PROPERTY NOT PRESENT"));
				}
				try {cols.put(MXFColumn.AS_07_Object_Identifiers, new StringMetadataColumn(MXFColumn.AS_07_Object_Identifiers, objIdentifiers));}
				catch(PropertyNotPresentException ex) {
					cols.put(MXFColumn.AS_07_Object_Identifiers, new StringMetadataColumn(MXFColumn.AS_07_Object_Identifiers, "PROPERTY NOT PRESENT"));
				}

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
