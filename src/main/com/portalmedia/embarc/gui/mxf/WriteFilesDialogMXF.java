package com.portalmedia.embarc.gui.mxf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.ProgressDialog;
import com.portalmedia.embarc.gui.helper.MXFFileList;
import com.portalmedia.embarc.parser.FileInformation;
import com.portalmedia.embarc.parser.mxf.MXFMetadata;
import com.portalmedia.embarc.parser.mxf.MXFService;
import com.portalmedia.embarc.parser.mxf.MXFServiceImpl;
import com.portalmedia.embarc.report.HashReportValue;

import javafx.concurrent.Task;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

/**
 * Dialog pane that allows user to write edited files, displays results
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class WriteFilesDialogMXF extends Dialog {
	String filePath;
	String csvPath;
	boolean writeEditedOnly = false;
	int success = 0;
	int failures = 0;
	final List<HashReportValue> report = new LinkedList<>();
	List<FileInformation<MXFMetadata>> fileListCopy = new ArrayList<FileInformation<MXFMetadata>>();
	private static final Logger LOGGER = Logger.getLogger(Main.class.getClass().getName());

	public WriteFilesDialogMXF(String filePath, boolean writeEditedOnly) throws FileNotFoundException {
		this.filePath = filePath;
		this.writeEditedOnly = writeEditedOnly;
		writeFiles(filePath);
	}

	private void writeFiles(String filePath) throws FileNotFoundException {
		List<FileInformation<MXFMetadata>> originalFileList = MXFFileList.getList();
		
		if (!writeEditedOnly) {
			fileListCopy = new ArrayList<FileInformation<MXFMetadata>>(originalFileList);
		} else {
			for (FileInformation<MXFMetadata> file : originalFileList) {
				if (file.isEdited()) fileListCopy.add(file);
			}
		}

		final Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				System.currentTimeMillis();

				if (fileListCopy.isEmpty()) {
					updateProgress(100, 100);
					cancel();
				}

				int count = 0;
				final int fileCount = fileListCopy.size();
				double processed = 0;

				for (FileInformation<MXFMetadata> file : fileListCopy) {
					try {
						if (writeEditedOnly && !file.isEdited()) continue;

						final String inputPath = file.getPath();
						String outputPath = inputPath;

						if (!filePath.isEmpty()) {
							outputPath = filePath + File.separator + file.getName();
						}

						LOGGER.info("Writing " + inputPath + " to " + outputPath);

						try {
							MXFService mxfService = new MXFServiceImpl(inputPath);
							boolean isSuccess = mxfService.writeFile(outputPath, file.getFileData().getCoreColumns());
							if (isSuccess) {
								success++;
							} else {
								failures++;
							}
						} catch (IOException e) {
							LOGGER.log(Level.SEVERE, e.toString(), e);
							e.printStackTrace();
						}
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
						e.printStackTrace();
					}

					count++;
					processed = (count * 100) / fileCount;
					updateProgress(processed, 100);
				}
				return null;
			}
		};

		final ProgressDialog progressDialog = new ProgressDialog();
		progressDialog.activateProgressBar(task);
		progressDialog.getDialogAlert().show();

		final Label filesToProcess = new Label("Files to Write: " + Integer.toString(fileListCopy.size()));
		progressDialog.setCountLabel(filesToProcess);

		progressDialog.getDialogAlert().setOnCloseRequest(e -> {
			if (task.isRunning()) {
				System.out.println("WRITE FILES CANCELLED BY USER");
				task.cancel();
			}
		});

		new Thread(task).start();

		task.setOnSucceeded(e -> {
			final Label filesWritten = new Label("File Writing Complete!");
			progressDialog.setCountLabel(filesWritten);
			final List<Label> labels = new ArrayList<>();
			labels.add(new Label("Total Files Written: " + success));
			labels.add(new Label("Total Failures: " + failures));
			progressDialog.showLabels(labels);
			progressDialog.showCloseButton();
		});

		task.setOnCancelled(e -> {
			final Label filesWritten = new Label("WRITE FILES CANCELLED");
			progressDialog.setCountLabel(filesWritten);
			final List<Label> labels = new ArrayList<>();
			labels.add(new Label("No edited files were found."));
			progressDialog.showLabels(labels);
			progressDialog.cancelProgressBar();
			progressDialog.showCloseButton();
		});
		
	}

}
