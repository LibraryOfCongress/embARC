package com.portalmedia.embarc.gui.dpx;

import java.net.URL;
import java.util.ResourceBundle;

import com.portalmedia.embarc.parser.SectionDef;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Editor pane controller
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
@SuppressWarnings({ "unchecked" })
public class MetadataEditorController implements Initializable {

	@FXML
	private AnchorPane metadataEditorContainer;
	@FXML
	private AnchorPane editorContentSwapPane;
	@FXML
	private AnchorPane editorPane;
	@FXML
	private Tab editableTab;
	@FXML
	private Tab nonEditableTab;
	@FXML
	private Button writeFilesButton;
	@FXML
	private ImageView fadgiLogoImageView;

	private EditorForm editorFormPane;
	private GeneralForm generalFormPane;

	@SuppressWarnings("rawtypes")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediatorDPX.getInstance().registerMetadataEditorController(this);

		fadgiLogoImageView.setFocusTraversable(true);
		fadgiLogoImageView.setAccessibleText("FADGI logo. F A D G I stands for Federal Agencies Digital Guidelines Initiative.");

		ControllerMediatorDPX.getInstance().isEditingProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue o, Object ov, Object nv) {
				if (editorFormPane == null) {
					return;
				} else {
					writeFilesButton.setDisable((boolean) nv);
				}
			}
		});

		writeFilesButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				WriteFilesModalController.getInstance().showWriteFilesDialog();
			}
		});

		writeFilesButton.setGraphicTextGap(20);
	}

	public void refreshValidation() {
		if (editorFormPane != null) {
			editorFormPane.refreshValidation();
		}
	}

	public void resetEditControl(SectionDef section) {
		editorContentSwapPane.getChildren().removeAll(editorContentSwapPane.getChildren());
		setWriteControl(section);
	}

	public void setEditControl(SectionDef section) {
		editorFormPane = new EditorForm();
		if (generalFormPane != null) {
			generalFormPane.setVisible(false);
		}
		editorFormPane.setVisible(true);
		if (editorContentSwapPane.getChildren().size() > 0) {
			editorContentSwapPane.getChildren().removeAll(editorContentSwapPane.getChildren());
		}
		editorFormPane.setSection(section, false);
		editorFormPane.setTitle(section.getDisplayName());
		editorFormPane.setMaxWidth(Double.MAX_VALUE);
		AnchorPane.setTopAnchor(editorFormPane, 0.0);
		AnchorPane.setLeftAnchor(editorFormPane, 0.0);
		AnchorPane.setRightAnchor(editorFormPane, 0.0);
		AnchorPane.setBottomAnchor(editorFormPane, 0.0);

		editorContentSwapPane.getChildren().setAll(editorFormPane);
	}

	public void setGeneralControl() {
		generalFormPane = new GeneralForm();
		generalFormPane.setVisible(true);
		if (editorFormPane != null) {
			editorFormPane.setVisible(false);
		}
		if (editorContentSwapPane.getChildren().size() > 0) {
			editorContentSwapPane.getChildren().removeAll(editorContentSwapPane.getChildren());
		}
		generalFormPane.setTitle("General Information");
		generalFormPane.setMaxWidth(Double.MAX_VALUE);
		AnchorPane.setTopAnchor(generalFormPane, 0.0);
		AnchorPane.setLeftAnchor(generalFormPane, 0.0);
		AnchorPane.setRightAnchor(generalFormPane, 0.0);
		AnchorPane.setBottomAnchor(generalFormPane, 0.0);

		editorContentSwapPane.getChildren().setAll(generalFormPane);
	}

	public void setWriteControl(SectionDef section) {
		final WriteFilesView pane1 = new WriteFilesView();
		pane1.setMaxWidth(Double.MAX_VALUE);
		pane1.setMessage(section);
		AnchorPane.setTopAnchor(pane1, 0.0);
		AnchorPane.setLeftAnchor(pane1, 0.0);
		AnchorPane.setRightAnchor(pane1, 0.0);
		AnchorPane.setBottomAnchor(pane1, 0.0);

		editorContentSwapPane.getChildren().setAll(pane1);

	}
}
