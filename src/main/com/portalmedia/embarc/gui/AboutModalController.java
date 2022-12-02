package com.portalmedia.embarc.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controls about modal
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-03-11
 */
public class AboutModalController {

	private static class ControllerHolder {
		private static final AboutModalController INSTANCE = new AboutModalController();
	}

	public static AboutModalController getInstance() {
		return ControllerHolder.INSTANCE;
	}

	private AboutModalController() {}
	
	@FXML
	private Button closeAboutButton;
	@FXML
	private Button aboutButton;
	@FXML
	private Button licenseButton;
	@FXML
	private Pane aboutPane;
	@FXML
	private Pane licensePane;
	@FXML
	private Hyperlink fadgiLink;

	public void showAboutModal(Stage stage) {
		final Stage aboutStage = new Stage();
		aboutStage.initOwner(stage);
		aboutStage.setHeight(450);
		aboutStage.setWidth(670);
		aboutStage.initStyle(StageStyle.UTILITY);
		aboutStage.setResizable(false);
		aboutStage.setTitle("About embARC");

		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AboutModal.fxml"));
		fxmlLoader.setController(this);

		try {
			final Scene scene = new Scene(fxmlLoader.load());
			aboutStage.setScene(scene);
			aboutStage.show();
			fadgiLink.setBorder(Border.EMPTY);
			fadgiLink.setPadding(new Insets(4, 0, 4, 0));
			fadgiLink.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					URI uri;
					try {
						uri = new URI(fadgiLink.getText());
						openUrl(uri);
					} catch (final URISyntaxException e) {
						System.out.println("URI Syntax Error");
					}
				}
			});
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}

		closeAboutButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				aboutStage.close();
			}
		});

		aboutButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				aboutPane.setVisible(true);
				aboutPane.setPrefHeight(350);
				licensePane.setVisible(false);
				licensePane.setPrefHeight(0);
			}
		});

		licenseButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				aboutPane.setVisible(false);
				aboutPane.setPrefHeight(0);
				licensePane.setVisible(true);
				licensePane.setPrefHeight(350);
			}
		});
	}
	
	private void openUrl(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (final IOException e) {
			System.out.println("URI IO Error");
		}
	}

}
