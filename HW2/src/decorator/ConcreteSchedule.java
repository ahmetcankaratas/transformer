package decorator;

import model.Course;
import java.util.ArrayList;
import java.util.List;

public class ConcreteSchedule implements ScheduleComponent {
    private List<Course> courses = new ArrayList<>();

    @Override
    public void addCourse(Course course) {
        courses.add(course);
        System.out.println("Added course: " + course.getName());
    }

    @Override
    public List<Course> getCourses() {
        return courses;
    }
}