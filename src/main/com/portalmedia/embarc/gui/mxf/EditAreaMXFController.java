package com.portalmedia.embarc.gui.mxf;

import java.net.URL;

import java.util.ResourceBundle;

import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.helper.MXFFileList;
import com.portalmedia.embarc.parser.SectionDef;
import com.portalmedia.embarc.parser.mxf.MXFSection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

/**
 * 
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-07-02
 */
public class EditAreaMXFController implements Initializable {
	@FXML
	private AnchorPane editAreaAnchorPane;
	@FXML
	private AnchorPane editAreaContainer;
	@FXML
	private ScrollPane mxfEditScrollPane;
	@FXML
	private Button writeMXFFilesButton;
	@FXML
	private ImageView fadgiLogoImageView;

	CoreMXFController coreView;
	WriteMXFController writeViewMXF;
	DescriptorMXFController descriptorMXF;
	TDBDController tdbdView;
	FileInfoController fileInfoView;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediatorMXF.getInstance().registerEditAreaController(this);

		fadgiLogoImageView.setFocusTraversable(true);
		fadgiLogoImageView.setAccessibleText("FADGI logo. F A D G I stands for Federal Agencies Digital Guidelines Initiative.");

		writeMXFFilesButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				WriteFilesModalControllerMXF.getInstance().showWriteFilesDialog();
			}
		});

		writeMXFFilesButton.setGraphicTextGap(20);

		ControllerMediatorMXF.getInstance().isEditingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> o, Boolean ov, Boolean nv) {
				if (writeViewMXF == null) return;
				setWriteFilesButtonsDisabled(nv);
			}
		});

		MXFFileList.getInstance().hasCoreRequiredFieldsErrorProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> o, Boolean ov, Boolean nv) {
				if (writeViewMXF == null) return;
				if (nv) {
					writeMXFFilesButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent e) {
							final Alert alert = new Alert(AlertType.NONE, "Fix missing required Core DMS fields marked with icon in Core DMS tab.", ButtonType.OK);
							alert.initModality(Modality.APPLICATION_MODAL);
							alert.initOwner(Main.getPrimaryStage());
							alert.setHeaderText("Cannot Write Files");
							alert.showAndWait();
							if (alert.getResult() == ButtonType.OK) {
								alert.close();
							}
						}
					});
				} else {
					writeMXFFilesButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent e) {
							WriteFilesModalControllerMXF.getInstance().showWriteFilesDialog();
						}
					});
				}
			}
		});
	}

	private void setWriteFilesButtonsDisabled(boolean isDisabled) {
		writeMXFFilesButton.setDisable(isDisabled);
		Main.getMXFMenuBar().getMenus().get(0).getItems().get(2).setDisable(isDisabled);
	}

	public void resetEditArea() {
		editAreaContainer.getChildren().removeAll(editAreaContainer.getChildren());
	}

	public void setWriteControl() {
		writeViewMXF = new WriteMXFController();
		writeViewMXF.setContent();
		AnchorPane.setTopAnchor(writeViewMXF, 0.0);
		AnchorPane.setLeftAnchor(writeViewMXF, 0.0);
		AnchorPane.setRightAnchor(writeViewMXF, 0.0);
		AnchorPane.setBottomAnchor(writeViewMXF, 0.0);
		editAreaContainer.getChildren().setAll(writeViewMXF);
	}

	public void setCoreControl() {
		coreView = new CoreMXFController();
		if (fileInfoView != null) fileInfoView.setVisible(false);
		coreView.setVisible(true);
		if (editAreaContainer.getChildren().size() > 0) {
			editAreaContainer.getChildren().removeAll(editAreaContainer.getChildren());
		}
		coreView.setSection(false);
		coreView.setTitle("Core DMS");
		AnchorPane.setTopAnchor(coreView, 0.0);
		AnchorPane.setLeftAnchor(coreView, 0.0);
		AnchorPane.setRightAnchor(coreView, 0.0);
		AnchorPane.setBottomAnchor(coreView, 0.0);
		editAreaContainer.getChildren().setAll(coreView);
	}

	public void setTDBDControl(SectionDef section) {
		if (section != MXFSection.TD && section != MXFSection.BD) return; // TODO: set file info?
		tdbdView = new TDBDController();
		tdbdView.setContent(section);
		AnchorPane.setTopAnchor(tdbdView, 0.0);
		AnchorPane.setLeftAnchor(tdbdView, 0.0);
		AnchorPane.setRightAnchor(tdbdView, 0.0);
		AnchorPane.setBottomAnchor(tdbdView, 0.0);
		editAreaContainer.getChildren().setAll(tdbdView);
	}

	public void setFileInfoControl() {
		fileInfoView = new FileInfoController();
		fileInfoView.setContent();
		AnchorPane.setTopAnchor(fileInfoView, 0.0);
		AnchorPane.setLeftAnchor(fileInfoView, 0.0);
		AnchorPane.setRightAnchor(fileInfoView, 0.0);
		AnchorPane.setBottomAnchor(fileInfoView, 0.0);
		editAreaContainer.getChildren().setAll(fileInfoView);
	}

	public void setDescriptorControl() {
		descriptorMXF = new DescriptorMXFController();
		descriptorMXF.setVisible(true);
		if (coreView != null) coreView.setVisible(false);
		if (editAreaContainer.getChildren().size() > 0) {
			editAreaContainer.getChildren().removeAll(editAreaContainer.getChildren());
		}
		descriptorMXF.setTitle("Descriptors");
		AnchorPane.setTopAnchor(descriptorMXF, 0.0);
		AnchorPane.setLeftAnchor(descriptorMXF, 0.0);
		AnchorPane.setRightAnchor(descriptorMXF, 0.0);
		AnchorPane.setBottomAnchor(descriptorMXF, 0.0);
		editAreaContainer.getChildren().setAll(descriptorMXF);
	}

} 