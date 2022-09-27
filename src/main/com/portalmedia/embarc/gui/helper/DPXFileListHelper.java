package com.portalmedia.embarc.gui.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.dizitart.no2.objects.Cursor;

import com.google.cloud.Crc32c;
import com.portalmedia.embarc.database.DBService;
import com.portalmedia.embarc.gui.model.DatabaseSummary;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.gui.model.SelectedFilesSummary;
import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.MetadataColumn;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;
import com.portalmedia.embarc.parser.dpx.DPXMetadata;
import com.portalmedia.embarc.parser.dpx.DPXService;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Method for handling files to/from the database and other convenience methods
 * needed for table view in UI.
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXFileListHelper {
	static DBService<DPXFileInformationViewModel> dbService = new DBService<>(DPXFileInformationViewModel.class);

	private static int current_id = 0;
	private static boolean selectAll = false;

	private static void AddFileInformationViewModel(DPXFileInformation f) {
		final DPXFileInformationViewModel fivm = toFileInformationViewModel(f);
		DatabaseSummary.addFile(fivm);
		dbService.add(fivm);
	}

	public static <T> Boolean addFileToDatabase(String file) {
		if (file != null) {
			AddFileInformationViewModel(createDPXFileInformation(file));
		}
		return true;
	}

	public static <T> DPXFileInformation createDPXFileInformation(String filePath) {
		final File file = new File(filePath);
		final String name = file.getName();
		final String path = file.getPath();

		final DPXFileInformation fileInfo = new DPXFileInformation();
		fileInfo.setName(name);
		fileInfo.setPath(path);

		current_id++;
		fileInfo.setId(current_id);

		try {
			final DPXService service = new DPXService(file.getPath());
			fileInfo.setFileData(service.readFile());
		} catch (final Exception e) {
			System.out.println("Failed to parse file metadata");
			e.printStackTrace();
		}

		return fileInfo;
	}

	public static SelectedFilesSummary createSelectedFilesSummary(List<DPXFileInformationViewModel> selectedRows) {

		if (selectAll) {
			final Cursor<DPXFileInformationViewModel> files = dbService.getAllCursors();
			SelectedFilesSummary selectedFilesSummary = SelectedFilesSummary
					.create(new ArrayList<DPXFileInformationViewModel>());
			for (final DPXFileInformationViewModel fi : files) {
				selectedFilesSummary = SelectedFilesSummary.append(fi);
				if (!selectedFilesSummary.hasPossibleColumnsLeft()) {
					break;
				}
			}
			return selectedFilesSummary;
		} else {
			return SelectedFilesSummary.create(selectedRows);
		}
	}

	public static SelectedFilesSummary createSelectedFilesSummaryAll() {

		SelectedFilesSummary selectedFilesSummary = SelectedFilesSummary
				.create(new ArrayList<DPXFileInformationViewModel>());
		for (final DPXFileInformationViewModel fi : dbService.getAllCursors()) {
			selectedFilesSummary = SelectedFilesSummary.append(fi);
			if (!selectedFilesSummary.hasPossibleColumnsLeft()) {
				break;
			}
		}
		return selectedFilesSummary;

	}

	public static <T> Boolean deleteFileFromDB(int id) {
		System.out.println("Deleting file: " + id);
		try {
			dbService.delete(id);
		} catch (final Exception e) {
			System.out.println("Failed to delete file");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void deleteSelectedRows(List<DPXFileInformationViewModel> selectedRows) {
		if (selectAll) {
			System.out.println("Select all delete");
			dbService.deleteAll();
			DatabaseSummary.deleteAll();
		} else {
			for (final DPXFileInformationViewModel fivm : selectedRows) {
				dbService.delete(Integer.parseInt(fivm.getProp("id")));
				DatabaseSummary.removeFile(fivm);
			}
		}
	}

	public static Cursor<DPXFileInformationViewModel> getAllFiles(boolean editedOnly) {
		return editedOnly ? dbService.getEditedCursors() : dbService.getAllCursors();
	}

	public static List<DPXFileInformationViewModel> getAllFilesToWrite(boolean editedOnly) {
		Cursor<DPXFileInformationViewModel> fileList = editedOnly ? dbService.getEditedCursors() : dbService.getAllCursors();
		List<DPXFileInformationViewModel> finalFileList = new ArrayList<DPXFileInformationViewModel>();
		for (DPXFileInformationViewModel fivm : fileList) {
			boolean fileShouldBeWritten = fivm.getFileShouldBeWritten();
			if (fileShouldBeWritten) {
				finalFileList.add(fivm);
			}
		}
		return finalFileList;
	}

	public static byte[] getBytesFromFile(String filePath, int start) throws IOException {
		final File f = new File(filePath);
		final byte[] allBytes = Files.readAllBytes(f.toPath());
		final byte[] imageData = Arrays.copyOfRange(allBytes, start, allBytes.length);
		return imageData;
	}

	public static String getCrc32Hash(byte[] bytes) {
		try {
			final Crc32c c = new Crc32c();
			c.update(bytes, 0, bytes.length);
			return String.format("%02x", c.getValue());
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private static int getErrorCount(Set<ValidationRuleSetEnum> rules) {
		final long startTime = System.currentTimeMillis();
		final int size = dbService.getErrorCount(rules);
		System.out.println("Baseline getting error count " + (System.currentTimeMillis() - startTime) + "ms " + size);
		return size;
	}

	public static ObservableList<DPXFileInformationViewModel> getObservableFileList() {
		return FXCollections.observableArrayList(dbService.getAll());
	}

	public static long getSelectedFileCount(ObservableList<DPXFileInformationViewModel> selectedRows,
			Set<ValidationRuleSetEnum> rules, boolean filteredByErrors) {
		if (selectAll && filteredByErrors) {
			return getErrorCount(rules);
		}
		if (selectAll) {
			return dbService.getSize();
		} else {
			return selectedRows.size();
		}
	}

	public static int getTotalFiles() {
		return (int) dbService.getSize();
	}

	public static boolean selectAllSelected() {
		return selectAll;
	}

	public static void setSelectAll(boolean value) {
		selectAll = value;
	}

	private static DPXFileInformationViewModel toFileInformationViewModel(DPXFileInformation f) {
		final DPXFileInformationViewModel fivm = new DPXFileInformationViewModel();
		fivm.setProp("name", f.getName());
		fivm.setProp("path", f.getPath());
		fivm.setId(f.getId());
		final DPXMetadata dpx = f.getFileData();
		final LinkedHashMap<ColumnDef, MetadataColumn> columns = dpx.getMetadataHashMap();

		for (final ColumnDef key : columns.keySet()) {
			final MetadataColumnDef c = columns.get(key);
			fivm.setProp(key, c.toString());
		}
		return fivm;
	}

	public static void updateName(DPXFileInformationViewModel fivm) {
		fivm.autoPopulateName();
		dbService.update(fivm);
		DatabaseSummary.updateFile(fivm);
	}

	public static void updateValues(HashMap<DPXColumn, String> changedValues, DPXFileInformationViewModel file) {
		for (final DPXColumn column : changedValues.keySet()) {
			file.setProp(column, changedValues.get(column));
		}
		file.setEdited(true);
		setFileShouldBeWritten(file, true, false);
		dbService.update(file);
		DatabaseSummary.updateFile(file);
	}

	public static void setFileShouldBeWritten(DPXFileInformationViewModel file, boolean shouldBeWritten, boolean shouldUpdateDb) {
		file.setFileShouldBeWritten(shouldBeWritten);
		if (shouldUpdateDb) {
			dbService.update(file);
			DatabaseSummary.updateFile(file);
		}
	}

}
