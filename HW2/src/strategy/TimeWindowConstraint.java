package strategy;

import model.Schedule;
import model.Course;

public class TimeWindowConstraint implements ConstraintStrategy {
    private final int minHour;
    private final int maxHour;

    public TimeWindowConstraint(int minHour, int maxHour) {
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    @Override
    public boolean isSatisfied(Schedule schedule, Course course) {
        int courseHour = course.getTime().getHour();
        return courseHour >= minHour && courseHour <= maxHour;
    }
} 