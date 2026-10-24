package com.portalmedia.embarc.system;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.parser.FileInformation;
import com.portalmedia.embarc.parser.mxf.MXFMetadata;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/**
 * Utility class to check available disk space before writing files
 * 
 * @author PortalMedia
 * @version 1.0
 * @since 2025-05-11
 */
public class DiskSpaceChecker {
    private static final Logger LOGGER = Logger.getLogger(DiskSpaceChecker.class.getName());
    
    /**
     * Checks if there is enough disk space available in the target directory for DPX files
     * 
     * @param targetDir The directory where files will be written
     * @param fileList List of DPX files to be written
     * @return true if user confirms to proceed or if there's enough space, false otherwise
     */
    public static boolean checkDiskSpaceForDPX(String targetDir, List<DPXFileInformationViewModel> fileList) {
        if (targetDir == null || targetDir.isEmpty() || fileList == null || fileList.isEmpty()) {
            return true;
        }
        
        File directory = new File(targetDir);
        if (!directory.exists()) {
            // Create directory if it doesn't exist
            if (!directory.mkdirs()) {
                LOGGER.log(Level.WARNING, "Failed to create directory: " + targetDir);
                return true; // Continue anyway as this will be handled during file writing
            }
        }
        
        // Calculate total size of files to be written
        long totalSize = 0;
        for (DPXFileInformationViewModel file : fileList) {
            try {
                String filePath = file.getProp("path");
                if (filePath != null && !filePath.isEmpty()) {
                    File sourceFile = new File(filePath);
                    if (sourceFile.exists()) {
                        totalSize += sourceFile.length();
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error calculating file size", e);
            }
        }
        
        // Get available space in target directory
        long availableSpace = directory.getFreeSpace();
        
        // If available space is less than total size, show confirmation dialog
        if (availableSpace < totalSize) {
            LOGGER.log(Level.SEVERE, "Available space is less than total size");
            return showConfirmationDialog(totalSize, availableSpace);
        }
        
        return true;
    }
    
    /**
     * Checks if there is enough disk space available in the target directory for MXF files
     * 
     * @param targetDir The directory where files will be written
     * @param fileList List of MXF files to be written
     * @return true if user confirms to proceed or if there's enough space, false otherwise
     */
    public static boolean checkDiskSpaceForMXF(String targetDir, List<FileInformation<MXFMetadata>> fileList) {
        if (targetDir == null || targetDir.isEmpty() || fileList == null || fileList.isEmpty()) {
            return true;
        }
        
        File directory = new File(targetDir);
        if (!directory.exists()) {
            // Create directory if it doesn't exist
            if (!directory.mkdirs()) {
                LOGGER.log(Level.WARNING, "Failed to create directory: " + targetDir);
                return true; // Continue anyway as this will be handled during file writing
            }
        }
        
        // Calculate total size of files to be written
        long totalSize = 0;
        for (FileInformation<MXFMetadata> file : fileList) {
            try {
                totalSize += file.getFileData().getFileSize();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error calculating file size", e);
            }
        }
        
        // Get available space in target directory
        long availableSpace = directory.getFreeSpace();
        
        // If available space is less than total size, show confirmation dialog
        if (availableSpace < totalSize) {
            return showConfirmationDialog(totalSize, availableSpace);
        }
        
        return true;
    }
    
    /**
     * Shows a confirmation dialog to the user when there's not enough disk space
     * 
     * @param requiredSpace The amount of space required for the files
     * @param availableSpace The amount of space available on the disk
     * @return true if user confirms to proceed, false otherwise
     */
    private static boolean showConfirmationDialog(long requiredSpace, long availableSpace) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Disk Space Warning");
        alert.setHeaderText("Insufficient Disk Space");
        
        String formattedRequired = formatSize(requiredSpace);
        String formattedAvailable = formatSize(availableSpace);
        
        alert.setContentText("embARC is about to write " + formattedRequired + " of data, but only " + 
                formattedAvailable + " is available on the target drive. Do you want to continue?");
        
        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonData.NO);
        
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        alert.initOwner(Main.getPrimaryStage());
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }
    
    /**
     * Formats a size in bytes to a human-readable string (KB, MB, GB, TB)
     * 
     * @param size Size in bytes
     * @return Formatted string
     */
    private static String formatSize(long size) {
        if (size <= 0) return "0 B";
        
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        
        if (digitGroups >= units.length) {
            digitGroups = units.length - 1;
        }
        
        DecimalFormat df = new DecimalFormat("#,##0.##");
        return df.format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
