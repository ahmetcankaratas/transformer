package decorator;

import model.Course;
import model.Schedule;
import java.util.List;

public abstract class ScheduleDecorator implements Schedule {
    protected Schedule schedule;

    public ScheduleDecorator(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public void addCourse(Course course) {
        schedule.addCourse(course);
    }

    @Override
    public List<Course> getCourses() {
        return schedule.getCourses();
    }

    @Override
    public boolean removeCourse(Course course) {
        return schedule.removeCourse(course);
    }

    @Override
    public void clear() {
        schedule.clear();
    }
}