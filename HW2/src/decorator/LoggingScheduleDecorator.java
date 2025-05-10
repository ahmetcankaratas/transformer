package decorator;

import model.Course;
import model.Schedule;

public class LoggingScheduleDecorator extends ScheduleDecorator {
    public LoggingScheduleDecorator(Schedule schedule) {
        super(schedule);
    }

    @Override
    public void addCourse(Course course) {
        System.out.println("Adding course: " + course);
        super.addCourse(course);
    }

    @Override
    public boolean removeCourse(Course course) {
        System.out.println("Removing course: " + course);
        return super.removeCourse(course);
    }
} 