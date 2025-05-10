package decorator;

import model.Course;
import model.Schedule;
import java.util.List;
import java.util.stream.Collectors;

public class CourseCodeValidationDecorator extends ScheduleDecorator {
    private static final String VALID_DEPARTMENT = "CENG";

    public CourseCodeValidationDecorator(Schedule schedule) {
        super(schedule);
    }

    @Override
    public void addCourse(Course course) {
        if (isValidCourseCode(course.getName())) {
            super.addCourse(course);
        } else {
            System.out.println("Skipping non-CENG course: " + course.getName());
        }
    }

    @Override
    public List<Course> getCourses() {
        // Filter to return only CENG courses
        return super.getCourses().stream()
                .filter(course -> isValidCourseCode(course.getName()))
                .collect(Collectors.toList());
    }

    private boolean isValidCourseCode(String courseCode) {
        return courseCode != null && 
               courseCode.startsWith(VALID_DEPARTMENT) && 
               courseCode.length() == 7; // CENG + 3 digits
    }
} 