package com.portalmedia.embarc.gui.mxf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.ProgressDialog;
import com.portalmedia.embarc.gui.helper.MXFFileList;
import com.portalmedia.embarc.parser.FileInformation;
import com.portalmedia.embarc.parser.mxf.MXFFileWriteResult;
import com.portalmedia.embarc.parser.mxf.MXFMetadata;
import com.portalmedia.embarc.parser.mxf.MXFService;
import com.portalmedia.embarc.parser.mxf.MXFServiceImpl;

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
public class WriteFilesDialogMXF extends Dialog {
	private boolean writeEditedOnly = false;
	private int success = 0;
	private int failures = 0;
	private Map<String, Exception> exceptions = new LinkedHashMap<String, Exception>();
	private List<FileInformation<MXFMetadata>> fileListCopy = new ArrayList<FileInformation<MXFMetadata>>();
	private static final Logger LOGGER = Logger.getLogger(Main.class.getClass().getName());

	public WriteFilesDialogMXF(String filePath, boolean writeEditedOnly) throws FileNotFoundException {
		this.writeEditedOnly = writeEditedOnly;
		writeFiles(filePath);
	}

	private void writeFiles(String filePath) throws FileNotFoundException {
		List<FileInformation<MXFMetadata>> originalFileList = MXFFileList.getList();
		
		if (!writeEditedOnly) {
			fileListCopy = new ArrayList<FileInformation<MXFMetadata>>(originalFileList);
		} else {
			for (FileInformation<MXFMetadata> file : originalFileList) {
				if (file.isEdited() && file.getFileShouldBeWritten()) fileListCopy.add(file);
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
							MXFFileWriteResult result = mxfService.writeFile(outputPath, file.getFileData().getCoreColumns());
							if (result.isSuccess()) {
								success++;
								file.setFileShouldBeWritten(false);
							} else {
								failures++;
								exceptions.put(exceptions.size() + 1 + ". " + file.getName(), result.getException());
							}
						} catch (IOException e) {
							LOGGER.log(Level.SEVERE, e.toString(), e);
						}
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
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

		final Text filesToProcess = new Text("Files to Write: " + Integer.toString(fileListCopy.size()));
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
			final List<Text> labels = new ArrayList<>();
			labels.add(new Text("Total Files Written: " + success));
			labels.add(new Text("Total Failures: " + failures));
			progressDialog.showLabels(labels);
			progressDialog.showExceptions(exceptions);
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
