package strategy;

import model.Schedule;
import model.Course;

public class NoClashConstraint implements ConstraintStrategy {
    @Override
    public boolean isSatisfied(Schedule schedule, Course course) {
        return schedule.getCourses().stream()
                .noneMatch(existingCourse -> 
                    existingCourse.getDay() == course.getDay() && 
                    existingCourse.getTime().equals(course.getTime()));
    }
}
