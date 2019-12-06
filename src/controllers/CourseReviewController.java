package controllers;

import animation.Shaker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import hibernate.MySessionFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import models.Course;
import models.Review;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class CourseReviewController {

    @FXML private JFXComboBox<String> courseCombo;
    @FXML private JFXTextArea review;
    @FXML private Button submitBtn;

    private List<Course> courses = new ArrayList<>();

    @FXML
    void initialize() {
        loadCoursesToCombobox();
        submitBtn.setOnAction(event -> submitReview());
    }

    private void submitReview() {
        if(!validate()) return;

        Review theReview = new Review(review.getText().trim());
        int position = courseCombo.getSelectionModel().getSelectedIndex();

        SessionFactory factory = MySessionFactory.getInstructorSF();
        Session session = factory.getCurrentSession();
        try {
            session.beginTransaction();
            Course course = session.get(Course.class, courses.get(position).getId());
            course.addReview(theReview);
            session.save(course);
            session.getTransaction().commit();
            System.out.println("saved review");
            clearFields();
        }finally {
            session.close();
            factory.close();
        }
    }

    private void clearFields() {
        courseCombo.getSelectionModel().clearSelection();
        review.clear();
    }

    private boolean validate() {
        if(courseCombo.getSelectionModel().isEmpty()){
            new Shaker(courseCombo).shake();
            return false;
        }
        if(review.getText().isEmpty()){
            new Shaker(review).shake();
            return false;
        }
        return true;
    }

    private void loadCoursesToCombobox() {
        SessionFactory factory = MySessionFactory.getInstructorSF();
        Session session = factory.getCurrentSession();
        try {
            session.beginTransaction();
            @SuppressWarnings("unchecked")
            List<Course> loadedCourses = session.createQuery("from Course").getResultList();
            for(Course course : loadedCourses){
                courses.add(course);
                courseCombo.getItems().add(course.getId() + ". " + course.getTitle());
            }

        }finally {
            session.close();
            factory.close();
        }
    }
}
