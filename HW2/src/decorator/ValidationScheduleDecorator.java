package decorator;

import model.Course;
import model.Schedule;
import strategy.ConstraintStrategy;

public class ValidationScheduleDecorator extends ScheduleDecorator {
    private final ConstraintStrategy constraint;

    public ValidationScheduleDecorator(Schedule schedule, ConstraintStrategy constraint) {
        super(schedule);
        this.constraint = constraint;
    }

    @Override
    public void addCourse(Course course) {
        if (constraint.isSatisfied(schedule, course)) {
            super.addCourse(course);
        } else {
            throw new IllegalArgumentException("Course violates scheduling constraints: " + course);
        }
    }
} 