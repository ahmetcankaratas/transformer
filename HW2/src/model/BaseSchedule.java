package model;

import java.util.ArrayList;
import java.util.List;

public class BaseSchedule implements Schedule {
    protected List<Course> courses;

    public BaseSchedule() {
        this.courses = new ArrayList<>();
    }

    @Override
    public void addCourse(Course course) {
        courses.add(course);
    }

    @Override
    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    @Override
    public boolean removeCourse(Course course) {
        return courses.remove(course);
    }

    @Override
    public void clear() {
        courses.clear();
    }
} 