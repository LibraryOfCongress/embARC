package com.portalmedia.embarc.gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.portalmedia.embarc.database.DBService;
import com.portalmedia.embarc.gui.dpx.TopMenuBarDPX;
import com.portalmedia.embarc.gui.helper.CleanInputPathHelper;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.gui.mxf.TopMenuBarMXF;
import com.portalmedia.embarc.system.UserPreferences;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
	public static String embARCVersion = "";

	public static void main(String[] args) throws Exception {
		try {
			String os = System.getProperty("os.name");
			if (os != null && os.startsWith("Mac")) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("apple.awt.application.name", "embARC");
			}
			RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
			List<String> arguments = runtimeMxBean.getInputArguments();
			for(String a : arguments){
				LOGGER.info(a);
			}
			setupLogger();
			embARCVersion = getVersion();
			LOGGER.info("Starting embARC " + embARCVersion);
			launch(args);
		} catch (Exception ex) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Exception in Main.  Rethrowing", ex);
			throw ex;
		}
	}

	@Override
	public void init() {
		try {
			splashLayout = new DragBoard();
			splashLayout.setFocusTraversable(false);
			splashLayout.setAccessibleText("embARC " + embARCVersion + ". embARC stands for: Metadata Embedded for Archival Content.");
			splashLayout.setAccessibleHelp("Start the app by dragging and dropping DPX or MXF files here or use the file menu to import files");
			
			StackPane stack = new StackPane();
			stack.setFocusTraversable(false);
			
			ImageView splashImage = new ImageView(getClass().getResource("embARC.png").toURI().toString());
			splashImage.setFitHeight(600);
			splashImage.setFitWidth(600);
			splashImage.setFocusTraversable(false);
			
			VBox labelVBox = new VBox();
			labelVBox.setAlignment(Pos.CENTER);
			labelVBox.setFocusTraversable(false);
			
			Pane emptyPane = new Pane();
			emptyPane.setPrefHeight(200);
			emptyPane.setFocusTraversable(false);
			labelVBox.getChildren().add(emptyPane);
			
			Pane embarcLabelPane = new Pane();
			embarcLabelPane.setPrefHeight(200);
			embarcLabelPane.setPrefWidth(600);
			embarcLabelPane.setFocusTraversable(false);

			Label embarcLabel = new Label("embARC " + embARCVersion);
			embarcLabel.setFocusTraversable(true);
			embarcLabel.setFont(Font.font ("Verdana", 14));
			embarcLabel.setLayoutX(430);
			embarcLabel.setLayoutY(160);

			embarcLabelPane.getChildren().add(embarcLabel);
			labelVBox.getChildren().add(embarcLabelPane);

			Pane dropFilesLabelPane = new Pane();
			dropFilesLabelPane.setPrefHeight(200);
			dropFilesLabelPane.setPrefWidth(600);

			Label dropFilesLabel = new Label("Drag and drop DPX or MXF files here");
			dropFilesLabel.setFocusTraversable(true);
			dropFilesLabel.setAccessibleHelp("Or use the file menu to import files.");
			dropFilesLabel.setFont(Font.font ("Verdana", 20));
			dropFilesLabel.setTextFill(Color.rgb(0, 71, 110));
			dropFilesLabel.setTextAlignment(TextAlignment.CENTER);
			dropFilesLabel.setLayoutX(120);
			dropFilesLabel.setLayoutY(80);

			dropFilesLabelPane.getChildren().add(dropFilesLabel);
			labelVBox.getChildren().add(dropFilesLabelPane);

			stack.getChildren().addAll(splashImage, labelVBox);

			splashLayout.setCenter(stack);
			splashLayout.setStyle("-fx-background-color: transparent;");
		} catch (URISyntaxException e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Exception in init", e);
		} catch (Exception e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Exception in init", e);
		}
	}

	@Override
	public void start(final Stage initStage) throws Exception {
		// show splash screen immediately on start up
		showSplash(initStage);
		primaryStage = new Stage(StageStyle.DECORATED);
		primaryStage.setTitle("embARC primary stage title");

		URL splashScreenUrl = getClass().getResource("embARC.png");
		primaryStage.getIcons().add(new Image(splashScreenUrl.toURI().toString()));
		
		// setup "are you sure" modal on app quit
		setPrimaryStageCloseEvent();
		
		// load root fxml file
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/com/portalmedia/embarc/gui/Root.fxml"));
		rootLayout = loader.load();
		rootLayout.setAccessibleText("embARC Application ally text");
		rootLayout.setAccessibleHelp("embARC Application ally help");
		
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
			Path fullPath = Paths.get(logFileDir, "embARC.log");
			fileHandler = new FileHandler(fullPath.toString());
			System.out.println("log file location: " + fullPath);
			SimpleFormatter simple = new SimpleFormatter();
			fileHandler.setFormatter(simple);
			LOGGER.addHandler(fileHandler);
		} catch (IOException e) {
			System.out.println("IO Exception Error");
		}
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
		} catch (Exception ex) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Exception in showMainStageDPX", ex);
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
			LOGGER.log(java.util.logging.Level.SEVERE, "Exception in showMainStageMXF", e);
		}
	}

	private void setPrimaryStageCloseEvent() {
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				final String message = "Are you sure you want to quit embARC?";
				final Alert alert = AccessibleAlertHelper.CreateAccessibleAlert(
						"Quit embARC", 
						AlertType.CONFIRMATION, 
						message
					);
				
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.initOwner(primaryStage);
				alert.setGraphic(null);
				alert.setHeaderText(null);
				
				DialogPane dialogPane = alert.getDialogPane();				 
				alert.getDialogPane().lookupButton(ButtonType.OK).setAccessibleHelp(message);
				 	
				dialogPane.getStylesheets().add(getClass().getResource("/com/portalmedia/embarc/gui/application.css").toExternalForm());
				dialogPane.getStyleClass().add("alertDialog");	 
				

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					LOGGER.info("Closing embARC");
					/*if ("DPX".equals(programType)) {
						closeDpxFileInformationDatabase();
						closeUserPreferenceDatabase();
					}*/
					System.exit(0);
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
				/*if (!primaryStage.isShowing() || "DPX".equals(programType)) {
					closeDpxFileInformationDatabase();
					closeUserPreferenceDatabase();
				}*/
				System.exit(0);
			}
		});
	}
	
	private void closeDpxFileInformationDatabase() {
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
			LOGGER.log(java.util.logging.Level.SEVERE, "Error closing database", ex);
		}
		executor.shutdown();
	}
	
	private void closeUserPreferenceDatabase() {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		try {
			CompletableFuture.supplyAsync(() -> {
				DBService<UserPreferences> dbService = new DBService<UserPreferences>(UserPreferences.class);
				dbService.getSize();
				dbService.closeDB();
				System.out.println("Database UserPreferences closed");
				return null;
			}, executor).get(60, TimeUnit.SECONDS);
		} catch (Exception ex) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Error closing database", ex);
		}
		executor.shutdown();
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static Stage getSplashStage() {
		return splashStage;
	}

	public static MenuBar getMXFMenuBar() {
		return menuBarMXF;
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
	
	private static String getVersion() {
		Properties properties = new Properties();
		
		try (InputStream input = VersionUtil.class.getResourceAsStream("/resources/version.properties")) {
			if (input == null) {
				return "unknown";
			}
			properties.load(input);
			return properties.getProperty("version");
		} catch (IOException ex) {
			LOGGER.log(java.util.logging.Level.WARNING, "Exception in Main getVersion.", ex);
			return "unknown";
		}
	}

}
