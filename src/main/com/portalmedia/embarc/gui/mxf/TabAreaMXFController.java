package com.portalmedia.embarc.gui.mxf;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.portalmedia.embarc.gui.AccessibleAlertHelper;
import com.portalmedia.embarc.gui.Main;
import com.portalmedia.embarc.gui.ProgressSpinner;
import com.portalmedia.embarc.gui.helper.MXFFileList;
import com.portalmedia.embarc.gui.helper.RowNumberManager;
import com.portalmedia.embarc.gui.model.MXFMetadataColumnViewModel;
import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;
import com.portalmedia.embarc.gui.model.MXFFileInformationViewModel;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.SectionDef;
import com.portalmedia.embarc.parser.mxf.DeviceSetHelper;
import com.portalmedia.embarc.parser.mxf.IdentifierSetHelper;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.parser.mxf.MXFColumnHelpText;
import com.portalmedia.embarc.parser.mxf.MXFFileInfo;
import com.portalmedia.embarc.parser.mxf.MXFMetadataColumnViewModelList;
import com.portalmedia.embarc.parser.mxf.MXFSection;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;
import tv.amwa.maj.exception.PropertyNotPresentException;
import tv.amwa.maj.model.impl.AS07CoreDMSDeviceObjectsImpl;
import tv.amwa.maj.model.impl.AS07DMSIdentifierSetImpl;

/**
 * 
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-20
 */
public class TabAreaMXFController implements Initializable {
	@FXML
	private TabPane mxfTabPane;
	@FXML
	private TableView<MXFFileInformationViewModel> mxfTable;

	private TableViewSelectionModel<MXFFileInformationViewModel> tableSelectionModel;
	private SectionDef selectedSection;
	private MXFSelectedFilesSummary selectedFilesSummary;
	private final String TAB_VALIDATION_WARNING = " Contains validation error";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediatorMXF.getInstance().registerTabAreaController(this);
		setTabClickListener();
		setDescriptorColumns();
		setFileInfoColumns();
		mxfTable.setFocusTraversable(true);

