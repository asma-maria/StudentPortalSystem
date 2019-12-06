package controllers;

import animation.Shaker;
import hibernate.MySessionFactory;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import models.Student;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class StudentRegistrationController {
    @FXML private AnchorPane studentAnchorPane;
    @FXML private JFXTextField name;
    @FXML private JFXTextField email;
    @FXML private JFXTextField contact;
    @FXML private JFXButton submitBtn;
    @FXML private JFXComboBox<String> department;
    @FXML private JFXComboBox<String> semesterComboBox;
    @FXML private JFXTextField searchStudent;

    @FXML private TableView<Student> tableView;
    @FXML private TableColumn<Student, Integer> tableId;
    @FXML private TableColumn<Student, String> tableName;
    @FXML private TableColumn<Student, String> tableDepartment;
    @FXML private TableColumn<Student, String> tableEmail;
    @FXML private TableColumn<Student, String> tableContact;
    @FXML private TableColumn<Student, String> tableSemester;

    @FXML private JFXButton deleteBtn;
    @FXML private JFXButton refreshBtn;

    private ObservableList<Student> students = FXCollections.observableArrayList();
    private FilteredList<Student> filteredData = new FilteredList<>(students, p -> true);

    @FXML
    void initialize() {
        setContactValidation();
        setTableProperties();

        setSemesters();
        setDepartments();

        submitBtn.setOnAction(event -> addStudent());
        refreshBtn.setOnAction(event -> loadStudents());
        deleteBtn.setOnAction(event -> deleteStudent());
        loadStudents();
    }

    private void setContactValidation(){
        contact.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                contact.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        contact.textProperty().addListener((ov, oldValue, newValue) -> {
            if (contact.getText().length() > 11) {
                String s = contact.getText().substring(0, 11);
                contact.setText(s);
            }
        });
    }

    private void updateStudent(String column, String newValue) { 	
		SessionFactory factory = MySessionFactory.getInstructorSF();

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		try {
			Student student = session.get(Student.class, tableView.getSelectionModel().getSelectedItem().getId());
			switch (column) {
			case "name": 
				student.setName(newValue);
				break;
				
			case "department": 
				student.setDepartment(newValue);
				break;
				
			case "email": 
				student.setEmail(newValue);
				break;
				
			case "contact":
				student.setContact(newValue);
				break;
				
			case "semester":
				student.setSemester(newValue);
				break;
				
			default:
				break;
			}
			session.getTransaction().commit();
		}finally {
			session.close();
			factory.close();
		}
		
        loadStudents();
    }

    private void setTableProperties(){
        tableId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        tableEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        tableSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        tableName.setCellFactory(TextFieldTableCell.forTableColumn());
        
        tableName.setCellFactory(TextFieldTableCell.forTableColumn());
        tableName.setOnEditCommit(event -> updateStudent("name", event.getNewValue()));

        tableDepartment.setCellFactory(TextFieldTableCell.forTableColumn());
        tableDepartment.setOnEditCommit(event -> updateStudent("department", event.getNewValue()));

        tableEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        tableEmail.setOnEditCommit(event -> updateStudent("email", event.getNewValue()));

        tableContact.setCellFactory(TextFieldTableCell.forTableColumn());
        tableContact.setOnEditCommit(event -> updateStudent("contact", event.getNewValue()));

        tableSemester.setCellFactory(TextFieldTableCell.forTableColumn());
        tableSemester.setOnEditCommit(event -> updateStudent("semester", event.getNewValue()));
    }

    @FXML
    void search() {
        searchStudent.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(student -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = newValue.toLowerCase();
            if (student.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else return student.getEmail().toLowerCase().contains(lowerCaseFilter);
        }));

        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    private void deleteStudent() {
		SessionFactory factory = MySessionFactory.getInstructorSF();

		Session session = factory.getCurrentSession();
		try {
			Student student = tableView.getSelectionModel().getSelectedItem();
			
			session.beginTransaction();
			session.delete(student);
			session.getTransaction().commit();
		}finally {
			session.close();
			factory.close();
		}

		loadStudents();
    }

    private void addStudent() {
        if(!verify()) return;

		SessionFactory factory = MySessionFactory.getInstructorSF();

		Session session = factory.getCurrentSession();
		
		try {
			Student tempStudent = new Student(
					name.getText().trim(),
					email.getText().trim(),
					contact.getText().trim(),
					department.getSelectionModel().getSelectedItem(),
					semesterComboBox.getSelectionModel().getSelectedItem());
			
			System.out.println(tempStudent);
			
			session.beginTransaction();
			session.save(tempStudent);
			session.getTransaction().commit();
			
		}finally{
			session.close();
			factory.close();
		}
		clearFields();
        loadStudents();	
    }


	private void loadStudents() {
		SessionFactory factory = MySessionFactory.getInstructorSF();

		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Student> loadedStudents = session.createQuery("from Student").getResultList();
			students.clear();
            students.addAll(loadedStudents);
	    	tableView.setItems(students);
	  
		}finally {
			session.close();
			factory.close();
		}
    }

    private boolean verify(){
        JFXSnackbar jfxSnackbar = new JFXSnackbar(studentAnchorPane);
        if(name.getText().trim().equals("")){
            new Shaker(name).shake();
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Name empty"));
            return false;
        }
        if(email.getText().trim().equals("")){
            new Shaker(email).shake();
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Email empty"));
            return false;
        }
        if(contact.getText().trim().equals("")){
            new Shaker(contact).shake();
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Contact empty"));
            return false;
        }
        if(department.getSelectionModel().isEmpty()){
            new Shaker(department).shake();
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Select Department"));
            return false;
        }
        if(semesterComboBox.getSelectionModel().isEmpty()){
            new Shaker(semesterComboBox).shake();
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Select Semester"));
            return false;
        }
        return true;
    }

    private void setDepartments(){
        department.getItems().add("CSE");
        department.getItems().add("EEE");
        department.getItems().add("ECE");
    }

    private void setSemesters(){
        String year = new SimpleDateFormat("yyyy").format(new Date());
        semesterComboBox.getItems().add("Spring " + year);
        semesterComboBox.getItems().add("Summer " + year);
        semesterComboBox.getItems().add("Fall " + year);
    }

    private void clearFields(){
        name.setText("");
        contact.setText("");
        email.setText("");
        semesterComboBox.getSelectionModel().clearSelection();
        department.getSelectionModel().clearSelection();
    }
}
