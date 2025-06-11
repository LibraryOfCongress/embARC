package com.portalmedia.embarc.gui.dpx;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import com.portalmedia.embarc.gui.AccessibleAlertHelper;
import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.ProgressSpinner;
import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.helper.RowNumberManager;
import com.portalmedia.embarc.gui.model.DPXFileInformationViewModel;
import com.portalmedia.embarc.gui.model.DPXMetadataColumnViewModel;
import com.portalmedia.embarc.gui.model.SelectedFilesSummary;
import com.portalmedia.embarc.gui.model.TabSummary;
import com.portalmedia.embarc.gui.model.TableState;
import com.portalmedia.embarc.parser.SectionDef;
import com.portalmedia.embarc.parser.dpx.DPXBatchProcessor;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXColumnHelpText;
import com.portalmedia.embarc.parser.dpx.DPXImageElement;
import com.portalmedia.embarc.parser.dpx.DPXMetadataColumnViewModelList;
import com.portalmedia.embarc.parser.dpx.DPXSection;
import com.portalmedia.embarc.parser.dpx.DPXSequenceError;
import com.portalmedia.embarc.system.UserPreferencesService;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;

/**
 * The CenterPaneController handles all UI interaction with TableView portion of
 * the application, and acts as a bridge between the files metadata objects
 * stored in the table and the database
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class CenterPaneController implements Initializable {
	@FXML
	private BorderPane borderPane;
	@FXML
	private TableView<DPXFileInformationViewModel> table;
	@FXML
	private TabPane tabPane;
	@FXML
	private Tab generalTab;
	@FXML
	private Tab fileInformationTab;
	@FXML
	private Tab imageInformationTab;
	@FXML
	private Tab imageSourceInformationTab;
	@FXML
	private Tab motionPictureFilmTab;
	@FXML
	private Tab televisionTab;
	@FXML
	private Tab userDefinedTab;
	@FXML
	private SectionDef selectedSection;

	private SelectedFilesSummary selectedFilesSummary;
	private TableViewSelectionModel<DPXFileInformationViewModel> tableSelectionModel;
	private Boolean columnsHaveBeenSet = false;
	private List<String> shownImageElements;
	private List<TableColumn<DPXFileInformationViewModel, ?>> userHiddenColumns = new ArrayList<>();
	private TabSummary tabSummary;
	private boolean filteredByError = false;
	private boolean userPreferencesSet = false;
	private List<DPXSequenceError> filenameSequenceErrorList = new ArrayList<DPXSequenceError>();
	private List<DPXSequenceError> imageFilenameSequenceErrorList = new ArrayList<DPXSequenceError>();
	private List<DPXSequenceError> sourceImageFilenameSequenceErrorList = new ArrayList<DPXSequenceError>();
	private List<DPXSequenceError> framePositionSequenceErrorList = new ArrayList<DPXSequenceError>();

	private final String TAB_VALIDATION_WARNING = " Contains validation error";
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediatorDPX.getInstance().registerCenterPaneController(this);
		table.setFocusTraversable(true);
		
		table.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
            	if(event.getCode().equals(KeyCode.ENTER)) {
            		TableColumn<DPXFileInformationViewModel, ?> focusedColumn = table.getFocusModel().getFocusedCell().getTableColumn();
            		if(focusedColumn != null && focusedColumn.isSortable()) {
            			if(focusedColumn.getSortType() == SortType.ASCENDING) {
            				focusedColumn.setSortType(SortType.DESCENDING);
            			} else {
            				focusedColumn.setSortType(SortType.ASCENDING);
            			}
            			table.getSortOrder().clear();
            			table.getSortOrder().add(focusedColumn);
            		} 		
            		event.consume();
            	}
            }
		});
				
		tableSelectionModel = table.getSelectionModel();
		tableSelectionModel.getSelectedItems()
				.addListener((ListChangeListener.Change<? extends DPXFileInformationViewModel> change) -> {
					final ObservableList<DPXFileInformationViewModel> files = table.getSelectionModel().getSelectedItems();
					boolean selectAllChanged = false;

					final boolean selectAll = files.size() == table.getItems().size();
					if (TableState.isSelectAll() != selectAll) {
						selectAllChanged = true;
					}
					TableState.setSelectAll(selectAll);
					DPXFileListHelper.setSelectAll(selectAll);

					ControllerMediatorDPX.getInstance().setSelectedFileList(files);

					if ((!TableState.isSelectAll() && !TableState.getDeleteFiles()) || selectAllChanged) {
						TableState.setUpdateSummary(true);
						TableState.setUpdateEditor(true);
						updateData();
						updateTable();
					}
				});
		tableSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
		tabSummary = new TabSummary();
		shownImageElements = new LinkedList<>();
		shownImageElements.add(DPXImageElement.IMAGE_ELEMENT_1.getDisplayName());
		shownImageElements.add(DPXImageElement.IMAGE_ELEMENT_2.getDisplayName());
		shownImageElements.add(DPXImageElement.IMAGE_ELEMENT_3.getDisplayName());
		shownImageElements.add(DPXImageElement.IMAGE_ELEMENT_4.getDisplayName());
		shownImageElements.add(DPXImageElement.IMAGE_ELEMENT_5.getDisplayName());
		shownImageElements.add(DPXImageElement.IMAGE_ELEMENT_6.getDisplayName());
		shownImageElements.add(DPXImageElement.IMAGE_ELEMENT_7.getDisplayName());
		shownImageElements.add(DPXImageElement.IMAGE_ELEMENT_8.getDisplayName());
		setTabClickListener();
		setIsEditingListener();
		refreshEditor(false);
	}

	private void initUserPreferences() {
		if (!userPreferencesSet) {
			final UserPreferencesService userPreferences = new UserPreferencesService();
			final List<String> hiddenColNames = userPreferences.getHiddenDPXColumns();

			for (final TableColumn<DPXFileInformationViewModel, ?> column : table.getColumns()) {
				if (hiddenColNames.contains(column.getId())) {
					if (!userHiddenColumns.contains(column)) {
						column.setVisible(false);
						userHiddenColumns.add(column);
					}
				}
			}
			userPreferencesSet = true;
		}
	}

	public void refreshEditor(boolean updateSummary) {
		if (updateSummary) {
			selectedFilesSummary = DPXFileListHelper
					.createSelectedFilesSummary(table.getSelectionModel().getSelectedItems());
			setSelectedRuleSets(ControllerMediatorDPX.getInstance().getCurrentRuleSets());
			setTabWarnings();
			setColumnSequenceGapWarnings();
		}
		
		final int selectedRowCount = table.getSelectionModel().getSelectedItems().size();
		if (selectedRowCount > 0 && selectedSection != null) {
			ControllerMediatorDPX.getInstance().setEditor(selectedSection);
		} else if (selectedRowCount > 0) {
			ControllerMediatorDPX.getInstance().resetEditor(selectedSection);
			ControllerMediatorDPX.getInstance().setGeneralEditor();		
		} else {
			ControllerMediatorDPX.getInstance().resetEditor(selectedSection);
		}	
	}

	public void refreshValidation() {
		table.refresh();
	}

	public void selectAllFiles() {
		final ProgressSpinner spinner = new ProgressSpinner();

		final Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				TableState.setSelectAll(true);
				TableState.setUpdateSummary(true);
				TableState.setUpdateEditor(true);
				DPXFileListHelper.setSelectAll(true);
				updateData();
				return null;
			}
		};

		task.setOnSucceeded(event -> {
			Platform.runLater(() -> {
				updateTable();
			});
			spinner.getDialogStage().hide();
		});

		final Thread thread = new Thread(task);
		thread.start();

		spinner.getDialogStage().show();
	}

	private void selectSection(SectionDef section) {
		this.selectedSection = section;
		refreshEditor(false);
	}

	private void setColumnContextMenu(TableColumn<DPXFileInformationViewModel, String> col) {
		final ContextMenu cm = new ContextMenu();
		cm.setStyle("-fx-font-size: 12px");
		final MenuItem mi1 = new MenuItem("Auto Populate Name");
		mi1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				autoPopulateNames();
			}
		});
		cm.getItems().add(mi1);
		col.setContextMenu(cm);
	}

	private void setColumns() {
		if (table.getItems().size() != 0) {
			setGeneralColumns();
			String helpText;
			final DPXMetadataColumnViewModelList columnList = DPXMetadataColumnViewModelList.getInstance();
			for (final DPXMetadataColumnViewModel mcvm : columnList.getColumns()) {
				helpText = DPXColumnHelpText.getInstance().getHelpText(mcvm.getColumn());
				if (!mcvm.getHasSubsection()) {
					final TableColumn<DPXFileInformationViewModel, String> col = new TableColumn<>(mcvm.getDisplayName());
					String tempColName = mcvm.getDisplayName() + mcvm.getSectionDisplayName();
					final String colName = tempColName;
					col.setCellValueFactory(
							cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProp(colName)));

					col.setUserData(mcvm);
					col.setVisible(false);
					col.setCellFactory(column -> {
						return new ValidationCellFactory(column);
					});
					if (mcvm.getColumn() == DPXColumn.IMAGE_FILE_NAME) {
						setColumnContextMenu(col);
					}
					setColumnWidth(col);
					setColumnHeader(col, helpText, mcvm.getColumn().getDisplayName());
					col.setSortable(true);
					col.setId(mcvm.getDisplayName());
					setColumnStyles(col, mcvm);
					table.getColumns().add(col);
				} else {
					final TableColumn<DPXFileInformationViewModel, String> subCol = new TableColumn<>(
							mcvm.getDisplayName());
					String tempColName = mcvm.getDisplayName() + mcvm.getSectionDisplayName();
					tempColName += mcvm.getSubsectionName();
					final String colName = tempColName;
					subCol.setCellValueFactory(
							cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProp(colName)));
					subCol.setUserData(mcvm);
					subCol.setVisible(false);

					subCol.setCellFactory(column -> {
						return new ValidationCellFactory(column);
					});
					
					setColumnWidth(subCol);
					String sectionName = mcvm.getSubsectionName();
					String imageNumber = sectionName.substring(sectionName.length()-1);
					String subSecDisplayName = imageNumber.concat(" ").concat(mcvm.getDisplayName());
					setColumnHeader(subCol, "", subSecDisplayName);
					subCol.setSortable(true);
					subCol.setId(subSecDisplayName);
					setColumnStyles(subCol, mcvm);

					table.getColumns().add(subCol);
				}
			}

			columnsHaveBeenSet = true;
			ControllerMediatorDPX.getInstance().notifyColumnsSet();
			initUserPreferences();
		}
	}

	private void setColumnStyles(TableColumn<DPXFileInformationViewModel, String> col, DPXMetadataColumnViewModel mcvm) {
		if (mcvm.isEditable()) {
			col.getStyleClass().add("editable-column");
		} else {
			col.getStyleClass().add("uneditable-column");
		}
	}
	
	private void setColumnHeader(
			TableColumn<DPXFileInformationViewModel, ?> col,
			String helpText, 
			String columnDisplayName) {
		setColumnHeader(col, helpText, columnDisplayName, false);
	}
	
	private void setColumnHeader(
			TableColumn<DPXFileInformationViewModel, ?> col,
			String helpText, 
			String columnDisplayName,
			boolean parentOfSubheaders) {
		HBox hbox = new HBox();
		final String defaultStyle = "-fx-font-size: 12px";
		final Label title = new Label(columnDisplayName);
		title.setStyle(defaultStyle);
		final Tooltip tt = new Tooltip(columnDisplayName + "\n\n" + helpText);
		tt.setStyle("-fx-font-size: 12px");
		tt.setPrefWidth(500);
		tt.setWrapText(true);
		tt.setAutoHide(false);
		title.setTooltip(tt);
		setColumnHeaderSequenceErrorIndicator(columnDisplayName, title, hbox);
		if(parentOfSubheaders) {
			new FontAwesomeIconView(FontAwesomeIcon.CARET_DOWN);
			title.setStyle("-fx-padding: 0 0 0 5; -fx-font-size: 12px");
			final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.CARET_DOWN);
			hbox.getChildren().add(icon);
		} 
		
		hbox.getChildren().add(title);
		hbox.setAlignment(Pos.CENTER);
		hbox.setStyle(defaultStyle);
		col.setText(columnDisplayName);
		col.setGraphic(hbox);	
		col.setStyle("-fx-padding: 0 0 0 5;");
	}
	
	private void setColumnHeaderSequenceErrorIndicator(String columnDisplayName, Label title, HBox hbox) {
		if (columnDisplayName == "Filename" &&
			!filenameSequenceErrorList.isEmpty()
		) {
			setSequenceErrorIcon(title, hbox);
		}
		if (columnDisplayName == DPXColumn.IMAGE_FILE_NAME.getDisplayName() &&
			!imageFilenameSequenceErrorList.isEmpty()
		) {
			setSequenceErrorIcon(title, hbox);
		}
		if (columnDisplayName == DPXColumn.SOURCE_IMAGE_FILENAME.getDisplayName() &&
			!sourceImageFilenameSequenceErrorList.isEmpty()
		) {
			setSequenceErrorIcon(title, hbox);
		}
		if (columnDisplayName == DPXColumn.FRAME_POSITION_IN_SEQUENCE.getDisplayName() &&
			!framePositionSequenceErrorList.isEmpty()
		) {
			setSequenceErrorIcon(title, hbox);
		}
	}
	
	private void setSequenceErrorIcon(Label title, HBox hbox) {
		title.setStyle("-fx-padding: 0 0 0 5; -fx-font-size: 12px");
		final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION);
		icon.setStyleClass("fadgi-sr-warning");
		hbox.getChildren().add(icon);
	}

	private void setColumnWidth(TableColumn<DPXFileInformationViewModel, String> col) {
		col.setPrefWidth(125);
	}

	public void setFiles() {
		final ObservableList<DPXFileInformationViewModel> ofl = DPXFileListHelper.getObservableFileList();
		final FilteredList<DPXFileInformationViewModel> filteredList = new FilteredList<>(ofl);
		
		filteredList.setPredicate(new Predicate<DPXFileInformationViewModel>() {
			@Override
			public boolean test(DPXFileInformationViewModel t) {
				return (t.hasError(ControllerMediatorDPX.getInstance().getCurrentRuleSets()) && filteredByError)
						|| !filteredByError; // or true
			}
		});
		SortedList<DPXFileInformationViewModel> sortedData = new SortedList<>(filteredList);
		sortedData.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedData);
		setSequenceErrorLists();
		if (!columnsHaveBeenSet) {
			setColumns();
		}
	}

	private void setGeneralColumns() {
		final TableColumn<DPXFileInformationViewModel, String> idCol = new TableColumn<>("Row");
		idCol.setId("Row");
		idCol.setUserData("General");
		idCol.getStyleClass().add("general-section");

		idCol.setCellValueFactory(
				new Callback<CellDataFeatures<DPXFileInformationViewModel, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<DPXFileInformationViewModel, String> p) {

						final int id = p.getValue().getId();
						final int rowNumber = (table.getItems().indexOf(p.getValue()) + 1);
						RowNumberManager.addRow(id, rowNumber);

						return new ReadOnlyObjectWrapper<>(rowNumber + "");
					}
				});
		idCol.setSortable(false);
		idCol.setPrefWidth(50);
		setColumnHeader(idCol, "", "Row");

		final TableColumn<DPXFileInformationViewModel, String> nameCol = new TableColumn<>("Filename");
		nameCol.setId("Filename");
		nameCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProp("name")));
		nameCol.setUserData("General");
		nameCol.getStyleClass().add("general-section");
		setColumnWidth(nameCol);
		setColumnHeader(nameCol, "", "Filename");

		final TableColumn<DPXFileInformationViewModel, String> pathCol = new TableColumn<>("File Path");
		pathCol.setId("File Path");
		pathCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProp("path")));
		pathCol.setUserData("General");
		pathCol.getStyleClass().add("general-section");
		setColumnWidth(pathCol);
		setColumnHeader(pathCol, "", "File Path");

		table.getColumns().add(idCol);
		table.getColumns().add(nameCol);
		table.getColumns().add(pathCol);
	}

	private void setIsEditingListener() {
		ControllerMediatorDPX.getInstance().isEditingProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> o, Object ov, Object nv) {
				if ((boolean) nv) {
//					table.setDisable(true);
				} else {
//					table.setDisable(false);
				}
			}
		});
	}

	public void setSelectedRuleSets(HashSet<ValidationRuleSetEnum> selectedRuleSets) {
		if (selectedFilesSummary != null) {
			selectedFilesSummary.setValidRuleSets(selectedRuleSets);
		}
		TabSummary.start();

		for (final DPXFileInformationViewModel fivm : DPXFileListHelper.getAllFiles(false)) {
			TabSummary.append(fivm, selectedRuleSets);
		}
		tabSummary = TabSummary.getTabSummary();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setTabWarnings();
				setColumnSequenceGapWarnings();
			}
		});
	}


	private void setTabClickListener() {
		tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			if (table.getItems().size() > 0) {
				tabPane.requestFocus(); //Covers special case where navigating with VoiceOver VO+-> key combo does not keep focus on tab pane
				switch (newTab.getId()) {
				case "generalTab":
					for (final TableColumn<DPXFileInformationViewModel, ?> col : table.getColumns()) {
						if (!col.getUserData().toString().equals("General") && !userHiddenColumns.contains(col)) {
							col.setVisible(false);
							
						} else {
							col.setVisible(true);
						}
					}
					table.scrollToColumnIndex(0);
					selectedSection = null;
					selectSection(selectedSection);
					break;
				case "fileInformationTab":
					for (final TableColumn<DPXFileInformationViewModel, ?> col : table.getColumns()) {
						final String tmpValue = col.getUserData().toString();
						if (tmpValue.equals("General")) {
							if ((col.getId().equals("Filename") || col.getId().equals("Row"))
									&& !userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
							continue;
						}
						final DPXMetadataColumnViewModel mcvm = (DPXMetadataColumnViewModel) col.getUserData();
						if (mcvm == null) {
							continue;
						}
						if (mcvm.getSectionDisplayName().equals("File Information") || col.getId().equals("Filename")
								|| col.getId().equals("Row")) {
							if (!userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
						} else if (col.isVisible()) {
							col.setVisible(false);
						}
					}
					table.scrollToColumnIndex(0);
					selectSection(DPXSection.FILE_INFORMATION_HEADER);
					break;
				case "imageInformationTab":
					for (final TableColumn<DPXFileInformationViewModel, ?> col : table.getColumns()) {
						final String tmpValue = col.getUserData().toString();
						if (tmpValue.equals("General")) {
							if ((col.getId().equals("Filename") || col.getId().equals("Row"))
									&& !userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
							continue;
						}
						final DPXMetadataColumnViewModel mcvm = (DPXMetadataColumnViewModel) col.getUserData();
						if (mcvm == null) {
							continue;
						}
						if (mcvm.getSectionDisplayName().equals("Image Information")
							|| col.getId().equals("Filename") || col.getId().equals("Row")) {
							if (!userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
						} else if (col.isVisible()) {
							col.setVisible(false);
						}
					}
					table.scrollToColumnIndex(0);
					selectSection(DPXSection.IMAGE_INFORMATION_HEADER);
					break;
				case "imageSourceInformationTab":
					for (final TableColumn<DPXFileInformationViewModel, ?> col : table.getColumns()) {
						final String tmpValue = col.getUserData().toString();
						if (tmpValue.equals("General")) {
							if ((col.getId().equals("Filename") || col.getId().equals("Row"))
									&& !userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
							continue;
						}
						final DPXMetadataColumnViewModel mcvm = (DPXMetadataColumnViewModel) col.getUserData();
						if (mcvm == null) {
							continue;
						}
						if (mcvm.getSectionDisplayName().equals("Image Source Information")
								|| col.getId().equals("Filename") || col.getId().equals("Row")) {
							if (!userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
						} else if (col.isVisible()) {
							col.setVisible(false);
						}
					}
					table.scrollToColumnIndex(0);
					selectSection(DPXSection.IMAGE_SOURCE_INFORMATION_HEADER);
					break;
				case "motionPictureFilmTab":
					for (final TableColumn<DPXFileInformationViewModel, ?> col : table.getColumns()) {
						final String tmpValue = col.getUserData().toString();
						if (tmpValue.equals("General")) {
							if ((col.getId().equals("Filename") || col.getId().equals("Row"))
									&& !userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
							continue;
						}

						final DPXMetadataColumnViewModel mcvm = (DPXMetadataColumnViewModel) col.getUserData();
						if (mcvm == null) {
							continue;
						}
						if (mcvm.getSectionDisplayName().equals("Motion Picture Film Information")
								|| col.getId().equals("Filename") || col.getId().equals("Row")) {
							if (!userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
						} else if (col.isVisible()) {
							col.setVisible(false);
						}
					}
					table.scrollToColumnIndex(0);
					selectSection(DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER);
					break;
				case "televisionTab":
					for (final TableColumn<DPXFileInformationViewModel, ?> col : table.getColumns()) {
						final String tmpValue = col.getUserData().toString();
						if (tmpValue.equals("General")) {
							if ((col.getId().equals("Filename") || col.getId().equals("Row"))
									&& !userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
							continue;
						}
						final DPXMetadataColumnViewModel mcvm = (DPXMetadataColumnViewModel) col.getUserData();
						if (mcvm == null) {
							continue;
						}
						if (mcvm.getSectionDisplayName().equals("Television Information")
								|| col.getId().equals("Filename") || col.getId().equals("Row")) {
							if (!userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
						} else if (col.isVisible()) {
							col.setVisible(false);
						}
					}
					table.scrollToColumnIndex(0);
					selectSection(DPXSection.TELEVISION_INFORMATION_HEADER);
					break;
				case "userDefinedTab":
					for (final TableColumn<DPXFileInformationViewModel, ?> col : table.getColumns()) {
						final String tmpValue = col.getUserData().toString();
						if (tmpValue.equals("General")) {
							if ((col.getId().equals("Filename") || col.getId().equals("Row"))
									&& !userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
							continue;
						}
						final DPXMetadataColumnViewModel mcvm = (DPXMetadataColumnViewModel) col.getUserData();
						if (mcvm == null) {
							continue;
						}
						if (mcvm.getSectionDisplayName().equals("User Defined Information")
								|| col.getId().equals("Filename") || col.getId().equals("Row")) {
							if (!userHiddenColumns.contains(col)) {
								col.setVisible(true);
							} else {
								col.setVisible(false);
							}
						} else if (col.isVisible()) {
							col.setVisible(false);
						}
					}
					table.scrollToColumnIndex(0);
					selectSection(DPXSection.USER_DEFINED_DATA);
					break;
				default:
					break;
				}
			}
		});
	}

	private void setColumnSequenceGapWarnings() {
		setSequenceErrorLists();
		for (final TableColumn<DPXFileInformationViewModel, ?> tableColumn : table.getColumns()) {
			final String tmpValue = tableColumn.getUserData().toString();
			if (tmpValue.equals("General")) {
				if (tableColumn.getId() == "Filename") {
					tableColumn.setGraphic(null);
					setColumnHeader(tableColumn, "", "Filename");
				}
				continue;
			}
			final DPXMetadataColumnViewModel mcvm = (DPXMetadataColumnViewModel)tableColumn.getUserData();
			if (mcvm == null) {
				continue;
			}
			if (mcvm.getDisplayName() == DPXColumn.IMAGE_FILE_NAME.getDisplayName() ||
				mcvm.getDisplayName() == DPXColumn.SOURCE_IMAGE_FILENAME.getDisplayName() ||
				mcvm.getDisplayName() == DPXColumn.FRAME_POSITION_IN_SEQUENCE.getDisplayName()
			) {
				tableColumn.setGraphic(null);
				setColumnHeader(
					tableColumn,
					DPXColumnHelpText.getInstance().getHelpText(mcvm.getColumn()),
					mcvm.getDisplayName()
				);
			}
		}
	}
	
	public void setTabWarnings() {
		final ObservableList<Tab> tabs = tabPane.getTabs();
		for (final Tab t : tabs) {		
			switch (t.getId()) {
			case "generalTab":
				removeAccessibleTabWarning(t, "General");
				break;
			case "fileInformationTab":
				if (tabSummary.hasSectionViolation(DPXSection.FILE_INFORMATION_HEADER)) {
					setAccessibleTabWarning(t, "File Info");
				} else {
					removeAccessibleTabWarning(t, "File Info");
				}
				break;
			case "imageInformationTab":
				if (tabSummary.hasSectionViolation(DPXSection.IMAGE_INFORMATION_HEADER)) {
					setAccessibleTabWarning(t, "Image Info");
				} else {
					removeAccessibleTabWarning(t, "Image Info");
				}
				break;
			case "imageSourceInformationTab":
				if (tabSummary.hasSectionViolation(DPXSection.IMAGE_SOURCE_INFORMATION_HEADER)) {
					setAccessibleTabWarning(t, "Image Source Info");
				} else {
					removeAccessibleTabWarning(t, "Image Source Info");
				}
				break;
			case "motionPictureFilmTab":
				if (tabSummary.hasSectionViolation(DPXSection.MOTION_PICTURE_FILM_INFORMATION_HEADER)) {
					setAccessibleTabWarning(t, "Motion Picture Film Info");
				} else {
					removeAccessibleTabWarning(t, "Motion Picture Film Info");
				}
				break;
			case "televisionTab":
				if (tabSummary.hasSectionViolation(DPXSection.TELEVISION_INFORMATION_HEADER)) {
					setAccessibleTabWarning(t, "Television Info");
				} else {
					removeAccessibleTabWarning(t, "Television Info");
				}
				break;
			case "userDefinedTab":
				if (tabSummary.hasSectionViolation(DPXSection.USER_DEFINED_DATA)) {
					setAccessibleTabWarning(t, "User Defined Info");
				} else {
					removeAccessibleTabWarning(t, "User Defined Info");
				}
				break;
			default:
				break; // unknown tab, should not be reached
			}
		}
	}

	
	private void setAccessibleTabWarning(Tab t, String titleText) {
		String accessibleText = titleText + TAB_VALIDATION_WARNING;
		
		HBox hbox = new HBox();
		final String defaultStyle = "-fx-font-size: 12px";
		final Label title = new Label(titleText);
		title.setStyle("-fx-padding: 0 0 0 5; -fx-font-size: 12px");
		final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION);
		icon.setStyleClass("fadgi-sr-warning");
		hbox.getChildren().add(icon);	
		hbox.getChildren().add(title);
		hbox.setAlignment(Pos.CENTER);
		hbox.setStyle(defaultStyle);
		
		t.setGraphic(hbox);
		t.setText(accessibleText);
		t.setStyle("-fx-padding: 0 0 0 5;");
	}
	
	private void removeAccessibleTabWarning(Tab t,  String titleText) {	
		HBox hbox = new HBox();
		final String defaultStyle = "-fx-font-size: 12px";
		final Label title = new Label(titleText);
		title.setStyle("-fx-padding: 0 0 0 5; -fx-font-size: 12px");
		hbox.getChildren().add(title);
		hbox.setAlignment(Pos.CENTER);
		hbox.setStyle(defaultStyle);
		
		t.setGraphic(hbox);
		t.setText(titleText);
		t.setStyle("-fx-padding: 0 0 0 5;");
	}
	
	public void showColumnVisibilityDialogue() {
		final Alert columnVisibilityDialog = new Alert(AlertType.INFORMATION, "", ButtonType.OK);
		columnVisibilityDialog.initModality(Modality.APPLICATION_MODAL);
		columnVisibilityDialog.initOwner(Main.getPrimaryStage());
		columnVisibilityDialog.setResizable(true);
		columnVisibilityDialog.setGraphic(null);
		columnVisibilityDialog.setTitle("Show/hide Columns");
		columnVisibilityDialog.setHeaderText(null);
		columnVisibilityDialog.setContentText("Set individual column visibility");
		final List<CheckBox> checkBoxes = createColumnVisibilityCheckBoxes();
		if (checkBoxes.size() > 0) {
			final GridPane grid = new GridPane();
			grid.setHgap(15);
			grid.setVgap(10);
			grid.setPadding(new Insets(10, 10, 10, 10));

			int rowIndex = 1;
			int colIndex = 0;
			String prevSection = "";
			String currentSection = "";
			for (final CheckBox cb : checkBoxes) {
				currentSection = cb.getUserData().toString();
				if (prevSection != currentSection) {
					colIndex++;
					rowIndex = 1;
					prevSection = currentSection;
					final Text sectionHead = new Text(currentSection);
					sectionHead.setStyle("fx-font-weight: bold");
					grid.add(sectionHead, colIndex, 0);
				}
				cb.setStyle("-fx-font-size: 11px");
				grid.add(cb, colIndex, rowIndex);
				rowIndex++;
			}
			columnVisibilityDialog.getDialogPane().setContent(grid);
		} else {
			columnVisibilityDialog.setContentText("No files in workspace.");
		}

		columnVisibilityDialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
			@Override
			public void handle(DialogEvent event) {
				final int currentTabIdx = tabPane.getSelectionModel().getSelectedIndex();
				tabPane.getSelectionModel().selectFirst();
				tabPane.getSelectionModel().clearAndSelect(currentTabIdx);
			}
		});

		columnVisibilityDialog.showAndWait();

		if (columnVisibilityDialog.getResult() == ButtonType.OK) {
			columnVisibilityDialog.close();
			final int currentTabIdx = tabPane.getSelectionModel().getSelectedIndex();
			tabPane.getSelectionModel().selectFirst();
			tabPane.getSelectionModel().clearAndSelect(currentTabIdx);
		}

	}

	private void showEditsAlert(int editedCount) {
		//The alert will not resize to accommodate title length (unlike header, which is not accessible in JavaFX8 to my knowledge)
		//Adding rough padding to message so that modal width includes full title
		String info = String.format("        %s total fields changed.        ", editedCount);
		if (editedCount == 1) {
			info = String.format("        %s field changed.        ", editedCount);
		}
		
		final Alert alert = AccessibleAlertHelper.CreateAccessibleAlert(
				"Changes Applied",
				AlertType.NONE,
				info,
				ButtonType.OK
			);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(Main.getPrimaryStage());
		 
		 DialogPane dialogPane = alert.getDialogPane();		 
		 alert.getDialogPane().lookupButton(ButtonType.OK).setAccessibleHelp(info);
		 	
		 dialogPane.getStylesheets().add(getClass().getResource("/com/portalmedia/embarc/gui/application.css").toExternalForm());
		 dialogPane.getStyleClass().add("alertDialog");	 
		
		alert.showAndWait();
		if (alert.getResult() == ButtonType.OK) {
			alert.close();
		}
		
		ControllerMediatorDPX.getInstance().setEditedFieldsCount(0);
	}

	public void updateChangedValues(HashMap<DPXColumn, String> changedValues) {
		final ProgressSpinner spinner = new ProgressSpinner();

		final Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				final ObservableList<Integer> selectedIndices = table.getSelectionModel().getSelectedIndices();
				final ExecutorService executor = Executors
						.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				final List<Callable<Object>> futures = new ArrayList<>(selectedIndices.size());
				for (final Integer i : selectedIndices) {
					futures.add(Executors.callable(() -> {
						try {
							final DPXFileInformationViewModel fivm = table.getItems().get(i);
							DPXFileListHelper.updateValues(changedValues, fivm);
						} catch (final Exception e) {
							System.out.println("Update values error");
						}
					}));
				}
				try {
					executor.invokeAll(futures);
				} catch (final InterruptedException e) {
					System.out.println("Update values thread error");
				}

				if (TableState.isSelectAll()) {
					selectedFilesSummary = DPXFileListHelper.createSelectedFilesSummaryAll();
				} else {
					selectedFilesSummary = DPXFileListHelper
							.createSelectedFilesSummary(table.getSelectionModel().getSelectedItems());
				}

				setSelectedRuleSets(ControllerMediatorDPX.getInstance().getCurrentRuleSets());
				return null;
			}
		};

		task.setOnSucceeded(event -> {
			table.refresh();
			refreshEditor(false);
			spinner.getDialogStage().hide();
			Platform.runLater(() -> showEditsAlert(changedValues.size()));
		});

		final Thread thread = new Thread(task);
		thread.start();

		spinner.getDialogStage().show();
	}

	public void updateChangedValuesAllFiles(HashMap<DPXColumn, String> changedValues) {
		final ProgressSpinner spinner = new ProgressSpinner();

		final Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				table.getSelectionModel().selectAll();
				final ObservableList<Integer> selectedIndices = table.getSelectionModel().getSelectedIndices();
				final ExecutorService executor = Executors
						.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				final List<Callable<Object>> futures = new ArrayList<>(selectedIndices.size());
				for (final Integer i : selectedIndices) {
					futures.add(Executors.callable(() -> {
						try {
							final DPXFileInformationViewModel fivm = table.getItems().get(i);
							DPXFileListHelper.updateValues(changedValues, fivm);
						} catch (final Exception e) {
							System.out.println("Update values error");
						}
					}));
				}
				try {
					executor.invokeAll(futures);
				} catch (final InterruptedException e) {
					System.out.println("Update values thread error");
				}

				if (TableState.isSelectAll()) {
					selectedFilesSummary = DPXFileListHelper.createSelectedFilesSummaryAll();
				} else {
					selectedFilesSummary = DPXFileListHelper
							.createSelectedFilesSummary(table.getSelectionModel().getSelectedItems());
				}

				setSelectedRuleSets(ControllerMediatorDPX.getInstance().getCurrentRuleSets());
				return null;
			}
		};

		task.setOnSucceeded(event -> {
			table.refresh();
			refreshEditor(false);
			spinner.getDialogStage().hide();
			Platform.runLater(() -> showEditsAlert(changedValues.size()));
		});

		final Thread thread = new Thread(task);
		thread.start();

		spinner.getDialogStage().show();
	}

	private void updateData() {
		final boolean sa = TableState.isSelectAll();
		DPXFileListHelper.setSelectAll(sa);

		if (TableState.getDeleteFiles()) {
			final ObservableList<DPXFileInformationViewModel> toDelete = ControllerMediatorDPX.getInstance()
					.getSelectedFileList();
			DPXFileListHelper.deleteSelectedRows(toDelete);
			selectedFilesSummary = null;
		}

		if (TableState.getUpdateSummary() && !TableState.isUpdatingSummary()) {
			TableState.setUpdatingSummary(true);
			selectedFilesSummary = DPXFileListHelper
					.createSelectedFilesSummary(table.getSelectionModel().getSelectedItems());
			selectedFilesSummary.setValidRuleSets(ControllerMediatorDPX.getInstance().getCurrentRuleSets());
			TableState.setUpdateSummary(false);
			TableState.setUpdatingSummary(false);
		}
	}

	private void updateTable() {
		if (TableState.getDeleteFiles()) {
			ControllerMediatorDPX.getInstance().getSelectedFileList();
			ControllerMediatorDPX.getInstance().setFileList();
			table.getSelectionModel().clearSelection();
			TableState.setDeleteFiles(false);
		} else if (TableState.isSelectAll()) {
			try {
				tableSelectionModel.selectAll();
			} catch(Exception ex) {
				System.out.println("Caught exception attempting to select all items in table");
			}
		} else if (TableState.getDeselectAll()) {
			tableSelectionModel.clearSelection();
			TableState.setDeselectAll(false);
		}

		if (TableState.getUpdateEditor()) {
			final int selectedRowCount = table.getSelectionModel().getSelectedItems().size();
			if (selectedRowCount > 0 && selectedSection != null) {
				ControllerMediatorDPX.getInstance().setEditor(selectedSection);
			} else if (selectedRowCount > 0) {
				ControllerMediatorDPX.getInstance().resetEditor(selectedSection);
				ControllerMediatorDPX.getInstance().setGeneralEditor();
			} else {
				ControllerMediatorDPX.getInstance().resetEditor(selectedSection);
			}
			TableState.setUpdateEditor(false);
		}
	}
	
	private void setSequenceErrorLists() {
		List<DPXSequenceError> sequenceErrors = DPXBatchProcessor.getSequenceErrorList(getTable().getItems(), false);
		filenameSequenceErrorList.clear();
		imageFilenameSequenceErrorList.clear();
		sourceImageFilenameSequenceErrorList.clear();
		framePositionSequenceErrorList.clear();

		for (DPXSequenceError seqError : sequenceErrors) {
			if (seqError.getColumn() == "Filename") {
				filenameSequenceErrorList.add(seqError);
			}
			if (seqError.getColumn() == "Image Filename") {
				imageFilenameSequenceErrorList.add(seqError);
			}
			if (seqError.getColumn() == "Source Image Filename") {
				sourceImageFilenameSequenceErrorList.add(seqError);
			}
			if (seqError.getColumn() == "Frame Position in Sequence") {
				framePositionSequenceErrorList.add(seqError);
			}
		}
	}

	public void autoPopulateNames() {
		final String message = "Are you sure you want to change the Image Filename metadata to match the Filename on disk?";
		final Alert alert = AccessibleAlertHelper.CreateAccessibleAlert(
				"Are you sure?",
				AlertType.CONFIRMATION,
				message
			);
				
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(Main.getPrimaryStage());
		alert.setGraphic(null);
		alert.setHeaderText(null);

		DialogPane dialogPane = alert.getDialogPane();		 
		alert.getDialogPane().lookupButton(ButtonType.OK).setAccessibleHelp(message);
		 	
		dialogPane.getStylesheets().add(getClass().getResource("/com/portalmedia/embarc/gui/application.css").toExternalForm());
		dialogPane.getStyleClass().add("alertDialog");	 
		

		final Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {

			final ProgressSpinner spinner = new ProgressSpinner();

			final Task<Void> task = new Task<Void>() {
				@Override
				public Void call() throws InterruptedException {
					final ObservableList<Integer> selectedIndices = table.getSelectionModel().getSelectedIndices();
					final ExecutorService executor = Executors
							.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
					final List<Callable<Object>> futures = new ArrayList<>(selectedIndices.size());
					for (final Integer i : selectedIndices) {
						futures.add(Executors.callable(() -> {
							try {
								final DPXFileInformationViewModel fivm = table.getItems().get(i);
								DPXFileListHelper.updateName(fivm);
							} catch (final Exception e) {
								System.out.println("Error updating filenames");
							}
						}));
					}
					try {
						executor.invokeAll(futures);
					} catch (final InterruptedException e) {
						System.out.println("Thread error");
					}

					setSelectedRuleSets(ControllerMediatorDPX.getInstance().getCurrentRuleSets());
					TableState.setUpdateEditor(true);
					TableState.setUpdateSummary(true);
					updateData();
					return null;
				}
			};

			task.setOnSucceeded(event -> {
				Platform.runLater(() -> {
					table.refresh();
					updateTable();
				});
				spinner.getDialogStage().hide();
			});

			final Thread thread = new Thread(task);
			thread.start();

			spinner.getDialogStage().show();

			refreshEditor(true);
		}
	}

	public void clearFilterByViolations() {
		table.setItems(DPXFileListHelper.getObservableFileList());
	}

	private List<CheckBox> createColumnVisibilityCheckBoxes() {
		final List<CheckBox> cbList = new ArrayList<>();
		Map<String, List<TableColumn<DPXFileInformationViewModel, ?>>> relatedColumnDictionary 
			= new HashMap<String, List<TableColumn<DPXFileInformationViewModel, ?>>>();
		Map<String, String> checkboxIdToColumnKeyMap 
		= new HashMap<String, String>();
		
		Integer count = 0;
		for (final TableColumn<DPXFileInformationViewModel, ?> col : table.getColumns()) {
			String text = col.getId();			
			if (text.length() > 20) {
				text = text.substring(0, 20) + "...";
			}
			final CheckBox checkbox = new CheckBox(text);
			final Tooltip tt = new Tooltip(col.getId());

			final String ttStyle = "-fx-font-size: 12px";
			tt.setStyle(ttStyle);

			checkbox.setTooltip(tt);
			checkbox.setId(count.toString());

			if (col.getUserData() instanceof String) {
				final String data = (String) col.getUserData();
				final List<TableColumn<DPXFileInformationViewModel, ?>> columnList 
					= new ArrayList<TableColumn<DPXFileInformationViewModel, ?>>();
				columnList.add(col);
				relatedColumnDictionary.put(data, columnList);
				
				checkbox.setUserData(col.getUserData());
				checkboxIdToColumnKeyMap.put(checkbox.getId(), data);
			} else {
				final DPXMetadataColumnViewModel mcvm = (DPXMetadataColumnViewModel) col.getUserData();
				String columnKey;
				if(mcvm.getHasSubsection()) {
					columnKey = mcvm.getSubsectionName();
					List<TableColumn<DPXFileInformationViewModel, ?>> existingColumnList
						= relatedColumnDictionary.get(columnKey);
					//One checkbox per subsection
					if(existingColumnList != null) {
						existingColumnList.add(col);
						continue;
					}
					
					checkbox.setText(mcvm.getSubsectionName());
					final Tooltip updatedTooltip = new Tooltip(mcvm.getSubsectionName());
					updatedTooltip.setStyle(ttStyle);
					checkbox.setTooltip(updatedTooltip);
				} else {
					columnKey = col.getId()+mcvm.hashCode(); //need keep unique
				}
				
				checkbox.setUserData(mcvm.getSectionDisplayName());
				checkboxIdToColumnKeyMap.put(checkbox.getId(), columnKey);
				List<TableColumn<DPXFileInformationViewModel, ?>> columnList
					= new ArrayList<TableColumn<DPXFileInformationViewModel, ?>>();
				columnList.add(col);
				relatedColumnDictionary.put(columnKey, columnList);				
			}

			if (!userHiddenColumns.contains(col)) {
				checkbox.setSelected(true);
			} else {
				checkbox.setSelected(false);
			}		
			
			cbList.add(checkbox);
			count++;
		}
		
		for (final CheckBox checkbox : cbList) {
			checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> obs, Boolean ov, Boolean nv) {
					final String columnKey = checkboxIdToColumnKeyMap.get(checkbox.getId());
					final List<TableColumn<DPXFileInformationViewModel, ?>> columns 
						= relatedColumnDictionary.get(columnKey);
					final UserPreferencesService userPreferences = new UserPreferencesService();
					for (final TableColumn<DPXFileInformationViewModel, ?> col : columns) {
						if (nv) {
							userHiddenColumns.remove(col);
							userPreferences.removeHiddenDPXColumn(col.getId());
						} else {
							userHiddenColumns.add(col);
							userPreferences.addHiddenDPXColumn(col.getId());
						}
					}
				}
			});
		}
		return cbList;
	}

	public void deleteSelectedFiles() {
		final ProgressSpinner spinner = new ProgressSpinner();

		final Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				TableState.setDeleteFiles(true);
				updateData();
				Platform.runLater(() -> {
					updateTable();
				});
				return null;
			}
		};

		task.setOnSucceeded(event -> {
			spinner.getDialogStage().hide();
			ControllerMediatorDPX.getInstance().setFiles();
		});
		spinner.activateProgressBar(task);
		final Thread thread = new Thread(task);
		thread.start();

		spinner.getDialogStage().show();
	}

	public void deselectAllFiles() {
		final ProgressSpinner spinner = new ProgressSpinner();

		final Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				DPXFileListHelper.setSelectAll(false);
				TableState.setSelectAll(false);
				TableState.setDeselectAll(true);
				TableState.setUpdateSummary(true);
				TableState.setUpdateEditor(true);
				updateData();
				return null;
			}
		};

		task.setOnSucceeded(event -> {
			Platform.runLater(() -> {
				updateTable();
			});
			spinner.getDialogStage().hide();
		});

		final Thread thread = new Thread(task);
		thread.start();

		spinner.getDialogStage().show();
	}

	public int filterByViolations() {

		final FilteredList<DPXFileInformationViewModel> filteredList = new FilteredList<>(table.getItems());
		filteredList.setPredicate(new Predicate<DPXFileInformationViewModel>() {
			@Override
			public boolean test(DPXFileInformationViewModel t) {
				return t.hasError(ControllerMediatorDPX.getInstance().getCurrentRuleSets()); // or true
			}
		});
		table.setItems(filteredList);

		return filteredList.size();
	}

	public ObservableList<DPXFileInformationViewModel> getSelectedFiles() {
		final ObservableList<DPXFileInformationViewModel> list = table.getSelectionModel().getSelectedItems();
		return list;
	}

	public SelectedFilesSummary getSelectedFilesSummary() {
		return selectedFilesSummary;
	}

	public TableView<DPXFileInformationViewModel> getTable() {
		return table;
	}

	public int getTableSize() {
		return table.getItems().size();
	}

}