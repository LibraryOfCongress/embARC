package com.portalmedia.embarc.gui.helper;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.model.MXFFileInformationViewModel;
import com.portalmedia.embarc.parser.FileInformation;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.parser.mxf.MXFMetadata;
import com.portalmedia.embarc.parser.mxf.MXFService;
import com.portalmedia.embarc.parser.mxf.MXFServiceImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tv.amwa.maj.exception.PropertyNotPresentException;

/**
 * 
 *
 * @author PortalMedia
 * @since 2019-05-20
 **/
public class MXFFileList {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getClass().getName());
	
	private static class MXFFileListHolder {
		private static final MXFFileList INSTANCE = new MXFFileList();
	}

	public static MXFFileList getInstance() {
		return MXFFileListHolder.INSTANCE;
	}
	
	private static List<FileInformation<MXFMetadata>> fileList;
	private int maxTD = 0;
	private int maxBD = 0;
	
	public MXFFileList() {
		fileList = new ArrayList<FileInformation<MXFMetadata>>();
	}
	
	public static List<FileInformation<MXFMetadata>> getList() {
		return fileList;
	}
	
	public void clearList() {
		fileList.clear();
	}
	
	public ObservableList<MXFFileInformationViewModel> getObservableList() {
		List<MXFFileInformationViewModel> list = new ArrayList<MXFFileInformationViewModel>();

		for (FileInformation<MXFMetadata> f : fileList) {
			MXFFileInformationViewModel vm = new MXFFileInformationViewModel(f.getName(), f.getPath(), f.getUUID());

			vm.setCoreData(f.getFileData().getCoreColumns());
			vm.setProp("version", f.getFileData().getVersion());
			vm.setProp("format", f.getFileData().getFormat());
			vm.setProp("profile", f.getFileData().getProfile());
			vm.setProp("size", Long.toString(f.getFileData().getFileSize()));
			vm.setProp("audioTrackCount", Integer.toString(f.getFileData().getAudioTrackCount()));
			vm.setProp("videoTrackCount", Integer.toString(f.getFileData().getVideoTrackCount()));
			vm.setProp("captionTrackCount", Integer.toString(f.getFileData().getCaptionTrackCount()));
			vm.setProp("timecodeTrackCount", Integer.toString(f.getFileData().getTimecodeTrackCount()));
			vm.setProp("tdTrackCount", Integer.toString(f.getFileData().getTDCount()));
			vm.setProp("bdTrackCount", Integer.toString(f.getFileData().getBDCount()));

			HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> td = f.getFileData().getTDColumns();
			for (final String y : td.keySet()) {
				final LinkedHashMap<MXFColumn, MetadataColumnDef> m = td.get(y);
				vm.setTDElement(y, m);
			}

			HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> bd = f.getFileData().getBDColumns();
			for (final String y : bd.keySet()) {
				final LinkedHashMap<MXFColumn, MetadataColumnDef> m = bd.get(y);
				vm.setBDElement(y, m);
			}

			if (vm.getTDElementCount() > maxTD) maxTD = vm.getTDElementCount();
			if (vm.getBDElementCount() > maxBD) maxBD = vm.getBDElementCount();

			vm.setFileDescriptors(f.getFileData().getFileDescriptors());

			list.add(vm);
		}
		return FXCollections.observableArrayList(list);
	}
	
	public boolean addFileToList(FileInformation<MXFMetadata> fileInfo) {
		if (fileInfo == null) return false;
		fileList.add(fileInfo);
		return true;
	}
	
	public boolean addFileToList(String filePath) {
		LOGGER.info("Adding file to list: " + filePath);
		FileInformation<MXFMetadata> fileInfo = getFileInfo(filePath);
		if (fileInfo == null) return false;
		addFileToList(fileInfo);
		return true;
	}
	
	public FileInformation<MXFMetadata> getFileInfo(String filePath) {
		FileInformation<MXFMetadata> fileInfo = null;
        try {
        	MXFService service = new MXFServiceImpl(filePath);
	        fileInfo = service.getMetadata();
		} catch (PropertyNotPresentException pnpe) {
			LOGGER.log(Level.SEVERE, pnpe.toString(), pnpe);
			System.out.println("property not present exception: " + pnpe);
			pnpe.printStackTrace();
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			System.out.println("File not found exception in getFileInfo");
			e.printStackTrace();
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.toString(), ex);
			System.out.println("mxf file info exception: " + ex);
			ex.printStackTrace();
		}

        return fileInfo;
	}

	public int getMaxTD() {
		return maxTD;
	}

	public int getMaxBD() {
		return maxBD;
	}

	public static void updateValues(HashMap<MXFColumn, MetadataColumnDef> changedValues, MXFFileInformationViewModel fivm) {
		for (MXFColumn col : changedValues.keySet()) {
			fivm.getCoreData().put(col, changedValues.get(col));
			fivm.setProp(col.getDisplayName(), changedValues.get(col).getCurrentValue());
		}
		fivm.setEdited(true);
		for (FileInformation<MXFMetadata> file : fileList) {
			if (file.getPath().equals(fivm.getProp("path"))) {
				file.getFileData().setCoreColumns(fivm.getCoreData());
				file.setEdited(true);
			}
		}
	}

	public static void deleteSelectedRows(List<MXFFileInformationViewModel> selectedRows) {
		for (final MXFFileInformationViewModel fivm : selectedRows) {
			fileList.removeIf(file -> {
				return file.getPath().equals(fivm.getProp("path"));
			});
		}
	}
}
