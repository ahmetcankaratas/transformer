package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for holding table data
 */
public class TableData {
    private String tableName;
    private List<String> headers;
    private List<List<String>> rows;
    
    public TableData(String tableName) {
        this.tableName = tableName;
        headers = new ArrayList<>();
        rows = new ArrayList<>();
    }
    
    public void addHeader(String header) {
        headers.add(header);
    }
    
    public void addRow(List<String> row) {
        rows.add(row);
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public List<String> getHeaders() {
        return headers;
    }
    
    public List<List<String>> getRows() {
        return rows;
    }
    
    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
    
    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }
} 