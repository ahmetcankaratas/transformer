package decorator;

import model.Course;
import model.Schedule;

public class ValidationDecorator extends ScheduleDecorator {
    public ValidationDecorator(Schedule decoratedSchedule) {
        super(decoratedSchedule);
    }

    @Override
    public void addCourse(Course course) {
        if (course.getName() != null && course.getDay() != null && course.getTime() != null) {
            super.addCourse(course);
        } else {
            System.out.println("Invalid course information, not added.");
        }
    }
}
