package factory;

import model.Schedule;
import model.BaseSchedule;
import strategy.ConstraintStrategy;
import strategy.TimeWindowConstraint;
import decorator.LoggingScheduleDecorator;
import decorator.ValidationScheduleDecorator;
import decorator.CourseCodeValidationDecorator;

public class GraduateScheduleFactory extends ScheduleFactory {
    @Override
    public Schedule createSchedule() {
        Schedule schedule = new BaseSchedule();
        ConstraintStrategy constraint = new TimeWindowConstraint(9, 17); // Graduate courses only during business hours
        
        // Add decorators in the correct order
        schedule = new CourseCodeValidationDecorator(schedule);  // First validate course codes
        schedule = new ValidationScheduleDecorator(schedule, constraint);  // Then check time constraints
        schedule = new LoggingScheduleDecorator(schedule);  // Finally add logging
        
        return schedule;
    }
}