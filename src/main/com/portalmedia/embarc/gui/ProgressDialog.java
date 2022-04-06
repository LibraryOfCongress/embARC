package com.portalmedia.embarc.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Progress dialog modal to display process completion
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ProgressDialog {
	private final Alert dialogAlert;
	private final ProgressIndicator progressIndicator = new ProgressIndicator();
	private Node processCount;
	private final VBox vbox;

	public ProgressDialog() {
		dialogAlert = new Alert(AlertType.NONE);
		dialogAlert.initOwner(Main.getPrimaryStage());
		dialogAlert.setTitle("Processing Files");

		Pane progressPane = new Pane();
		HBox progressHBox = new HBox();
		progressIndicator.setProgress(-1F);
		progressIndicator.setVisible(true);

		vbox = new VBox();
		progressPane.getChildren().add(progressIndicator);
		progressHBox.getChildren().add(progressPane);
		progressHBox.setAlignment(Pos.CENTER);
		progressHBox.setPadding(new Insets(0,0,20,0));
		vbox.getChildren().add(progressHBox);
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.setSpacing(10);

		dialogAlert.getDialogPane().setPrefSize(300, 300);
		dialogAlert.getDialogPane().setPadding(new Insets(40,10,10,0));
		dialogAlert.getDialogPane().setContent(vbox);
	}

	public void activateProgressBar(final Task<?> task) {
		progressIndicator.progressProperty().bind(task.progressProperty());
		dialogAlert.show();
	}

	public void cancelProgressBar() {
		if (progressIndicator.progressProperty().isBound()) {
			progressIndicator.progressProperty().unbind();
		}
	}

	public Alert getDialogAlert() {
		return dialogAlert;
	}

	public void setCountLabel(Node processText) {
		processCount = processText;
		vbox.getChildren().add(processCount);
	}

	public void showCloseButton() {
		dialogAlert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
	}

	public void showLabels(List<Text> labels) {
		for (int i = 0; i < labels.size(); i++) {
			vbox.getChildren().add(labels.get(i));
		}
	}

	public void showExceptions(Map<String, Exception> exceptions) {
		if (exceptions.isEmpty()) return;
		Separator spacer = new Separator();
		vbox.getChildren().add(spacer);

		String titleStr = "";
		if (exceptions.size() > 1) {
			titleStr = "Errors have occurred. See details below.";
		} else {
			titleStr = "An error has occurred. See details below.";
		}
		Text title = new Text(titleStr);
		title.setTextAlignment(TextAlignment.CENTER);
		vbox.getChildren().add(title);

		TextArea textArea = new TextArea();

		for (Map.Entry<String, Exception> kv : exceptions.entrySet()) {
			String prev = textArea.getText();
			String curr = kv.getKey() + "\n" + kv.getValue().toString();
			if (!prev.isEmpty()) {
				textArea.setText(prev + "\n\n" + curr);
			} else {
				textArea.setText(curr);
			}
		}

		vbox.getChildren().add(textArea);

		Text part1 = new Text("If errors persist, please open an issue here:\n");
		part1.setTextAlignment(TextAlignment.CENTER);
		part1.setLineSpacing(-10);
		vbox.getChildren().add(part1);

		Hyperlink part2 = new Hyperlink("https://github.com/LibraryOfCongress/embARC/issues");
		part2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				URI uri;
				try {
					uri = new URI(part2.getText());
					openUrl(uri);
				} catch (final URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		vbox.getChildren().add(part2);

		Text part3 = new Text("\nor contact Kate Murray at kmur@loc.gov.");
		part3.setTextAlignment(TextAlignment.CENTER);
		part3.setLineSpacing(-10);
		vbox.getChildren().add(part3);

		dialogAlert.setResizable(true);
		dialogAlert.setWidth(400);
		dialogAlert.setHeight(500);
		dialogAlert.getDialogPane().setPrefSize(400, 500);
		dialogAlert.setResizable(false);
	}

	private void openUrl(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
