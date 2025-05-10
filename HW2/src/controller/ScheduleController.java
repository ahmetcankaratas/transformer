package controller;

import model.Schedule;
import model.Course;
import factory.ScheduleFactory;
import java.util.List;

public class ScheduleController {
    private Schedule schedule;
    private ScheduleFactory factory;

    public ScheduleController(ScheduleFactory factory) {
        this.factory = factory;
        this.schedule = factory.createSchedule();
    }

    public void addCourse(String name, String day, String time, String professor) {
        try {
            Course course = new Course(name, day, time, professor);
            schedule.addCourse(course);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to add course: " + e.getMessage());
        }
    }

    public List<Course> getCourses() {
        return schedule.getCourses();
    }

    public boolean removeCourse(String name, String day, String time, String professor) {
        Course course = new Course(name, day, time, professor);
        return schedule.removeCourse(course);
    }

    public void clearSchedule() {
        schedule.clear();
    }
} 