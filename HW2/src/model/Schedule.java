package model;

import java.util.List;

public interface Schedule {
    void addCourse(Course course);
    List<Course> getCourses();
    boolean removeCourse(Course course);
    void clear();
}
