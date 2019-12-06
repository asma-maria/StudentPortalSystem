package models;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="student")
public class Student {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="name")
	private String name;

	@Column(name="email")
	private String email;
	
	@Column(name="contact")
	private String contact;
	
	@Column(name="department")
	private String department;
	
	@Column(name="semester")
	private String semester;

	@ManyToMany(fetch = FetchType.LAZY, cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "course_student",
			joinColumns = @JoinColumn(name = "student_id"),
			inverseJoinColumns = @JoinColumn(name = "course_id"))
	private List<Course> courses;

	public void addCourse(Course course){
		if(courses==null){
			courses = new ArrayList<>();
		}
		courses.add(course);
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public Student() {	}

	public Student(String name, String email, String contact, String department, String semester) {
		this.name = name;
		this.email = email;
		this.contact = contact;
		this.department = department;
		this.semester = semester;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	@Override
	public String toString() {
		return name;
	}
}
