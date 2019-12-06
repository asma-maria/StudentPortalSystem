import models.Course;
import models.Instructor;
import models.InstructorDetail;
import models.Review;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Test {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(Instructor.class).
                        addAnnotatedClass(InstructorDetail.class).
                        addAnnotatedClass(Course.class).
                        addAnnotatedClass(Review.class).
                        buildSessionFactory();
        Session session = factory.getCurrentSession();
        try {
            Course course = new Course("Object","CSE310");
            course.addReview(new Review("nice course"));
            course.addReview(new Review("stupid.... waste of time"));
            session.beginTransaction();
            session.save(course);
            session.getTransaction().commit();
        }finally {
            session.close();
            factory.close();
        }
    }
}
