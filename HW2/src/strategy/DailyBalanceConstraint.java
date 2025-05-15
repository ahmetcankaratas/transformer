package strategy;

import model.Course;
import model.Schedule;

public class DailyBalanceConstraint implements ConstraintStrategy {
    @Override
    public boolean isSatisfied(Schedule schedule, Course course) {
        int dailyCourseCount = 0;
        for (Course c : schedule.getCourses()) {
            if (c.getDay().equals(course.getDay())) {
                dailyCourseCount++;
            }
        }
        return dailyCourseCount < 3;
    }
}