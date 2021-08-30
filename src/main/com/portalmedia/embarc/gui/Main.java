package com.portalmedia.embarc.gui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.portalmedia.embarc.database.DBService;
import com.portalmedia.embarc.gui.dpx.TopMenuBarDPX;
import com.portalmedia.embarc.gui.helper.CleanInputPathHelper;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.gui.mxf.TopMenuBarMXF;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Main class. Starts app, sets up layout, and creates menu bar items
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class Main extends Application {
	private static Stage splashStage;
	private static BorderPane splashLayout;
	private static Stage primaryStage;
	private static BorderPane rootLayout;
	private static MenuBar menuBarDPX = TopMenuBarDPX.createMenuBar();
	private static MenuBar menuBarMXF = TopMenuBarMXF.createMenuBar();
	private static MenuBar splashMenuBar = SplashMenuBar.createMenuBar();
	private static String programType = "";
	static Handler fileHandler = null;
	private static final Logger LOGGER = Logger.getLogger(Main.class.getClass().getName());

	public static void main(String[] args) throws Exception {
		setupLogger();
		LOGGER.info("Starting embARC");
		launch(args);
	}

	public static void setupLogger() {
		try {
			String logFileDir = "";

		    String os = System.getProperty("os.name");
			String cleanHomePath = CleanInputPathHelper.cleanString(System.getProperty("user.home"));
			if (os != null && os.startsWith("Mac")) {
				// MacOS log file location
				logFileDir = cleanHomePath + "/Library/Application Support/embARC";
			} else {
				// Windows log file location
				logFileDir = cleanHomePath + "/AppData/Local/embARC";
			}

		    File directory = new File(logFileDir);
		    if (!directory.exists()) directory.mkdir();
		    String fullPath = logFileDir + "/embARC.log";
		    fileHandler = new FileHandler(fullPath);
		    System.out.println("log file location: " + fullPath);
		    SimpleFormatter simple = new SimpleFormatter();
		    fileHandler.setFormatter(simple);
		    LOGGER.addHandler(fileHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		ImageView splashImage;
		try {
			splashImage = new ImageView(getClass().getResource("embARC.png").toURI().toString());
			splashImage.setFitHeight(600);
			splashImage.setFitWidth(600);

			splashLayout = new DragBoard();

			StackPane stack = new StackPane();

			Label embarcLabel = new Label("embARC v1.0.0");
			embarcLabel.setFont(Font.font ("Verdana", 14));
			embarcLabel.setPadding(new Insets(150,0,0,400));

			Label dropFiles = new Label("Drag and drop DPX or MXF files here");
			dropFiles.setFont(Font.font ("Verdana", 20));
			dropFiles.setTextFill(Color.rgb(0, 71, 110));
			dropFiles.setPadding(new Insets(400,0,0,0));

			stack.getChildren().addAll(splashImage, embarcLabel, dropFiles);

			splashLayout.setCenter(stack);
			splashLayout.setStyle("-fx-background-color: transparent;");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start(final Stage initStage) throws Exception {

		// show spash screen immediately on start up
		showSplash(initStage);
		primaryStage = new Stage(StageStyle.DECORATED);
		primaryStage.setTitle("embARC");

		URL splashScreenUrl = getClass().getResource("embARC.png");

		primaryStage.getIcons().add(new Image(splashScreenUrl.toURI().toString()));
		
		// setup "are you sure" modal on app quit
		setPrimaryStageCloseEvent();
		
		// load root fxml file
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/com/portalmedia/embarc/gui/Root.fxml"));
		rootLayout = loader.load();
		
		// set root scene, apply style sheet
		Scene scene = new Scene(rootLayout);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
	}

	private void showSplash(Stage initStage) {
		
		// create splash menu bar
		setSplashMenuBar();

		// initialize splash screen scene & stage
		Scene splashScene = new Scene(splashLayout);
		splashScene.setFill(Color.TRANSPARENT);
		initStage.initStyle(StageStyle.TRANSPARENT);
		initStage.setScene(splashScene);
		initStage.show();
		splashStage = initStage;
		setSplashScreenCloseRequest();
	}

	public static void hideSplash() {
		if (splashStage.isShowing()) splashStage.hide();
	}

	public static void showMainStageDPX(Boolean refresh) {
		LOGGER.info("Begin DPX GUI");
		programType = "DPX";
		DBService<DPXFileInformationViewModel> dbService = new DBService<DPXFileInformationViewModel>(DPXFileInformationViewModel.class);
		setMenuBarDPX(refresh);
		if (splashStage.isShowing()) splashStage.close();
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/com/portalmedia/embarc/gui/dpx/MainViewDPX.fxml"));
			AnchorPane mainWindow = (AnchorPane)loader.load();
			rootLayout.setCenter(mainWindow);
			dbService.getSize();
			dbService.dropCollection();
			dbService.closeDB();
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showMainStageMXF(Boolean refresh) {
		LOGGER.info("Begin MXF GUI");
		programType = "MXF";
		setMenuBarMXF(refresh);
		if (splashStage.isShowing()) splashStage.close();
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/com/portalmedia/embarc/gui/mxf/MainViewMXF.fxml"));
			AnchorPane mainWindow = (AnchorPane)loader.load();
			rootLayout.setCenter(mainWindow);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setPrimaryStageCloseEvent() {
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.initOwner(primaryStage);
				alert.setGraphic(null);
				alert.setTitle("Quit embARC");
				alert.setHeaderText(null);
				alert.setContentText("Are you sure you want to quit embARC?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					LOGGER.info("Closing embARC");
					if (programType == "DPX") closeDatabase();
				} else {
					e.consume();
				}
			}
		});
	}
	
	private void setSplashScreenCloseRequest() {
		splashStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				if (!primaryStage.isShowing() || programType == "DPX") closeDatabase();
			}
		});
	}
	
	private void closeDatabase() {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		try {
			CompletableFuture.supplyAsync(() -> {
				DBService<DPXFileInformationViewModel> dbService = new DBService<DPXFileInformationViewModel>(DPXFileInformationViewModel.class);
				dbService.getSize();
				dbService.dropCollection();
				dbService.closeDB();
				System.out.println("Database closed");
				return null;
			}, executor).get(60, TimeUnit.SECONDS);
		} catch (Exception ex) {
			System.out.println("Unable to close database");
		}
		executor.shutdown();
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static Stage getSplashStage() {
		return splashStage;
	}

	public static void setMenuBarDPX(Boolean refresh) {
		if (refresh) {
			rootLayout.getChildren().remove(menuBarDPX);
			menuBarDPX = null;
			menuBarDPX = TopMenuBarDPX.createMenuBar();
		}
		final String os = System.getProperty("os.name");
		if (os != null && os.startsWith("Mac")) {
			menuBarDPX.useSystemMenuBarProperty().set(true);
			rootLayout.getChildren().add(menuBarDPX);
		} else {
			rootLayout.setTop(menuBarDPX);
//			primaryStage.setFullScreen(true);
		}
	}
	
	public static void setMenuBarMXF(Boolean refresh) {
		if (refresh) {
			rootLayout.getChildren().remove(menuBarMXF);
			menuBarMXF = null;
			menuBarMXF = TopMenuBarMXF.createMenuBar();
		}
		final String os = System.getProperty("os.name");
		if (os != null && os.startsWith("Mac")) {
			menuBarMXF.useSystemMenuBarProperty().set(true);
			rootLayout.getChildren().add(menuBarMXF);
		} else {
			rootLayout.setTop(menuBarMXF);
//			primaryStage.setFullScreen(true);
		}
	}
	
	public static void setSplashMenuBar() {
		final String os = System.getProperty("os.name");
		if (os != null && os.startsWith("Mac")) {
			splashMenuBar.useSystemMenuBarProperty().set(true);
			splashLayout.getChildren().add(splashMenuBar);
		} else {
			splashLayout.setTop(splashMenuBar);
		}
	}
	
}
