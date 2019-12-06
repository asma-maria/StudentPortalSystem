package hibernate;

import models.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class MySessionFactory {
	
	public static SessionFactory getInstructorSF() {
		SessionFactory factory = new Configuration().configure("hibernate.cfg.xml")
				.addAnnotatedClass(Instructor.class).addAnnotatedClass(InstructorDetail.class).
						addAnnotatedClass(Course.class).addAnnotatedClass(Review.class)
				.addAnnotatedClass(Student.class).buildSessionFactory();
		
		return factory;
	}
	
//	public static SessionFactory getStudentSF() {
//		SessionFactory factory = new Configuration().configure("hibernate.cfg.xml")
//				.addAnnotatedClass(Student.class).buildSessionFactory();
//
//		return factory;
//
//	}
}
