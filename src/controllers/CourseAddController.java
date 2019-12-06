package controllers;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;

import animation.Shaker;
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
import javafx.scene.layout.AnchorPane;
import models.Course;
import models.Instructor;

public class CourseAddController {
    @FXML private AnchorPane courseAnchor;
    @FXML private JFXTextField courseTitle;
    @FXML private JFXTextField courseCode;
    @FXML private JFXComboBox<String> selectTeacherCombo;
    @FXML private JFXButton clearFieldsBtn;
    @FXML private JFXButton addCourseBtn;
    @FXML private TableView<Course> tableView;
    @FXML private TableColumn<Course, Integer> tableId;
    @FXML private TableColumn<Course, String> tableTitle;
    @FXML private TableColumn<Course, String> tableCode;
    @FXML private TableColumn<Course, String> tableTeacher;
    @FXML private JFXButton deleteCourseBtn;
    @FXML private JFXTextField searchCourse;
    
    private ObservableList<Course> courses = FXCollections.observableArrayList();
    private FilteredList<Course> filteredData = new FilteredList<>(courses, p -> true);
    
    @FXML
    void initialize() {
    	loadTableProperties();
        loadCourses();
        loadInstructorsToCombobox();
    	addCourseBtn.setOnAction(event -> addCourse());
    	clearFieldsBtn.setOnAction(event-> clearFields());
    	deleteCourseBtn.setOnAction(event-> deleteCourse());
    }
    
    private void deleteCourse() {
		// TODO Auto-generated method stub
		
	}

	private void loadInstructorsToCombobox() {
		SessionFactory factory = MySessionFactory.getInstructorSF();
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Instructor> loadedInstructors = session.createQuery("from Instructor").getResultList();
			
			for(Instructor instructor : loadedInstructors) {
				selectTeacherCombo.getItems().add(instructor.getId() + ". " + instructor.getFullName());
			}
			
			
		}finally {
			session.close();
			factory.close();
		}
		
	}

	private void addCourse() {
		if(!verify()) return;
		
		SessionFactory factory = MySessionFactory.getInstructorSF();
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			char id = selectTeacherCombo.getSelectionModel().getSelectedItem().charAt(0);
			
			Instructor instructor = session.get(Instructor.class, Character.getNumericValue(id));
			Course course = new Course(courseTitle.getText(), courseCode.getText());
			course.setInstructor(instructor);
			System.out.println(instructor.getEmail());
			session.save(course);
			session.getTransaction().commit();
			loadCourses();
			clearFields();
		}finally {
			session.close();
			factory.close();
		}
		
	}
	
    private boolean verify(){
        JFXSnackbar jfxSnackbar = new JFXSnackbar(courseAnchor);
        if(courseTitle.getText().trim().equals("")){
            new Shaker(courseTitle).shake();
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Title empty"));
            return false;
        }
        if(courseCode.getText().trim().equals("")){
            new Shaker(courseCode).shake();
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Code empty"));
            return false;
        }
        if(selectTeacherCombo.getSelectionModel().isEmpty()){
            new Shaker(selectTeacherCombo).shake();
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Select Teacher"));
            return false;
        }
        return true;
    }

	@FXML
    void search() {
    	System.out.println("search");
    	searchCourse.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(course -> {
			if (newValue == null || newValue.isEmpty())
				return true;

			String lowerCaseFilter = newValue.toLowerCase();
			return course.getTitle().toLowerCase().contains(lowerCaseFilter);
		}));
        
        SortedList<Course> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }
    
    private void loadTableProperties() {
		tableId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		tableCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        tableTeacher.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getInstructor().getFullName()));
	}

	private void loadCourses() {
		SessionFactory factory = MySessionFactory.getInstructorSF();
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Course> loadedCourses = session.createQuery("from Course").getResultList();
			courses.clear();
			courses.addAll(loadedCourses);
			tableView.setItems(courses);
		}finally {
			session.close();
			factory.close();
		}
	}

	private void clearFields() {
    	courseTitle.clear();
    	courseCode.clear();
    	selectTeacherCombo.getSelectionModel().clearSelection();
    }
}
