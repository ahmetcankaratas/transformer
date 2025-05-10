package model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Course {
    private String name;
    private DayOfWeek day;
    private LocalTime time;
    private String professor;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Course(String name, String day, String time, String professor) {
        validateInput(name, day, time);
        this.name = name;
        this.day = parseDay(day);
        this.time = parseTime(time);
        this.professor = professor;
    }

    private void validateInput(String name, String day, String time) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        if (day == null || day.trim().isEmpty()) {
            throw new IllegalArgumentException("Day cannot be empty");
        }
        if (time == null || time.trim().isEmpty()) {
            throw new IllegalArgumentException("Time cannot be empty");
        }
    }

    private DayOfWeek parseDay(String day) {
        try {
            return DayOfWeek.valueOf(day.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid day: " + day);
        }
    }

    private LocalTime parseTime(String time) {
        try {
            return LocalTime.parse(time, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Use HH:mm format");
        }
    }

    public String getName() {
        return name;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getProfessor() {
        return professor;
    }

    @Override
    public String toString() {
        return String.format("%s; %s; %s", name, day, time.format(TIME_FORMATTER));
    }
}