package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DashboardController {
    @FXML private Button studentRegistrationBtn;
    @FXML private Button departmentsBtn;
    @FXML private Button instructorsBtn;
    @FXML private Button coursesBtn;
    @FXML private Button addStudentsToCoursesBtn;
    @FXML private Button btnSettings;
    @FXML private Button signOutBtn;
    @FXML private AnchorPane detailsPane;
    @FXML private Button reviewBtn;

    @FXML
    void initialize() {
        studentRegistrationBtn.setOnAction(event -> studentRegistration());
        instructorsBtn.setOnAction(event -> instructors());
        coursesBtn.setOnAction(event -> courses());
        addStudentsToCoursesBtn.setOnAction(event -> addStudentsToCourses());
        reviewBtn.setOnAction(event -> review());
    }

    private void review() {
        AnchorPane sr;
        try {
            sr = FXMLLoader.load(getClass().getResource("/views/course_review.fxml"));
            detailsPane.getChildren().setAll(sr);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void addStudentsToCourses() {
        AnchorPane sr;
        try {
            sr = FXMLLoader.load(getClass().getResource("/views/course_student.fxml"));
            detailsPane.getChildren().setAll(sr);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void courses() {
        AnchorPane sr;
        try {
            sr = FXMLLoader.load(getClass().getResource("/views/course_add.fxml"));
            detailsPane.getChildren().setAll(sr);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void instructors() {
        AnchorPane sr;
        try {
            sr = FXMLLoader.load(getClass().getResource("/views/instructor_add.fxml"));
            detailsPane.getChildren().setAll(sr);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void studentRegistration() {
            AnchorPane sr;
            try {
                sr = FXMLLoader.load(getClass().getResource("/views/student_registration.fxml"));
                detailsPane.getChildren().setAll(sr);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
    }
}
