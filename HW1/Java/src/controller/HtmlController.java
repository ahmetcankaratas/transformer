package controller;

import java.util.List;

import model.TableData;

/**
 * Controller class for generating HTML
 */
public class HtmlController {
    
    /**
     * Generate HTML table from TableData
     * @param tableData The data to convert to HTML
     * @return HTML string
     */
    public String generateHtmlTable(TableData tableData) {
        StringBuilder html = new StringBuilder();
        
        html.append("<table class=\"table table-bordered\">\n");
        
        // Generate table header
        html.append("  <thead>\n");
        html.append("    <tr>\n");
        for (String header : tableData.getHeaders()) {
            html.append("      <th>").append(header).append("</th>\n");
        }
        html.append("    </tr>\n");
        html.append("  </thead>\n");
        
        // Generate table body
        html.append("  <tbody>\n");
        for (List<String> row : tableData.getRows()) {
            html.append("    <tr>\n");
            for (String cell : row) {
                html.append("      <td>").append(cell).append("</td>\n");
            }
            html.append("    </tr>\n");
        }
        html.append("  </tbody>\n");
        
        html.append("</table>");
        
        return html.toString();
    }
    
    /**
     * Generate HTML tabs from multiple TableData objects
     * @param tableDataList List of TableData objects
     * @return HTML string with tabs
     */
    public String generateHtmlTabs(List<TableData> tableDataList) {
        StringBuilder html = new StringBuilder();
        
        // Tab navigation
        html.append("<ul class=\"nav nav-tabs\" id=\"myTab\" role=\"tablist\">\n");
        for (int i = 0; i < tableDataList.size(); i++) {
            TableData tableData = tableDataList.get(i);
            String tabId = "tab" + i;
            
            html.append("  <li class=\"nav-item\" role=\"presentation\">\n");
            html.append("    <button class=\"nav-link");
            if (i == 0) {
                html.append(" active");
            }
            html.append("\" id=\"").append(tabId).append("-tab\" data-bs-toggle=\"tab\" ");
            html.append("data-bs-target=\"#").append(tabId).append("\" type=\"button\" role=\"tab\" ");
            html.append("aria-controls=\"").append(tabId).append("\" ");
            if (i == 0) {
                html.append("aria-selected=\"true\"");
            } else {
                html.append("aria-selected=\"false\"");
            }
            html.append(">").append(tableData.getTableName()).append("</button>\n");
            html.append("  </li>\n");
        }
        html.append("</ul>\n");
        
        // Tab content
        html.append("<div class=\"tab-content\" id=\"myTabContent\">\n");
        for (int i = 0; i < tableDataList.size(); i++) {
            TableData tableData = tableDataList.get(i);
            String tabId = "tab" + i;
            
            html.append("  <div class=\"tab-pane fade");
            if (i == 0) {
                html.append(" show active");
            }
            html.append("\" id=\"").append(tabId).append("\" role=\"tabpanel\" ");
            html.append("aria-labelledby=\"").append(tabId).append("-tab\">\n");
            
            html.append(generateHtmlTable(tableData));
            
            html.append("  </div>\n");
        }
        html.append("</div>");
        
        return html.toString();
    }
} 