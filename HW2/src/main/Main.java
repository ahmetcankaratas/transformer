package main;

import model.*;
import strategy.*;
import factory.*;
import decorator.*;
import controller.*;
import view.*;
import util.CourseFileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            // Create both schedule factories
            ScheduleFactory undergradFactory = new UndergraduateScheduleFactory();
            ScheduleFactory gradFactory = new GraduateScheduleFactory();

            // Create controllers for both schedule types
            ScheduleController undergradController = new ScheduleController(undergradFactory);
            ScheduleController gradController = new ScheduleController(gradFactory);

            // Read all courses from professor files
            List<Course> allCourses = CourseFileReader.readAllProfessorSchedules();
            
            // Separate courses into undergraduate and graduate
            List<Course> undergradCourses = new ArrayList<>();
            List<Course> gradCourses = new ArrayList<>();
            List<Course> nonCengCourses = new ArrayList<>();

            for (Course course : allCourses) {
                if (course.getName().startsWith("CENG")) {
                    if (course.getName().startsWith("CENG6")) {
                        gradCourses.add(course);
                    } else {
                        undergradCourses.add(course);
                    }
                } else {
                    nonCengCourses.add(course);
                }
            }

            // Add undergraduate courses
            for (Course course : undergradCourses) {
                try {
                    undergradController.addCourse(
                        course.getName(), 
                        course.getDay().toString(), 
                        course.getTime().toString(),
                        course.getProfessor()
                    );
                } catch (IllegalArgumentException e) {
                    System.out.println("Could not add undergraduate course: " + course.getName() + 
                                     " - " + e.getMessage());
                }
            }

            // Add graduate courses
            for (Course course : gradCourses) {
                try {
                    gradController.addCourse(
                        course.getName(), 
                        course.getDay().toString(), 
                        course.getTime().toString(),
                        course.getProfessor()
                    );
                } catch (IllegalArgumentException e) {
                    System.out.println("Could not add graduate course: " + course.getName() + 
                                     " - " + e.getMessage());
                }
            }

            // Create view and generate HTML
            ScheduleView view = new ScheduleView();
            String html = view.generateCombinedHtml(
                undergradController.getCourses(),
                gradController.getCourses(),
                nonCengCourses
            );

            // Save HTML to file
            try (FileWriter writer = new FileWriter("schedule_demo.html")) {
                writer.write(html);
            }

            System.out.println("Schedule has been generated and saved to schedule_demo.html");
            System.out.println("Total courses loaded: " + allCourses.size());
            System.out.println("Undergraduate courses in schedule: " + undergradController.getCourses().size());
            System.out.println("Graduate courses in schedule: " + gradController.getCourses().size());
            System.out.println("Non-CENG courses filtered out: " + nonCengCourses.size());

        } catch (IOException e) {
            System.err.println("Error writing schedule to file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}