package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.liisteners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;
	
	private SellerService service;
	
	private DepartmentService departmentService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	private ObservableList<Department> obsList;
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		this.dataChangeListeners.add(listener);
	}
	
	public void updateFormData() {
		if(this.entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		this.txtId.setText(String.valueOf(this.entity.getId()));
		this.txtName.setText(this.entity.getName());
		this.txtEmail.setText(this.entity.getEmail());
		Locale.setDefault(Locale.US);
		this.txtBaseSalary.setText(String.format("%.2f", this.entity.getBaseSalary()));
		if(this.entity.getBirthDate() != null)
			this.dpBirthDate.setValue(LocalDate.ofInstant(this.entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		
		if(this.entity.getDepartment() == null) {
			this.comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			this.comboBoxDepartment.setValue(this.entity.getDepartment());
		}
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		this.labelErrorName.setText(fields.contains("name") ? errors.get("name") : "");		
		this.labelErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
		this.labelErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : "");
		this.labelErrorBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate") : "");
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(this.entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if(this.service == null) {
			throw new IllegalStateException("Service  was null");
		}
		try {
			this.entity = this.getFormData();
			this.service.saveOrUpdate(entity);
			this.notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch(ValidationException e) {
			this.setErrorMessages(e.getErrors());
		} catch(DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener: this.dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}

	private Seller getFormData() {
		Seller obj = new Seller();
		obj.setId(Utils.tryParseToInt(this.txtId.getText()));
		
		ValidationException exception = new ValidationException("Validation error");
		
		if(this.txtName.getText() == null || this.txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(this.txtName.getText());
		
		if(this.txtEmail.getText() == null || this.txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(this.txtEmail.getText());
		
		if(this.dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		} else {
			Instant instant = Instant.from(this.dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		if(this.txtBaseSalary.getText() == null || this.txtBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field can't be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(this.txtBaseSalary.getText()));
		
		obj.setDepartment(this.comboBoxDepartment.getValue());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFiledInteger(txtId);
		Constraints.setTextFieldMaxLength(this.txtName, 70);
		Constraints.setTextFieldDouble(this.txtBaseSalary);
		Constraints.setTextFieldMaxLength(this.txtEmail, 60);
		Utils.formatDatePicker(this.dpBirthDate, "dd/MM/yyyy");
		this.initializeComboBoxDepartment();
	}

	public void loadAssociatedObjects() {
		if(this.departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		List<Department> list = this.departmentService.findAll();
		this.obsList = FXCollections.observableArrayList(list);
		this.comboBoxDepartment.setItems(obsList);
	}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		
		this.comboBoxDepartment.setCellFactory(factory);
		this.comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
