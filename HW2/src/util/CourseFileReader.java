package util;

import model.Course;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CourseFileReader {
    
    public static List<Course> readCoursesFromFile(String filePath) {
        List<Course> courses = new ArrayList<>();
        String professor = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.lastIndexOf('.'));
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(";");
                    if (parts.length == 3) {
                        String courseCode = parts[0].trim();
                        String day = parts[1].trim();
                        String time = parts[2].trim();
                        
                        try {
                            Course course = new Course(courseCode, day, time, professor);
                            courses.add(course);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Error parsing course in file " + filePath + ": " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
        }
        
        return courses;
    }
    
    public static List<Course> readAllProfessorSchedules() {
        List<Course> allCourses = new ArrayList<>();
        String[] professors = {
            "OGokalp", "AYilmaz", "KDemir", "SSahin", "MTekin"
        };
        
        for (String professor : professors) {
            String filePath = "resources/" + professor + ".txt";
            allCourses.addAll(readCoursesFromFile(filePath));
        }
        
        return allCourses;
    }
} 