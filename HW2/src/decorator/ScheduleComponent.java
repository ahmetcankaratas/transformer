package decorator;

import model.Course;
import java.util.List;

public interface ScheduleComponent {
    void addCourse(Course course);
    List<Course> getCourses();
}