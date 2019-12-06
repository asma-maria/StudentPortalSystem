package controllers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import hibernate.MySessionFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import models.Instructor;
import models.InstructorDetail;

import java.util.List;

public class InstructorAddController {
	
    @FXML private JFXTextField firstname;
    @FXML private JFXTextField lastname;
    @FXML private JFXTextField email;
    @FXML private JFXTextField details;
    @FXML private JFXTextField hobby;
    @FXML private JFXButton submitBtn;
    @FXML private JFXButton deleteBtn;
    @FXML private TableView<Instructor> tableView;
    @FXML private TableColumn<Instructor, Integer> tableId;
    @FXML private TableColumn<Instructor, String> tableFirstName;
    @FXML private TableColumn<Instructor, String> tableLastName;
    @FXML private TableColumn<Instructor, String> tableDetails;
    @FXML private TableColumn<Instructor, String> tableHobby;
    @FXML private JFXTextField search;
    @FXML private JFXButton clearFields;
    
    private ObservableList<Instructor> instructors = FXCollections.observableArrayList();
    private FilteredList<Instructor> filteredData = new FilteredList<>(instructors, i -> true);

    @FXML
    void initialize() {
        submitBtn.setOnAction( event-> addInstructor() );
        setTableProperties();
        loadInstructors();
        deleteBtn.setOnAction( event-> deleteInstructor() );
		clearFields.setOnAction( event-> clearFields() );
		tableFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        tableFirstName.setOnEditCommit(event -> updateTeacher("first_name", event.getNewValue()));
    }
    

    @FXML
    void searchTeacher() {
		search.textProperty().addListener((observableValue,oldValue,newValue)-> filteredData.setPredicate(instructor->{
			if(newValue==null||newValue.isEmpty()){
				return true;
			}
			String lowerCaseFilter=newValue.toLowerCase();
			if(instructor.getFirstName().toLowerCase().contains(lowerCaseFilter)){
				return true;
			}
			else return instructor.getEmail().toLowerCase().contains(lowerCaseFilter);
		}));
		SortedList<Instructor> sortedData=new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);
    }
    
    private void updateTeacher(String column, String newValue) {
		
	}

	private void deleteInstructor() {
		Instructor instructor = tableView.getSelectionModel().getSelectedItem();
		SessionFactory factory = MySessionFactory.getInstructorSF();
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			session.delete(instructor);
			session.getTransaction().commit();
	  
		}finally {
			session.close();
			factory.close();
		}
		loadInstructors();
	}

	private void setTableProperties(){
        tableId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableDetails.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getInstructorDetail().getDetails()));
        tableHobby.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getInstructorDetail().getHobby()));
    	tableFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
		tableLastName.setCellFactory(TextFieldTableCell.forTableColumn());
		tableDetails.setCellFactory(TextFieldTableCell.forTableColumn());
		tableHobby.setCellFactory(TextFieldTableCell.forTableColumn());

		tableFirstName.setOnEditCommit(event -> {

		});
    }
    
    @SuppressWarnings("unchecked")
	private void loadInstructors() {
    	SessionFactory factory = MySessionFactory.getInstructorSF();
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Instructor> loadedInstructors = session.createQuery("from Instructor").getResultList();
			instructors.clear();
			instructors.addAll(loadedInstructors);
//	    	instructors = FXCollections.observableArrayList(session.createQuery("from Instructor").list());
	    	tableView.setItems(instructors);
	  
		}finally {
			session.close();
			factory.close();
		}
		
	}

	private void addInstructor() {
    	// verify
    
		SessionFactory factory = MySessionFactory.getInstructorSF();
		Session session = factory.getCurrentSession();
		try {
	    	Instructor instructor = new Instructor(firstname.getText().trim(),
	    			lastname.getText().trim(), email.getText().trim());
	    	
	    	InstructorDetail instructorDetail = new InstructorDetail(
	    			details.getText().trim(), hobby.getText().trim());
	    	
	    	instructor.setInstructorDetail(instructorDetail);
			
			session.beginTransaction();
			session.save(instructor);
			session.getTransaction().commit();
			
			
		}finally {
			session.close();
			factory.close();
		}
		loadInstructors();
		clearFields();
    }
	
	private void clearFields() {
		firstname.clear();
		lastname.clear();
		email.clear();
		details.clear();
		hobby.clear();
	}

}
