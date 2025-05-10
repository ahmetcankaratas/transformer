package decorator;

import model.Course;
import model.Schedule;

public class LoggingDecorator extends ScheduleDecorator {
    public LoggingDecorator(Schedule decoratedSchedule) {
        super(decoratedSchedule);
    }

    @Override
    public void addCourse(Course course) {
        System.out.println("Log: Adding course " + course.getName() + "...");
        super.addCourse(course);
    }
}
