package controllers;

import com.jfoenix.controls.*;
import hibernate.MySessionFactory;
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
import models.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.Query;
import java.util.List;

public class CourseStudentController {
    @FXML private AnchorPane anchorPane;
    @FXML private JFXComboBox<String> courseCombo;
    @FXML private JFXButton addStudentBtn;
    @FXML private JFXTextField searchStudent;
    @FXML private JFXListView<Student> listView;
    @FXML private TableView<Student> tableView;
    @FXML private TableColumn<Student, Integer> tableId;
    @FXML private TableColumn<Student, String> tableName;
    @FXML private TableColumn<Student, String> tableDepartment;
    @FXML private TableColumn<Student, String> tableSemester;
    @FXML private JFXButton removeStudentBtn;
    @FXML private JFXButton confirmBtn;

    private ObservableList<Course> courses = FXCollections.observableArrayList();
    private ObservableList<Student> students = FXCollections.observableArrayList();
    private FilteredList<Student> filteredData = new FilteredList<>(students, p -> true);

    @FXML
    void initialize() {
        setTableProperties();
        loadCoursesToCombobox();
        loadStudentsToTable();
        addStudentBtn.setOnAction(event -> addStudent());
        removeStudentBtn.setOnAction(event -> removeStudent());
        confirmBtn.setOnAction(event -> confirmStudent());
    }

    private void confirmStudent() {
        if(courseCombo.getSelectionModel().isEmpty()){
            JFXSnackbar jfxSnackbar = new JFXSnackbar(anchorPane);
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Select Course"));
            return;
        }
        SessionFactory factory = MySessionFactory.getInstructorSF();
        Session session = factory.getCurrentSession();

        try {
            session.beginTransaction();
            String[] temp = courseCombo.getSelectionModel().getSelectedItem().split(" ");
            Course course = session.get(Course.class, Integer.parseInt(temp[0]));
            ObservableList<Student> tempStudents = listView.getItems();
            if(tempStudents.size()==0){
                System.out.println(tempStudents.size());
//                JFXSnackbar jfxSnackbar = new JFXSnackbar(anchorPane);
//                jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("No students selected!"));
                return;
            }
            System.out.println(tempStudents);
            for(Student student : tempStudents)
                course.addStudent(student);

            session.save(course);
            session.getTransaction().commit();
            System.out.println("saved");
        }finally {
            session.close();
            factory.close();
        }
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

    private void removeStudent() {
        if (listView.getSelectionModel().getSelectedItem() == null) {
            JFXSnackbar jfxSnackbar = new JFXSnackbar(anchorPane);
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Select a student!"));
            return;
        }
        Student student = listView.getSelectionModel().getSelectedItem();
        listView.getItems().remove(student);
        students.add(student);
        tableView.setItems(students);
    }

    private void addStudent() {
        if(courseCombo.getSelectionModel().isEmpty()){
            JFXSnackbar jfxSnackbar = new JFXSnackbar(anchorPane);
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Select Course"));
            return;
        }
        if(tableView.getSelectionModel().isEmpty()){
            JFXSnackbar jfxSnackbar = new JFXSnackbar(anchorPane);
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Select a student!"));
            return;
        }
        Student student = tableView.getSelectionModel().getSelectedItem();
        if(listView.getItems().contains(student)){
            JFXSnackbar jfxSnackbar = new JFXSnackbar(anchorPane);
            jfxSnackbar.enqueue(new JFXSnackbar.SnackbarEvent("Already added!"));
            return;
        }
        listView.getItems().add(student);
        students.remove(student);
        tableView.setItems(students);
    }

    private void setTableProperties() {
        tableId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        tableSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
    }

    private void loadStudentsToTable() {
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

    private void loadCoursesToCombobox() {
        SessionFactory factory = MySessionFactory.getInstructorSF();
        Session session = factory.getCurrentSession();

        try {
            session.beginTransaction();
            @SuppressWarnings("unchecked")
            List<Course> loadedCourses = session.createQuery("from Course").getResultList();
            courses.clear();
            courses.addAll(loadedCourses);
            for (Course course : courses){
                courseCombo.getItems().add(course.getId() + " " + course.getTitle());
            }
        }finally {
            session.close();
            factory.close();
        }
    }


}
