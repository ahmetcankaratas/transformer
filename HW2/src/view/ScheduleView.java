package view;

import model.Course;
import java.util.List;
import java.time.DayOfWeek;
import java.util.stream.Collectors;
import java.util.Arrays;

public class ScheduleView {
    public String generateCombinedHtml(List<Course> undergradCourses, List<Course> gradCourses, List<Course> nonCengCourses) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
            .append("<html>\n<head>\n")
            .append("<title>Schedule Demo - Undergraduate vs Graduate</title>\n")
            .append("<style>\n")
            .append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n")
            .append(".schedule-container { margin-bottom: 40px; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n")
            .append("h1 { color: #333; text-align: center; }\n")
            .append("h2 { color: #444; margin-bottom: 15px; }\n")
            .append("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }\n")
            .append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n")
            .append("th { background-color: #4CAF50; color: white; }\n")
            .append("tr:nth-child(even) { background-color: #f9f9f9; }\n")
            .append(".course { padding: 8px; border-radius: 4px; color: white; font-weight: bold; margin-bottom: 4px; display: block; }\n")
            .append(".undergrad { background-color: #2196F3; }\n")
            .append(".grad { background-color: #9C27B0; }\n")
            .append(".constraints { background-color: #fff3cd; padding: 15px; border-radius: 4px; margin-top: 10px; }\n")
            .append(".constraints h3 { margin-top: 0; color: #856404; }\n")
            .append(".constraints ul { margin: 0; padding-left: 20px; }\n")
            .append(".professor { font-size: 0.8em; color: #666; margin-top: 4px; }\n")
            .append("</style>\n")
            .append("</head>\n<body>\n")
            .append("<h1>University Course Schedule Demo</h1>\n");

        // Undergraduate Schedule
        html.append("<div class=\"schedule-container\">\n")
            .append("<h2>Undergraduate Schedule (NoClashConstraint)</h2>\n")
            .append("<div class=\"constraints\">\n")
            .append("<h3>Applied Constraints:</h3>\n")
            .append("<ul>\n")
            .append("<li>NoClashConstraint: Prevents scheduling conflicts between courses</li>\n")
            .append("<li>Validation Decorator: Validates course additions</li>\n")
            .append("<li>Logging Decorator: Tracks schedule modifications</li>\n")
            .append("</ul>\n")
            .append("</div>\n")
            .append(generateScheduleTable(undergradCourses, "undergrad"))
            .append("</div>\n");

        // Graduate Schedule
        html.append("<div class=\"schedule-container\">\n")
            .append("<h2>Graduate Schedule (TimeWindowConstraint)</h2>\n")
            .append("<div class=\"constraints\">\n")
            .append("<h3>Applied Constraints:</h3>\n")
            .append("<ul>\n")
            .append("<li>TimeWindowConstraint: Courses only between 9 AM - 5 PM</li>\n")
            .append("<li>CourseCodeValidationDecorator: Only CENG courses</li>\n")
            .append("<li>Validation Decorator: Validates time constraints</li>\n")
            .append("<li>Logging Decorator: Tracks schedule modifications</li>\n")
            .append("</ul>\n")
            .append("</div>\n")
            .append(generateScheduleTable(gradCourses, "grad"))
            .append("</div>\n");

        // Non-CENG Courses
        if (!nonCengCourses.isEmpty()) {
            html.append("<div class=\"schedule-container\">\n")
                .append("<h2>Non-CENG Courses (Filtered by CourseCodeValidationDecorator)</h2>\n")
                .append("<div class=\"constraints\">\n")
                .append("<h3>Applied Constraints:</h3>\n")
                .append("<ul>\n")
                .append("<li>CourseCodeValidationDecorator: Only CENG courses allowed</li>\n")
                .append("<li>These courses are filtered out by the decorator</li>\n")
                .append("</ul>\n")
                .append("</div>\n")
                .append(generateScheduleTable(nonCengCourses, "filtered"))
                .append("</div>\n");
        }

        html.append("</body>\n</html>");
        return html.toString();
    }

    private String generateScheduleTable(List<Course> courses, String type) {
        StringBuilder table = new StringBuilder();
        table.append("<table>\n")
             .append("<tr>\n")
             .append("<th>Time</th>\n");

        // Add day headers (only weekdays)
        Arrays.stream(DayOfWeek.values())
            .filter(day -> day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY)
            .forEach(day -> table.append("<th>").append(day).append("</th>\n"));
        table.append("</tr>\n");

        // Generate time slots (assuming 30-minute intervals from 8:00 to 20:00)
        for (int hour = 8; hour < 20; hour++) {
            final int currentHour = hour;  // Make hour effectively final for lambda
            for (int minute = 0; minute < 60; minute += 30) {
                final int currentMinute = minute;  // Make minute effectively final for lambda
                String time = String.format("%02d:%02d", currentHour, currentMinute);
                table.append("<tr>\n")
                     .append("<td>").append(time).append("</td>\n");

                // Add cells for each weekday
                Arrays.stream(DayOfWeek.values())
                    .filter(day -> day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY)
                    .forEach(day -> {
                        List<Course> coursesAtTime = courses.stream()
                            .filter(c -> c.getDay() == day && 
                                   c.getTime().getHour() == currentHour && 
                                   c.getTime().getMinute() == currentMinute)
                            .collect(Collectors.toList());

                        table.append("<td>");
                        for (Course course : coursesAtTime) {
                            if (type.equals("filtered")) {
                                table.append("<span style=\"color: #666; text-decoration: line-through;\">")
                                     .append(course.getName())
                                     .append("</span>");
                            } else {
                                table.append("<span class=\"course ").append(type).append("\">")
                                     .append(course.getName())
                                     .append("</span>");
                            }
                            table.append("<div class=\"professor\">")
                                 .append("Prof. ").append(course.getProfessor())
                                 .append("</div>");
                        }
                        table.append("</td>\n");
                    });
                table.append("</tr>\n");
            }
        }

        table.append("</table>\n");
        return table.toString();
    }
} 