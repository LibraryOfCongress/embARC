package com.portalmedia.embarc.gui.dpx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.portalmedia.embarc.gui.ProgressDialog;
import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXService;
import com.portalmedia.embarc.report.DPXReportService;
import com.portalmedia.embarc.report.HashReportValue;

import javafx.concurrent.Task;
import javafx.scene.control.Dialog;
import javafx.scene.text.Text;

/**
 * Dialog pane that allows user to write edited files, displays results
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class WriteFilesDialog extends Dialog {
	private boolean writeEditedOnly = false;
	private int success = 0;
	private int failures = 0;
	private final List<HashReportValue> report = new LinkedList<>();
	private boolean writeImageReport = false;

	public WriteFilesDialog(String filePath, String csvPath, boolean writeEditedOnly) {
		this.writeEditedOnly = writeEditedOnly;
		writeFiles(filePath, csvPath);
	}

	private void writeFiles(String filePath, String csvPath) {
		final List<DPXFileInformationViewModel> fileList = DPXFileListHelper.getAllFilesToWrite(writeEditedOnly);

		final Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				System.currentTimeMillis();
				if (fileList.size() == 0) {
					updateProgress(100, 100);
					cancel();
				}
				int count = 0;
				final int fileCount = fileList.size();
				double processed = 0;

				writeImageReport = csvPath != null && !csvPath.isEmpty() && fileCount > 0;
				report.clear();

				for (final DPXFileInformationViewModel file : fileList) {
					try {
						final String inputPath = file.getProp("path");
						String outputPath = inputPath;

						if (!filePath.isEmpty()) {
							outputPath = filePath + File.separator + file.getProp("name");
						}
						if (writeImageReport) {
							final String imageDataStartString = file.getProp(DPXColumn.OFFSET_TO_IMAGE_DATA);
							final int imageDataStart = Integer.parseInt(imageDataStartString);
							final byte[] originalImageData = DPXFileListHelper.getBytesFromFile(inputPath,
									imageDataStart);
							final String originalHash = DPXFileListHelper.getCrc32Hash(originalImageData);

							if (DPXService.writeFile(file, inputPath, outputPath)) {
								success++;
								DPXFileListHelper.setFileShouldBeWritten(file, false, true);
							} else {
								failures++;
							}

							final int newImageOffset = file.getOffsetToImageData();

							final byte[] newImageData = DPXFileListHelper.getBytesFromFile(outputPath, newImageOffset);
							final String newHash = DPXFileListHelper.getCrc32Hash(newImageData);
							report.add(new HashReportValue(outputPath, originalHash, newHash));
						} else {
							if (DPXService.writeFile(file, inputPath, outputPath)) {
								success++;
								DPXFileListHelper.setFileShouldBeWritten(file, false, true);
							} else {
								failures++;
							}
						}

					} catch (final Exception e) {
						System.out.println("Error writing file");
					}

					count++;
					processed = (count * 100) / fileCount;
					updateProgress(processed, 100);
				}
				try {
					if (writeImageReport && count > 0) {
						DPXReportService.WriteImageHashCsv(report, csvPath);
					}
				} catch (final IOException e) {
					System.out.println("Error writing hash report");
				}
				return null;
			}
		};

		final ProgressDialog progressDialog = new ProgressDialog();
		progressDialog.activateProgressBar(task);
		progressDialog.getDialogAlert().show();

		final Text filesToProcess = new Text("Files to Write: " + Integer.toString(fileList.size()));
		progressDialog.setCountLabel(filesToProcess);

		progressDialog.getDialogAlert().setOnCloseRequest(e -> {
			if (task.isRunning()) {
				System.out.println("WRITE FILES CANCELLED BY USER");
				task.cancel();
			}
		});

		new Thread(task).start();

		task.setOnSucceeded(e -> {
			final Text filesWritten = new Text("File Writing Complete!");
			progressDialog.setCountLabel(filesWritten);
			int matches = 0;
			for (final HashReportValue hrv : report) {
				String orgHash = hrv.getOriginalHash();
				String newHash = hrv.getNewHash();
				if (orgHash.equals(newHash)) matches++;
			}
			final List<Text> labels = new ArrayList<>();
			labels.add(new Text("Total Files Written: " + success));
			labels.add(new Text("Total Failures: " + failures));
			if (writeImageReport) {
				labels.add(new Text("Matching Image Checksums: " + matches));
			}
			progressDialog.showLabels(labels);
			progressDialog.showCloseButton();
		});

		task.setOnCancelled(e -> {
			final Text filesWritten = new Text("WRITE FILES CANCELLED");
			progressDialog.setCountLabel(filesWritten);
			final List<Text> labels = new ArrayList<>();
			labels.add(new Text("No edited files were found."));
			progressDialog.showLabels(labels);
			progressDialog.cancelProgressBar();
			progressDialog.showCloseButton();
		});
	}

}
