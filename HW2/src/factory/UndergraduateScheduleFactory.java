package factory;

import model.Schedule;
import model.BaseSchedule;
import strategy.ConstraintStrategy;
import strategy.NoClashConstraint;
import decorator.LoggingScheduleDecorator;
import decorator.ValidationScheduleDecorator;

public class UndergraduateScheduleFactory extends ScheduleFactory {
    @Override
    public Schedule createSchedule() {
        Schedule schedule = new BaseSchedule();
        ConstraintStrategy constraint = new NoClashConstraint();
        schedule = new ValidationScheduleDecorator(schedule, constraint);
        schedule = new LoggingScheduleDecorator(schedule);
        return schedule;
    }
}
