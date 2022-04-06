package com.portalmedia.embarc.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.portalmedia.embarc.gui.dpx.ControllerMediatorDPX;
import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.helper.MXFFileList;
import com.portalmedia.embarc.gui.mxf.ControllerMediatorMXF;
import com.portalmedia.embarc.parser.FileFormat;
import com.portalmedia.embarc.parser.FileFormatDetection;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controls file process dialog
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-03-11
 */
public class FileProcessController {

	ProgressDialog progressDialog;
	List<String> validFileList = new ArrayList<>();
	List<String> notValidFileList = new ArrayList<>();
	List<String> validFileFailures = new ArrayList<>();
	int totalItemsCount;
	FileFormat fileFormat = FileFormat.OTHER;
	private static final Logger LOGGER = Logger.getLogger(Main.class.getClass().getName());

	private static class ControllerHolder {
		private static final FileProcessController INSTANCE = new FileProcessController();
	}

	public static FileProcessController getInstance() {
		return ControllerHolder.INSTANCE;
	}

	private FileProcessController() {}
	
	// Present user with dialog allowing them to choose files for upload
	public void chooseFile(Stage stage) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a file");
		final List<File> files = fileChooser.showOpenMultipleDialog(stage);

		if (files != null && !files.isEmpty()) {
			totalItemsCount = files.size();
			processFiles(files, null);
		}
	}

	// Present user with dialog to choose a directory of files to upload
	public void chooseDirectory(Stage stage) {
		final DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("Select a Directory");
		final File file = fileChooser.showDialog(stage);

		if (file != null) {
			// count total items found
			totalItemsCount = countFiles(file);

			final List<File> files = new ArrayList<>();
			files.add(file);
			processFiles(files, null);
		}
	}

	public void processFiles(List<File> files, DragEvent event) {
		LOGGER.info("Begin processFiles, file count: " + files.size());
		progressDialog = new ProgressDialog();
		if (!validFileFormat(files)) return;
		totalItemsCount = files.size();

		final Task<Void> task = createProcessFilesTask(files);
		final Label filesToProcess = new Label("Total items found: " + totalItemsCount);
		final FontAwesomeIconView question = new FontAwesomeIconView(FontAwesomeIcon.QUESTION_CIRCLE);
		filesToProcess.setTooltip(new Tooltip("This count includes all folders and hidden files"));
		filesToProcess.setGraphic(question);
		progressDialog.setCountLabel(filesToProcess);
		progressDialog.activateProgressBar(task);
		progressDialog.getDialogAlert().show();

		progressDialog.getDialogAlert().setOnCloseRequest(e -> {
			if (task.isRunning()) {
				task.cancel();
				if (validFileList.size() > 0) {
					validFileList.removeAll(validFileList);
					notValidFileList.removeAll(notValidFileList);
					validFileFailures.removeAll(validFileFailures);
					if (fileFormat == FileFormat.DPX) {
						ControllerMediatorDPX.getInstance().refetchFileList();
					} else if (fileFormat == FileFormat.MXF) {
						MXFFileList.getInstance().clearList();
					}
				}
			}
		});

		new Thread(task).start();

		task.setOnSucceeded(e -> {
			handleTaskResult(event);
		});

		task.setOnFailed(e -> {
			handleTaskResult(event);
		});
	}
	
	private void handleTaskResult(DragEvent event) {
		// remove failures from valid file list for reporting purposes
		if (validFileFailures.size() > 0) {
			for (final String f : validFileFailures) {
				validFileList.remove(f);
			}
		}

		// create file processing report labels for ProgressDialog
		final List<Text> labels = new ArrayList<>();
		final int total = validFileList.size() + validFileFailures.size() + notValidFileList.size();
		labels.add(new Text(fileFormat + " Successes: " + validFileList.size()));
		labels.add(new Text(fileFormat + " Failures: " + validFileFailures.size()));
		labels.add(new Text("Non-" + fileFormat + " Ignored: " + notValidFileList.size()));
		final Text totalProcessed = new Text("Total Files Processed: " + total);
		labels.add(totalProcessed);
		progressDialog.showLabels(labels);
		progressDialog.showCloseButton();

		// reset the lists and update the working file list
		validFileList.clear();
		notValidFileList.clear();
		validFileFailures.clear();

		if (fileFormat == FileFormat.DPX) {
			ControllerMediatorDPX.getInstance().refetchFileList();
		} else if (fileFormat == FileFormat.MXF) {
			ControllerMediatorMXF.getInstance().setFileList();
		}
		System.gc();

		if (event != null) {
			event.consume();
		}
	}


	private List<File> getAllFilesInPath(File file, List<File> fileList) {
		final Path folder = Paths.get(file.getAbsolutePath());
		if (!Files.isDirectory(folder)) {
			fileList.add(file);
		} else {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
				for (final Path filePath : stream) {
					if (Files.isHidden(filePath)) {
						continue;
					}
					if (Files.isDirectory(filePath)) {
						getAllFilesInPath(file, fileList);
					} else {
						File newFile = new File(filePath.toString());
						fileList.add(newFile);
					}
				}
			} catch (final IOException ex) {
				// An I/O problem has occurred
				LOGGER.log(Level.SEVERE, ex.toString(), ex);
				ex.printStackTrace();
			}
		}
		return fileList;
	}

	private Boolean validFileFormat(List<File> files) {
		List<File> fileList = new ArrayList<File>();
		for (File f : files) getAllFilesInPath(f, fileList);

		Boolean foundMXF = false;
		Boolean foundDPX = false;

		for (File file : fileList) {
			Boolean isMXF = FileFormatDetection.isMXF(file.getAbsolutePath());
			if (isMXF) {
				foundMXF = true;
				continue;
			}

			Boolean isDPX = FileFormatDetection.isDPX(file.getAbsolutePath());
			if (isDPX) {
				foundDPX = true;
				continue;
			}
		}

		if (!foundMXF && !foundDPX) {
			ErrorDialog errorDialog = new ErrorDialog("No DPX nor MXF files were found. Please import either DPX or MXF files.");
			errorDialog.getDialogStage().show();
			return false;
		}

		if (foundMXF && foundDPX) {
			ErrorDialog errorDialog = new ErrorDialog("Both DPX and MXF files were found. Please import either DPX or MXF files.");
			errorDialog.getDialogStage().show();
			return false;
		}

		if (foundDPX) {
			fileFormat = FileFormat.DPX;
			Main.showMainStageDPX(true);
		}

		if (foundMXF) {
			fileFormat = FileFormat.MXF;
			Main.showMainStageMXF(true);
		}

		return true;
	}
	
	private Task<Void> createProcessFilesTask(List<File> files) {
		final Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				System.currentTimeMillis();
				for (final File f : files) {
					getDirectoryContents(f.getAbsolutePath());
				}

				int count = 0;
				final int totalValidFiles = validFileList.size();
				int increment = 1;

				if (totalValidFiles > 10000) {
					increment = 50;
				} else if (totalValidFiles > 5000) {
					increment = 25;
				} else if (totalValidFiles > 1000) {
					increment = 10;
				}

				final int totalFiles = totalValidFiles + notValidFileList.size();
				double processed = 0;
				try {
					for (final String f : validFileList) {
						if (isCancelled()) break;
						
						if (fileFormat == FileFormat.DPX) {
							final boolean success = DPXFileListHelper.addFileToDatabase(f);
							count++;
							if (!success) { // This error is most likely a duplicate file path
								System.out.println("Error while adding " + fileFormat + " file to DB.");
								validFileFailures.add(f);
							}
							if (count == totalFiles || count % increment == 0) {
								processed = (count * 100) / totalFiles;
								updateProgress(processed, 100);
							}
						}
						
						if (fileFormat == FileFormat.MXF) {
							boolean success = MXFFileList.getInstance().addFileToList(f);
							count++;
							if (!success) {
								System.out.println("Error while adding " + fileFormat + " file to list.");
								validFileFailures.add(f);
							}
							if (count == totalFiles || count % increment == 0) {
								processed = (count * 100) / totalFiles;
								updateProgress(processed, 100);
							}
						}
					}
				} catch (final Exception ex) {
					LOGGER.log(Level.SEVERE, ex.toString(), ex);
					ex.printStackTrace();
				}
				count += notValidFileList.size();
				processed = (count * 100) / totalFiles;
				updateProgress(processed, 100);

				Runtime.getRuntime().gc();
				System.gc();
				return null;
			}
		};
		return task;
	}

	private void getDirectoryContents(String dir) {
		final Path folder = Paths.get(dir);
		if (!Files.isDirectory(folder)) {
			checkFileType(folder.toAbsolutePath().toString());
		} else {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
				for (final Path filePath : stream) {
					if (Files.isHidden(filePath)) {
						continue;
					}
					if (Files.isDirectory(filePath)) {
						getDirectoryContents(filePath.toAbsolutePath().toString());
					} else {
						checkFileType(filePath.toAbsolutePath().toString());
					}
				}
			} catch (final IOException ex) {
				// An I/O problem has occurred
				LOGGER.log(Level.SEVERE, ex.toString(), ex);
				ex.printStackTrace();
			}
		}
	}

	private void checkFileType(String f) {
		if (fileFormat == FileFormat.DPX) {
			if (FileFormatDetection.getFileFormat(f) == FileFormat.DPX) {
				validFileList.add(f);
			} else {
				notValidFileList.add(f);
			}
			return;
		}

		if (fileFormat == FileFormat.MXF) {
			if (FileFormatDetection.getFileFormat(f) == FileFormat.MXF) {
				validFileList.add(f);
			} else {
				notValidFileList.add(f);
			}
			return;
		}
	}

	public int countFiles(File directory) {
		int count = 0;
		final File[] files = directory.listFiles();
		if (files != null) {
			for (final File file : files) {
				if (file.isFile()) {
					count++;
				}
				if (file.isDirectory()) {
					count += countFiles(file);
				}
			}
		}
		return count;
	}

}