		mxfTable.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					TableColumn<MXFFileInformationViewModel, ?> focusedColumn = mxfTable.getFocusModel()
							.getFocusedCell().getTableColumn();
					if (focusedColumn != null && focusedColumn.isSortable()) {
						if (focusedColumn.getSortType() == SortType.ASCENDING) {
							focusedColumn.setSortType(SortType.DESCENDING);
						} else {
							focusedColumn.setSortType(SortType.ASCENDING);
						}
						mxfTable.getSortOrder().clear();
						mxfTable.getSortOrder().add(focusedColumn);
					}
					event.consume();
				}
			}
		});

		tableSelectionModel = mxfTable.getSelectionModel();
		tableSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
		tableSelectionModel.getSelectedItems()
				.addListener((ListChangeListener.Change<? extends MXFFileInformationViewModel> change) -> {
					selectedFilesSummary = MXFSelectedFilesSummary.create(tableSelectionModel.getSelectedItems());
					ControllerMediatorMXF.getInstance().setSelectedFileList(selectedFilesSummary);
					refreshEditor(false);
				});
		MXFFileList.getInstance().hasCoreRequiredFieldsErrorProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> o, Boolean ov, Boolean nv) {
				setTabWarnings();
			}
		});
		
		setTabWarnings(); //Run at least once on load
		refreshEditor(false);
		setColumnVisibility("FileInfo");
		mxfTable.scrollToColumnIndex(0);
		selectSection(MXFSection.FILEINFO);
	}

	public ObservableList<MXFFileInformationViewModel> getSelectedFiles() {
		return mxfTable.getSelectionModel().getSelectedItems();
	}

	public void deleteSelectedFiles() {
		List<MXFFileInformationViewModel> filesToDelete = tableSelectionModel.getSelectedItems();
		MXFFileList.deleteSelectedRows(filesToDelete);
		setFiles();
	}

	public void selectAllFiles() {
		tableSelectionModel.selectAll();
		refreshEditor(false);
	}

	public void deselectAllFiles() {
		tableSelectionModel.clearSelection();
		refreshEditor(false);
	}

	public TableView<MXFFileInformationViewModel> getTable() {
		return mxfTable;
	}

	public int getTableSize() {
		return mxfTable.getItems().size();
	}

	public MXFSelectedFilesSummary getSelectedFilesSummary() {
		return selectedFilesSummary;
	}

	public void setFiles() {
		mxfTable.setItems(MXFFileList.getInstance().getObservableList());
		setDataColumns();
	}

	public void refreshEditor(boolean updateSummary) {
		if (updateSummary) {
			selectedFilesSummary = MXFSelectedFilesSummary.create(tableSelectionModel.getSelectedItems());
		}

		final int selectedRowCount = tableSelectionModel.getSelectedItems().size();

		// if no files are selected, reset and set writer
		if (selectedRowCount == 0) {
			ControllerMediatorMXF.getInstance().resetEditArea();
			ControllerMediatorMXF.getInstance().setWriter();
		}

		// if files are selected but section is null, reset and set descriptor view
		if (selectedRowCount > 0 && selectedSection == null) {
			ControllerMediatorMXF.getInstance().resetEditArea();
			ControllerMediatorMXF.getInstance().setDescriptor();
		}

		// if files are selected and section is selected
		if (selectedRowCount > 0 && selectedSection != null) {
			if (selectedSection == MXFSection.TD || selectedSection == MXFSection.BD) {
				ControllerMediatorMXF.getInstance().setTDBD(selectedSection);
			} else if (selectedSection == MXFSection.FILEINFO) {
				ControllerMediatorMXF.getInstance().setFileInfo();
			} else if (selectedSection == MXFSection.CORE) {
				ControllerMediatorMXF.getInstance().setCore();
			} else {
				ControllerMediatorMXF.getInstance().resetEditArea();
				ControllerMediatorMXF.getInstance().setFileInfo(); // ??
			}
		}
	}

	public void setTabClickListener() {
		mxfTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			if (mxfTable.getItems().size() > 0) {
				mxfTabPane.requestFocus(); //Covers special case where navigating with VoiceOver VO+-> key combo does not keep focus on tab pane
				switch (newTab.getId()) {
				case "fileInfoTab":
					setColumnVisibility("FileInfo");
					mxfTable.scrollToColumnIndex(0);
					selectSection(MXFSection.FILEINFO);
					break;
				case "descriptorsTab":
					setColumnVisibility("Descriptors");
					mxfTable.scrollToColumnIndex(0);
					selectedSection = null;
					selectSection(selectedSection);
					break;
				case "coreTab":
					setColumnVisibility("CORE");
					mxfTable.scrollToColumnIndex(0);
					selectSection(MXFSection.CORE);
					break;
				case "tdTab":
					setColumnVisibility("TD");
					mxfTable.scrollToColumnIndex(0);
					selectSection(MXFSection.TD);
					break;
				case "bdTab":
					setColumnVisibility("BD");
					mxfTable.scrollToColumnIndex(0);
					selectSection(MXFSection.BD);
					break;
				default:
					break;
				}
			}
		});
	}

	private void setColumnVisibility(String tabName) {
		for (final TableColumn<MXFFileInformationViewModel, ?> col : mxfTable.getColumns()) {
			if (!col.getUserData().toString().equals(tabName) && !col.getUserData().toString().equals("Descriptors")) {
				col.setVisible(false);
			} else {
				col.setVisible(true);
			}
		}
	}

	public void selectSection(SectionDef section) {
		this.selectedSection = section;
		refreshEditor(false);
	}

	private void setDescriptorColumns() {
		final TableColumn<MXFFileInformationViewModel, String> idCol = new TableColumn<>("Row");
		idCol.setId("Row");
		idCol.setUserData("Descriptors");
		idCol.getStyleClass().add("general-section");

		idCol.setCellValueFactory(
				new Callback<CellDataFeatures<MXFFileInformationViewModel, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<MXFFileInformationViewModel, String> p) {
						final int id = p.getValue().getId();
						final int rowNumber = (mxfTable.getItems().indexOf(p.getValue()) + 1);
						RowNumberManager.addRow(id, rowNumber);
						return new ReadOnlyObjectWrapper<>(rowNumber + "");
					}
				});
		idCol.setSortable(false);
		idCol.setPrefWidth(50);
		setColumnToolTip(idCol, "");

		final TableColumn<MXFFileInformationViewModel, String> nameCol = createFileInfoCol("Filename", "name", "Descriptors");
		nameCol.getStyleClass().add("general-section");

		final TableColumn<MXFFileInformationViewModel, String> pathCol = createFileInfoCol("File Path", "path",
				"Descriptors");
		pathCol.getStyleClass().add("general-section");

		mxfTable.getColumns().add(nameCol);
		mxfTable.getColumns().add(pathCol);
	}

	private void setFileInfoColumns() {
		TableColumn<MXFFileInformationViewModel, String> col;
		for (final MXFFileInfo fi : MXFFileInfo.values()) {
			col = createFileInfoCol(fi.getDisplayName(), fi.getIdentifier(), "FileInfo");
			mxfTable.getColumns().add(col);
		}
	}

	private void setDataColumns() {
		if (mxfTable.getItems().size() != 0) {
			final MXFMetadataColumnViewModelList columnList = MXFMetadataColumnViewModelList.getInstance();
			String section = "";
			for (final MXFMetadataColumnViewModel mcvm : columnList.getColumns()) {
				if (mcvm.getMXFColumn() == MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier
						|| mcvm.getMXFColumn() == MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode
						|| mcvm.getMXFColumn() == MXFColumn.AS_07_BD_DMS_PrimaryRFC5646LanguageCode
						|| mcvm.getMXFColumn() == MXFColumn.AS_07_Manifest
						|| mcvm.getMXFColumn() == MXFColumn.AS_07_Manifest_Valid) {
					continue;
				}
				section = mcvm.getColumn().getSection().toString();
				String colName = mcvm.getDisplayName();
				final TableColumn<MXFFileInformationViewModel, String> col = new TableColumn<>(colName);

				if ("Identifiers".equals(colName)) {
					col.setCellValueFactory(
							new Callback<CellDataFeatures<MXFFileInformationViewModel, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(
										CellDataFeatures<MXFFileInformationViewModel, String> c) {
									MXFColumn currentCol = mcvm.getMXFColumn();
									IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
									ArrayList<AS07DMSIdentifierSetImpl> identifiers = idSetHelper
											.createIdentifierListFromString(
													c.getValue().getCoreData().get(currentCol).getCurrentValue());
									return getIdentifierString(identifiers);
								}
							});
				} else if ("Devices".equals(colName)) {
					col.setCellValueFactory(
							new Callback<CellDataFeatures<MXFFileInformationViewModel, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(
										CellDataFeatures<MXFFileInformationViewModel, String> c) {
									MXFColumn currentCol = mcvm.getMXFColumn();
									DeviceSetHelper deviceSetHelper = new DeviceSetHelper();
									ArrayList<AS07CoreDMSDeviceObjectsImpl> devices = deviceSetHelper
											.createDeviceListFromString(
													c.getValue().getCoreData().get(currentCol).getCurrentValue());
									// display first item if present. if > 1, display multiple message
									if (devices != null && devices.size() > 0) {
										if (devices.size() > 1)
											return new ReadOnlyStringWrapper("Multiple devices");
										String manu = "";
										String type = "";
										try {
											manu = devices.get(0).getManufacturer();
										} catch (PropertyNotPresentException pex) {
										}
										try {
											type = devices.get(0).getDeviceType();
										} catch (PropertyNotPresentException pex) {
										}
										return new ReadOnlyStringWrapper(manu + " " + type);
									}
									return new ReadOnlyStringWrapper("No Devices");
								}
							});
				} else if ("CORE".equals(section)) {
					col.setCellValueFactory(
							new Callback<CellDataFeatures<MXFFileInformationViewModel, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(
										CellDataFeatures<MXFFileInformationViewModel, String> c) {
									MXFColumn currentCol = mcvm.getMXFColumn();
									if (c != null) {
										MXFFileInformationViewModel fivm = c.getValue();
										if (fivm != null) {
											HashMap<MXFColumn, MetadataColumnDef> coreData = fivm.getCoreData();
											if (coreData != null) {
												MetadataColumnDef currentValue = coreData.get(currentCol);
												if (currentValue != null) {
													return new ReadOnlyStringWrapper(currentValue.getCurrentValue());
												}
											}
										}
									}
									return new ReadOnlyStringWrapper("");
								}
							});
					col.setCellFactory(column -> {
						return new ValidationCellFactoryMXF(column, mcvm);
					});
				} else {
					col.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getProp(colName)));
				}
				col.setUserData(section);
				col.setVisible(false);
				setColumnWidth(col);
				col.setSortable(true);
				col.setId(mcvm.getDisplayName());
				setColumnStyles(col, mcvm);
				setColumnToolTip(col, MXFColumnHelpText.getInstance().getHelpText(mcvm.getColumn()));
				if (mcvm.getColumn().hasSubsection()) {
					setNestedDataColumns(col);
				} else {
					mxfTable.getColumns().add(col);
				}
			}
		}
	}

	private void setNestedDataColumns(TableColumn<MXFFileInformationViewModel, String> headerCol) {
		if (mxfTable.getItems().size() != 0) {
			final MXFMetadataColumnViewModelList columnList = MXFMetadataColumnViewModelList.getInstance();
			for (final MXFMetadataColumnViewModel mcvm : columnList.getColumns()) {
				SectionDef sectionDef = mcvm.getColumn().getSection();
				if (sectionDef != MXFSection.OBJECT)
					continue;

				if ("TD".equals(headerCol.getUserData().toString())) {
					for (int i = 0; i < MXFFileList.getInstance().getMaxTD(); i++) {
						String colName = Integer.toString(i+1).concat(" ").concat(mcvm.getDisplayName());
						MXFColumn mxfCol = mcvm.getMXFColumn();
						final TableColumn<MXFFileInformationViewModel, String> col = new TableColumn<>(colName);
						final int index = i;
						
						if (mcvm.getDisplayName() == "Identifiers") {
	 						col.setCellValueFactory(new Callback<CellDataFeatures<MXFFileInformationViewModel, String>, ObservableValue<String>>() {
	 							public ObservableValue<String> call(CellDataFeatures<MXFFileInformationViewModel, String> c) {
	 								IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
	 								ArrayList<AS07DMSIdentifierSetImpl> identifiers = idSetHelper.createIdentifierListFromString(c.getValue().getTDElementProperty(index, mxfCol));
	 								return getIdentifierString(identifiers);
	 							}
	 						});
	 					} else {			
	 						col.setCellValueFactory(c -> new ReadOnlyStringWrapper(
								c.getValue().getTDElementProperty(index, mxfCol)));
	 					}
						
						col.setUserData(headerCol.getUserData());
						col.setVisible(false);
						setColumnWidth(col);
						col.setSortable(true);
						col.setId(mcvm.getDisplayName());
						setColumnToolTip(col, "");
						setColumnStyles(col, mcvm);
						mxfTable.getColumns().add(col);
					}
				}
				
				if ("BD".equals(headerCol.getUserData().toString())) {		
					for (int i = 0; i < MXFFileList.getInstance().getMaxBD(); i++) {
						String colName = Integer.toString(i+1).concat(" ").concat(mcvm.getDisplayName());
						MXFColumn mxfCol = mcvm.getMXFColumn();
						final TableColumn<MXFFileInformationViewModel, String> col = new TableColumn<>(colName);
						final int index = i;
						
						
						if (mcvm.getDisplayName() == "Identifiers") {
	 						col.setCellValueFactory(new Callback<CellDataFeatures<MXFFileInformationViewModel, String>, ObservableValue<String>>() {
	 							public ObservableValue<String> call(CellDataFeatures<MXFFileInformationViewModel, String> c) {
	 								IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
	 								ArrayList<AS07DMSIdentifierSetImpl> identifiers = idSetHelper.createIdentifierListFromString(c.getValue().getBDElementProperty(index, mxfCol));
	 								return getIdentifierString(identifiers);
	 							}
	 						});
	 					} else {
	 						col.setCellValueFactory(c -> new ReadOnlyStringWrapper(
								c.getValue().getBDElementProperty(index, mxfCol)));
						
	 					}			
						
						col.setUserData(headerCol.getUserData());
						col.setVisible(false);
						setColumnWidth(col);
						col.setSortable(true);
						col.setId(mcvm.getDisplayName());
						setColumnToolTip(col, "");
						setColumnStyles(col, mcvm);
						mxfTable.getColumns().add(col);
					}
				}
			}
		}
	}

	private ReadOnlyStringWrapper getIdentifierString(ArrayList<AS07DMSIdentifierSetImpl> identifiers) {
		if (identifiers != null && identifiers.size() > 0 && identifiers.get(0) != null) {
			if (identifiers.size() > 1)
				return new ReadOnlyStringWrapper("Multiple identifiers");
			try {
				String idValue = identifiers.get(0).getIdentifierValue();
				try {
					String idRole = identifiers.get(0).getIdentifierRole();
					return new ReadOnlyStringWrapper(idValue + " (" + idRole + ")");
				} catch (PropertyNotPresentException e) {
					return new ReadOnlyStringWrapper(idValue);
				}
			} catch (PropertyNotPresentException e) {
				return new ReadOnlyStringWrapper("");
			}
		} else {
			return new ReadOnlyStringWrapper("No Identifiers");
		}
	}

	private TableColumn<MXFFileInformationViewModel, String> createFileInfoCol(String name, String propName,
			String userData) {
		final TableColumn<MXFFileInformationViewModel, String> col = new TableColumn<>(name);
		col.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProp(propName)));
		col.setId(name);
		col.setUserData(userData);
		if ("FileInfo".equals(userData)) {
			col.setVisible(false);
		} else {
			col.setVisible(true);
		}
		setColumnWidth(col);
		setColumnToolTip(col, "");
		return col;
	}

	private void setColumnWidth(TableColumn<MXFFileInformationViewModel, String> col) {
		col.setPrefWidth(120);
	}

	private void setColumnStyles(TableColumn<MXFFileInformationViewModel, String> col,
			MXFMetadataColumnViewModel mcvm) {
		if (mcvm.isEditable()) {
			col.getStyleClass().add("editable-column");
		} else {
			col.getStyleClass().add("uneditable-column");
		}
	}

	// TODO: need tooltip data
	private void setColumnToolTip(TableColumn<MXFFileInformationViewModel, String> col, String helpText) {
		final String headerName = col.textProperty().get();
		final Label title = new Label(headerName);
		if (!helpText.isEmpty()) {
			final Tooltip tt = new Tooltip(headerName + "\n\n" + helpText);
			tt.setStyle("-fx-font-size: 12px");
			tt.setPrefWidth(500);
			tt.setWrapText(true);
			tt.setAutoHide(false);
			title.setTooltip(tt);
		}
		title.setStyle("-fx-font-size: 12px");
		col.setGraphic(title);
		col.setText("");
	}

	public void updateChangedValues(HashMap<MXFColumn, MetadataColumnDef> changedValues) {
		final ProgressSpinner spinner = new ProgressSpinner();
		final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		final Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				final ObservableList<Integer> selectedIndices = mxfTable.getSelectionModel().getSelectedIndices();
				final List<Callable<Object>> futures = new ArrayList<>(selectedIndices.size());
				for (final Integer i : selectedIndices) {
					futures.add(Executors.callable(() -> {
						try {
							final MXFFileInformationViewModel fivm = mxfTable.getItems().get(i);
							MXFFileList.updateValues(changedValues, fivm);
						} catch (final Exception e) {
							System.out.println("Update changed values error");
						}
					}));
				}
				try {
					executor.invokeAll(futures);
				} catch (final InterruptedException e) {
					System.out.println("Update changed values thread error");
				}
				selectedFilesSummary = MXFSelectedFilesSummary.create(tableSelectionModel.getSelectedItems());
				return null;
			}
		};

		task.setOnSucceeded(event -> {
			mxfTable.refresh();
			refreshEditor(false);
			spinner.getDialogStage().hide();
			Platform.runLater(() -> showEditsAlert(changedValues.size()));
			executor.shutdown();
		});

		final Thread thread = new Thread(task);
		thread.start();

		spinner.getDialogStage().show();
	}

	private void showEditsAlert(int editedCount) {
		String info = String.format("%s total fields changed.", editedCount);
		if (editedCount == 1) {
			info = String.format("%s field changed.", editedCount);
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
	}

	private void setTabWarnings() {
		final ObservableList<Tab> tabs = mxfTabPane.getTabs();
		for (final Tab tab : tabs) {
			final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION);
			icon.setStyleClass("fadgi-sr-warning");
			switch (tab.getId()) {
			case "coreTab":
				if (MXFFileList.getInstance().hasCoreRequiredFieldsErrorProperty().get()) {
					Platform.runLater(() -> setAccessibleTabWarning(tab, "Core DMS"));
				} else {
					Platform.runLater(() -> removeAccessibleTabWarning(tab, "Core DMS"));
				}
				break;
			case "fileInfoTab":
				Platform.runLater(() -> removeAccessibleTabWarning(tab, "File Info"));
				break;
			case "descriptorsTab":
				Platform.runLater(() -> removeAccessibleTabWarning(tab, "Descriptors"));
				break;
			case "tdTab":
				Platform.runLater(() -> removeAccessibleTabWarning(tab, "Text DMS"));
				break;
			case "bdTab":
				Platform.runLater(() -> removeAccessibleTabWarning(tab, "Binary DMS"));
				break;
			default:
				break; //Unknown tab, should not hit
			}
		}
	}

	private void setAccessibleTabWarning(Tab tab, String titleText) {
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
		
		tab.setGraphic(hbox);
		tab.setText(accessibleText);
		tab.setStyle("-fx-padding: 0 0 0 5;");
	}
	
	private void removeAccessibleTabWarning(Tab tab,  String titleText) {	
		HBox hbox = new HBox();
		final String defaultStyle = "-fx-font-size: 12px";
		final Label title = new Label(titleText);
		title.setStyle("-fx-padding: 0 0 0 5; -fx-font-size: 12px");
		hbox.getChildren().add(title);
		hbox.setAlignment(Pos.CENTER);
		hbox.setStyle(defaultStyle);
		
		tab.setGraphic(hbox);
		tab.setText(titleText);
		tab.setStyle("-fx-padding: 0 0 0 5;");
	}
}