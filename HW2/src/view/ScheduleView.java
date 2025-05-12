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
            .append("<meta charset='UTF-8'>\n")
            .append("<title>Weekly Schedule</title>\n")
            .append("<style>\n")
            .append("body { font-family: Arial, sans-serif; background-color: #f7f7f7; margin: 0; padding: 0; }\n")
            .append("h2 { background-color: #0A1D37; color: white; text-align: center; padding: 20px 0; margin: 0; }\n")
            .append(".tab { display: flex; justify-content: center; background-color: #eee; }\n")
            .append(".tab button { background-color: transparent; border: none; padding: 14px 20px; cursor: pointer; font-size: 16px; color: #0A1D37; border-bottom: 3px solid transparent; transition: 0.3s; }\n")
            .append(".tab button:hover { border-bottom: 3px solid #8B0000; }\n")
            .append(".tab button.active { border-bottom: 3px solid #0A1D37; font-weight: bold; }\n")
            .append(".tabcontent { display: none; padding: 20px; }\n")
            .append("table { border-collapse: collapse; width: 100%; background-color: white; }\n")
            .append("th { background-color: #0A1D37; color: white; padding: 10px; }\n")
            .append("td { border: 1px solid #ddd; padding: 10px; text-align: center; min-height: 50px; }\n")
            .append("td:first-child { background-color: #f2f2f2; font-weight: bold; }\n")
            .append("</style>\n")
            .append("<script>\n")
            .append("function openTab(evt, tabName) {\n")
            .append("    var i, tabcontent, tablinks;\n")
            .append("    tabcontent = document.getElementsByClassName('tabcontent');\n")
            .append("    for (i = 0; i < tabcontent.length; i++) {\n")
            .append("        tabcontent[i].style.display = 'none';\n")
            .append("    }\n")
            .append("    tablinks = document.getElementsByClassName('tablink');\n")
            .append("    for (i = 0; i < tablinks.length; i++) {\n")
            .append("        tablinks[i].className = tablinks[i].className.replace(' active', '');\n")
            .append("    }\n")
            .append("    document.getElementById(tabName).style.display = 'block';\n")
            .append("    evt.currentTarget.className += ' active';\n")
            .append("}\n")
            .append("window.onload = function() { document.getElementById('defaultOpen').click(); };\n")
            .append("</script>\n")
            .append("</head>\n<body>\n")
            .append("<h2>Weekly Course Schedules for 2024–2025 Spring</h2>\n")
            .append("<div class='tab'>\n")
            .append("    <button class='tablink' id='defaultOpen' onclick=\"openTab(event, 'Undergrad')\">Undergraduate</button>\n")
            .append("    <button class='tablink' onclick=\"openTab(event, 'Grad')\">Graduate</button>\n")
            .append("    <button class='tablink' onclick=\"openTab(event, 'NonCENG')\">Non-CENG</button>\n")
            .append("</div>\n");

        // Undergraduate Schedule
        html.append("<div id='Undergrad' class='tabcontent'>\n")
            .append(generateScheduleTable(undergradCourses))
            .append("</div>\n");

        // Graduate Schedule
        html.append("<div id='Grad' class='tabcontent'>\n")
            .append(generateScheduleTable(gradCourses))
            .append("</div>\n");

        // Non-CENG Courses
        html.append("<div id='NonCENG' class='tabcontent'>\n")
            .append(generateScheduleTable(nonCengCourses))
            .append("</div>\n");

        html.append("</body>\n</html>");
        return html.toString();
    }

    private String generateScheduleTable(List<Course> courses) {
        StringBuilder table = new StringBuilder();
        table.append("<table>\n")
             .append("<tr>\n")
             .append("<th>Hours</th>\n");

        // Add day headers (only weekdays)
        Arrays.stream(DayOfWeek.values())
            .filter(day -> day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY)
            .forEach(day -> table.append("<th>").append(day).append("</th>\n"));
        table.append("</tr>\n");

        // Define time slots
        String[] timeSlots = {
            "08:45–09:30", "09:45–10:30", "10:45–11:30", "11:45–12:30",
            "13:30–14:15", "14:30–15:15", "15:30–16:15", "16:30–17:15"
        };

        for (String timeSlot : timeSlots) {
            String[] times = timeSlot.split("–");
            String startTime = times[0];
            String endTime = times[1];
            
            int startHour = Integer.parseInt(startTime.split(":")[0]);
            int startMinute = Integer.parseInt(startTime.split(":")[1]);
            
            table.append("<tr>\n")
                 .append("<td>").append(timeSlot).append("</td>\n");

            // Add cells for each weekday
            Arrays.stream(DayOfWeek.values())
                .filter(day -> day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY)
                .forEach(day -> {
                    List<Course> coursesAtTime = courses.stream()
                        .filter(c -> c.getDay() == day && 
                               c.getTime().getHour() == startHour && 
                               c.getTime().getMinute() == startMinute)
                        .collect(Collectors.toList());

                    table.append("<td>");
                    for (Course course : coursesAtTime) {
                        table.append("<strong>").append(course.getName()).append("</strong><br>")
                             .append(course.getProfessor());
                    }
                    table.append("</td>\n");
                });
            table.append("</tr>\n");
        }

        table.append("</table>\n");
        return table.toString();
    }
} 