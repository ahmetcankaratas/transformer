package strategy;

import model.Schedule;
import model.Course;

public interface ConstraintStrategy {
    boolean isSatisfied(Schedule schedule, Course course);
}