package gui;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.liisteners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;
public class SellerListController implements Initializable, DataChangeListener {
	
	private SellerService service;
	
	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Seller> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		this.createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		this.initializeNodes();
	}
	
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		this.tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(this.tableColumnBaseSalary, 2);
		this.tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(this.tableColumnBirthDate, "dd/MM/yyyy");
		this.tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void updateTableView() {
		if(this.service == null) {
			throw new IllegalStateException("Service is null");
		}
		List<Seller> departments = this.service.findAll();
		obsList = FXCollections.observableArrayList(departments);
		this.tableViewSeller.setItems(obsList);
		this.initEditButtons();
		this.initRemoveButtons();
	}
	
	public void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		 try {
			 FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			 Pane pane = loader.load();
			 
			 SellerFormController controller = loader.getController();
			 controller.setSeller(obj);
			 controller.setServices(new SellerService(), new DepartmentService());
			 controller.loadAssociatedObjects();
			 controller.subscribeDataChangeListener(this);
			 controller.updateFormData();
			 
			 Stage dialogStage = new Stage();
			 dialogStage.setTitle("Enter Seller data");
			 dialogStage.setScene(new Scene(pane));
			 dialogStage.setResizable(false);
			 dialogStage.initOwner(parentStage);
			 dialogStage.initModality(Modality.WINDOW_MODAL);
			 dialogStage.showAndWait();
		 } catch(IOException e) {
			 e.printStackTrace();
			 Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		 }
	 }

	@Override
	public void onDataChanged() {
		this.updateTableView();		
	}	
	
	private void initEditButtons() {
		this.tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		this.tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");
			
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}	
	
	private void initRemoveButtons() {
		this.tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		this.tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	
	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		if(result.get() == ButtonType.OK) {
			if(this.service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				this.service.remove(obj);
				this.updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error remove object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}