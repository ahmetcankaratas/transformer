package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.TableData;
import util.FileValidator;

// PDF processing imports
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import java.awt.Rectangle;
import org.apache.pdfbox.text.TextPosition;

// Excel processing imports
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Controller class for file operations
 */
public class FileController {
    
    private List<TableData> tableDataList;
    
    public FileController() {
        tableDataList = new ArrayList<>();
    }
    
    /**
     * Process PDF file and extract tables
     * @param file PDF file
     * @return List of extracted table data
     */
    public List<TableData> processPdfFile(File file) {
        // Validate file before processing
        if (!FileValidator.isValidPdfFile(file)) {
            System.err.println("Invalid PDF file: " + file.getName());
            return tableDataList;
        }
        
        try (PDDocument document = PDDocument.load(file)) {
            // Get document information
            int pageCount = document.getNumberOfPages();
            System.out.println("PDF document loaded. Number of pages: " + pageCount);
            
            // Create table data for each page that might contain a table
            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                String pageName = "Page " + (pageIndex + 1);
                
                // Try to extract text using the improved region-based method
                TableData tableFromRegions = extractTableByRegions(document, pageIndex, pageName);
                if (tableFromRegions != null && !tableFromRegions.getRows().isEmpty()) {
                    tableDataList.add(tableFromRegions);
                    continue; // Skip other methods if region-based extraction was successful
                }
                
                // If region-based extraction failed, fall back to text-based extraction
                PDFTextStripper textStripper = new PDFTextStripper();
                textStripper.setStartPage(pageIndex + 1);
                textStripper.setEndPage(pageIndex + 1);
                textStripper.setSortByPosition(true);
                String pageText = textStripper.getText(document);
                
                // Print extracted text to console
                System.out.println("------- Extracted Text from " + pageName + " -------");
                System.out.println(pageText);
                System.out.println("-------------------------------------------");
                
                // Try to process the page as a table using text-based analysis
                TableData pageTable = extractTableFromPage(pageText, pageName);
                
                // Only add if the table has content
                if (pageTable != null && !pageTable.getRows().isEmpty()) {
                    tableDataList.add(pageTable);
                }
            }
            
            // If no tables were found, create a simple text representation
            if (tableDataList.isEmpty()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                
                TableData textTable = new TableData("Text content from " + file.getName());
                textTable.addHeader("Content");
                
                // Split text by lines and add as rows
                String[] lines = text.split("\\r?\\n");
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        List<String> row = new ArrayList<>();
                        row.add(line.trim());
                        textTable.addRow(row);
                    }
                }
                
                tableDataList.add(textTable);
            }
            
        } catch (IOException e) {
            System.err.println("Error processing PDF file: " + e.getMessage());
            e.printStackTrace();
            
            // If there's an error, create a simple table with error information
            TableData errorTable = new TableData("Error - " + file.getName());
            errorTable.addHeader("Error Message");
            List<String> errorRow = new ArrayList<>();
            errorRow.add("Error processing PDF file: " + e.getMessage());
            errorTable.addRow(errorRow);
            tableDataList.add(errorTable);
        }
        
        return tableDataList;
    }
    
    /**
     * Extract table using a region-based approach for better structure preservation
     * @param document PDF document
     * @param pageIndex Index of the page to process
     * @param pageName Name of the page for table naming
     * @return TableData object with extracted data
     */
    private TableData extractTableByRegions(PDDocument document, int pageIndex, String pageName) throws IOException {
        TableData tableData = new TableData("Table from " + pageName);
        
        // Initialize text stripper for region extraction
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        
        // Get page dimensions
        org.apache.pdfbox.pdmodel.PDPage page = document.getPage(pageIndex);
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        
        // Step 1: Analyze the page to detect potential table structure
        List<Float> horizontalLines = detectHorizontalLines(document, pageIndex);
        List<Float> verticalLines = detectVerticalLines(document, pageIndex);
        
        // If we couldn't detect proper lines, return null to fall back to text-based method
        if (horizontalLines.size() < 2 || verticalLines.size() < 2) {
            return null;
        }
        
        // Step 2: Create a grid of regions based on the detected lines
        List<List<String>> grid = extractTextFromGrid(document, pageIndex, horizontalLines, verticalLines);
        
        // Step 3: Process grid to create table data
        if (!grid.isEmpty()) {
            // Use first row as headers
            List<String> headerRow = grid.get(0);
            for (String header : headerRow) {
                tableData.addHeader(header.trim().isEmpty() ? "Column" : header.trim());
            }
            
            // Add data rows
            for (int i = 1; i < grid.size(); i++) {
                tableData.addRow(grid.get(i));
            }
        }
        
        return tableData;
    }
    
    /**
     * Detect horizontal lines that might represent row boundaries
     * @param document PDF document
     * @param pageIndex Index of the page to analyze
     * @return List of y-coordinates of potential row boundaries
     */
    private List<Float> detectHorizontalLines(PDDocument document, int pageIndex) throws IOException {
        List<Float> horizontalPositions = new ArrayList<>();
        
        // Get page dimensions
        org.apache.pdfbox.pdmodel.PDPage page = document.getPage(pageIndex);
        float pageHeight = page.getMediaBox().getHeight();
        
        // Extract text with position information to identify text lines
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                for (TextPosition position : textPositions) {
                    float y = pageHeight - position.getY();
                    // Add to positions if not already close to an existing position
                    boolean isNew = true;
                    for (Float existingY : horizontalPositions) {
                        if (Math.abs(existingY - y) < 5) {
                            isNew = false;
                            break;
                        }
                    }
                    if (isNew) {
                        horizontalPositions.add(y);
                    }
                }
            }
        };
        
        stripper.setStartPage(pageIndex + 1);
        stripper.setEndPage(pageIndex + 1);
        stripper.setSortByPosition(true);
        stripper.getText(document);
        
        // Sort positions to get them in order from top to bottom
        horizontalPositions.sort((a, b) -> Float.compare(a, b));
        
        // Filter positions to get more distinct rows (remove positions that are too close)
        List<Float> filteredPositions = new ArrayList<>();
        if (!horizontalPositions.isEmpty()) {
            filteredPositions.add(0f); // Add top of page
            filteredPositions.add(horizontalPositions.get(0));
            
            for (int i = 1; i < horizontalPositions.size(); i++) {
                float current = horizontalPositions.get(i);
                float previous = horizontalPositions.get(i - 1);
                
                if (current - previous > 10) { // Minimum gap between rows
                    filteredPositions.add(current);
                }
            }
            
            filteredPositions.add(pageHeight); // Add bottom of page
        }
        
        return filteredPositions;
    }
    
    /**
     * Detect vertical lines that might represent column boundaries
     * @param document PDF document
     * @param pageIndex Index of the page to analyze
     * @return List of x-coordinates of potential column boundaries
     */
    private List<Float> detectVerticalLines(PDDocument document, int pageIndex) throws IOException {
        List<Float> verticalPositions = new ArrayList<>();
        
        // Extract text with position information to identify columns
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                for (TextPosition position : textPositions) {
                    float x = position.getX();
                    // Add position at start of text segment
                    boolean startIsNew = true;
                    for (Float existingX : verticalPositions) {
                        if (Math.abs(existingX - x) < 10) {
                            startIsNew = false;
                            break;
                        }
                    }
                    if (startIsNew) {
                        verticalPositions.add(x);
                    }
                    
                    // Add position at end of text segment
                    float endX = x + position.getWidth();
                    boolean endIsNew = true;
                    for (Float existingX : verticalPositions) {
                        if (Math.abs(existingX - endX) < 10) {
                            endIsNew = false;
                            break;
                        }
                    }
                    if (endIsNew) {
                        verticalPositions.add(endX);
                    }
                }
            }
        };
        
        stripper.setStartPage(pageIndex + 1);
        stripper.setEndPage(pageIndex + 1);
        stripper.setSortByPosition(true);
        stripper.getText(document);
        
        // Sort positions to get them in order from left to right
        verticalPositions.sort((a, b) -> Float.compare(a, b));
        
        // Filter positions to get more distinct columns
        List<Float> filteredPositions = new ArrayList<>();
        if (!verticalPositions.isEmpty()) {
            filteredPositions.add(0f); // Add left edge of page
            
            for (int i = 0; i < verticalPositions.size(); i++) {
                float current = verticalPositions.get(i);
                
                // Check if this position is significantly different from the last one added
                if (filteredPositions.isEmpty() || 
                    current - filteredPositions.get(filteredPositions.size() - 1) > 20) {
                    filteredPositions.add(current);
                }
            }
            
            // Add right edge of page
            org.apache.pdfbox.pdmodel.PDPage page = document.getPage(pageIndex);
            filteredPositions.add(page.getMediaBox().getWidth());
        }
        
        return filteredPositions;
    }
    
    /**
     * Extract text from grid cells created by intersection of horizontal and vertical lines
     * @param document PDF document
     * @param pageIndex Index of the page to process
     * @param horizontalLines Y-coordinates of horizontal lines
     * @param verticalLines X-coordinates of vertical lines
     * @return 2D grid of extracted text
     */
    private List<List<String>> extractTextFromGrid(PDDocument document, int pageIndex, 
                                                 List<Float> horizontalLines, 
                                                 List<Float> verticalLines) throws IOException {
        List<List<String>> grid = new ArrayList<>();
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        
        org.apache.pdfbox.pdmodel.PDPage page = document.getPage(pageIndex);
        float pageHeight = page.getMediaBox().getHeight();
        
        // Process each row
        for (int row = 0; row < horizontalLines.size() - 1; row++) {
            float yTop = pageHeight - horizontalLines.get(row + 1);
            float yBottom = pageHeight - horizontalLines.get(row);
            float height = Math.abs(yBottom - yTop);
            
            List<String> rowData = new ArrayList<>();
            
            // Process each column in the current row
            for (int col = 0; col < verticalLines.size() - 1; col++) {
                float xLeft = verticalLines.get(col);
                float width = verticalLines.get(col + 1) - xLeft;
                
                // Region name unique to this cell
                String regionName = "cell_" + row + "_" + col;
                
                // Define rectangle for this cell
                Rectangle rect = new Rectangle((int)xLeft, (int)yTop, (int)width, (int)height);
                stripper.addRegion(regionName, rect);
                
                // Extract text from this cell
                stripper.extractRegions(page);
                String cellText = stripper.getTextForRegion(regionName);
                
                rowData.add(cellText.trim());
                
                // Clear the region for the next cell
                stripper.removeRegion(regionName);
            }
            
            // Only add non-empty rows
            boolean hasContent = false;
            for (String cell : rowData) {
                if (!cell.isEmpty()) {
                    hasContent = true;
                    break;
                }
            }
            
            if (hasContent) {
                grid.add(rowData);
            }
        }
        
        return grid;
    }
    
    /**
     * Extract table data from a PDF page text
     * @param pageText Text extracted from a PDF page
     * @param pageName Name of the page (for table naming)
     * @return TableData object with extracted data or null if no table found
     */
    private TableData extractTableFromPage(String pageText, String pageName) {
        // Create a new table data object
        TableData tableData = new TableData("Table from " + pageName);
        
        // Split the text into lines
        String[] lines = pageText.split("\\r?\\n");
        
        // Variables to track table detection
        boolean inTable = false;
        boolean headersAdded = false;
        int consistentColumnCount = -1;
        List<String[]> potentialRows = new ArrayList<>();
        
        // Analyze lines to find potential tables
        for (String line : lines) {
            // Skip empty lines
            if (line.trim().isEmpty()) {
                continue;
            }
            
            // Look for lines with consistent delimiters that might indicate table cells
            String[] columns = null;
            
            // Try tab delimiter
            if (line.contains("\t")) {
                columns = line.split("\\t");
            } 
            // Try multiple spaces (common in PDFs)
            else if (line.contains("  ")) {
                columns = line.split("\\s{2,}");
            }
            // Try pipe delimiter
            else if (line.contains("|")) {
                columns = line.split("\\|");
                // Clean up the split result
                for (int i = 0; i < columns.length; i++) {
                    columns[i] = columns[i].trim();
                }
            }
            
            // If potential columns were found
            if (columns != null && columns.length > 1) {
                // Clean empty entries
                List<String> cleanColumns = new ArrayList<>();
                for (String col : columns) {
                    if (!col.trim().isEmpty()) {
                        cleanColumns.add(col.trim());
                    }
                }
                
                // Skip if not enough columns after cleaning
                if (cleanColumns.size() < 2) {
                    continue;
                }
                
                columns = cleanColumns.toArray(new String[0]);
                
                // If this is the first row with columns, set it as the expected column count
                if (consistentColumnCount == -1) {
                    consistentColumnCount = columns.length;
                    potentialRows.add(columns);
                    inTable = true;
                }
                // If column count matches previous rows, it's likely part of the same table
                else if (Math.abs(columns.length - consistentColumnCount) <= 1) {  // Allow for small variations
                    potentialRows.add(columns);
                }
                // If column count is very different, we might have ended the table
                else if (Math.abs(columns.length - consistentColumnCount) > 2) {
                    // If we already have some rows, assume we're done with this table
                    if (potentialRows.size() > 1) {
                        break;
                    }
                    // Otherwise, this might be a better start for a table
                    else {
                        consistentColumnCount = columns.length;
                        potentialRows.clear();
                        potentialRows.add(columns);
                    }
                }
            }
        }
        
        // Process potential table rows
        if (potentialRows.size() >= 2) {  // Need at least header row and one data row
            // First row is headers
            String[] headers = potentialRows.get(0);
            for (String header : headers) {
                tableData.addHeader(header);
            }
            
            // Remaining rows are data
            for (int i = 1; i < potentialRows.size(); i++) {
                String[] rowData = potentialRows.get(i);
                List<String> row = new ArrayList<>();
                
                // Add each cell to row
                for (int j = 0; j < tableData.getHeaders().size(); j++) {
                    if (j < rowData.length) {
                        row.add(rowData[j]);
                    } else {
                        row.add(""); // Pad with empty cells if needed
                    }
                }
                
                tableData.addRow(row);
            }
            
            return tableData;
        }
        
        // If we couldn't detect a table structure, but the text might contain table-like data
        // Attempt to detect tables using regex patterns
        return attemptRegexTableDetection(pageText, pageName);
    }
    
    /**
     * Attempt to detect tables using regex patterns
     * @param text Text to analyze
     * @param pageName Name of the page
     * @return TableData if a table is detected, null otherwise
     */
    private TableData attemptRegexTableDetection(String text, String pageName) {
        // Look for patterns common in course schedules
        Pattern timePattern = Pattern.compile("(\\d{1,2}:\\d{2})\\s*-\\s*(\\d{1,2}:\\d{2})");
        Matcher timeMatcher = timePattern.matcher(text);
        
        if (timeMatcher.find()) {
            // Time slots found, might be a schedule
            TableData scheduleTable = new TableData("Schedule from " + pageName);
            
            // Add common headers for a course schedule
            scheduleTable.addHeader("Time");
            scheduleTable.addHeader("Monday");
            scheduleTable.addHeader("Tuesday");
            scheduleTable.addHeader("Wednesday");
            scheduleTable.addHeader("Thursday");
            scheduleTable.addHeader("Friday");
            
            // Extract lines with time patterns
            String[] lines = text.split("\\r?\\n");
            for (String line : lines) {
                Matcher lineMatcher = timePattern.matcher(line);
                if (lineMatcher.find()) {
                    // This line contains a time slot
                    String timeSlot = lineMatcher.group(0);  // The full time range
                    
                    // Try to extract course information
                    List<String> row = new ArrayList<>();
                    row.add(timeSlot);
                    
                    // Add empty placeholders for days
                    for (int i = 0; i < 5; i++) {
                        row.add("");
                    }
                    
                    scheduleTable.addRow(row);
                }
            }
            
            if (!scheduleTable.getRows().isEmpty()) {
                return scheduleTable;
            }
        }
        
        return null;
    }
    
    /**
     * Process Excel file and extract tables
     * @param file Excel file
     * @return List of extracted table data
     */
    public List<TableData> processExcelFile(File file) {
        // Validate file before processing
        if (!FileValidator.isValidExcelFile(file)) {
            System.err.println("Invalid Excel file: " + file.getName());
            return tableDataList;
        }
        
        // Use try-with-resources to ensure proper closing of resources
        try (FileInputStream inputStream = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            
            // Get the number of sheets
            int numberOfSheets = workbook.getNumberOfSheets();
            System.out.println("Excel workbook loaded. Number of sheets: " + numberOfSheets);
            
            // Process each sheet in the workbook
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                
                // Get sheet name or use default if blank
                String sheetName = workbook.getSheetName(i);
                if (sheetName == null || sheetName.trim().isEmpty()) {
                    sheetName = "Sheet " + (i + 1);
                }
                
                // Process the sheet if it has content
                if (sheet.getPhysicalNumberOfRows() > 0) {
                    TableData sheetData = processExcelSheet(sheet, sheetName, file.getName());
                    if (sheetData != null && !sheetData.getRows().isEmpty()) {
                        tableDataList.add(sheetData);
                    }
                }
            }
            
            // If no tables were found, create a simple info table
            if (tableDataList.isEmpty()) {
                TableData infoTable = new TableData("Info - " + file.getName());
                infoTable.addHeader("Message");
                List<String> infoRow = new ArrayList<>();
                infoRow.add("Excel file contains no data or could not be processed.");
                infoTable.addRow(infoRow);
                tableDataList.add(infoTable);
            }
            
        } catch (IOException e) {
            System.err.println("Error processing Excel file: " + e.getMessage());
            e.printStackTrace();
            
            // If there's an error, create a simple table with error information
            TableData errorTable = new TableData("Error - " + file.getName());
            errorTable.addHeader("Error");
            List<String> errorRow = new ArrayList<>();
            errorRow.add("Error processing Excel file: " + e.getMessage());
            errorTable.addRow(errorRow);
            tableDataList.add(errorTable);
        }
        
        return tableDataList;
    }
    
    /**
     * Process an Excel sheet to extract table data
     * @param sheet Excel sheet to process
     * @param sheetName Name of the sheet
     * @param fileName Name of the parent file
     * @return TableData object with extracted data
     */
    private TableData processExcelSheet(Sheet sheet, String sheetName, String fileName) {
        TableData tableData = new TableData("Excel - " + sheetName + " (" + fileName + ")");
        
        // Get iterator for all rows
        Iterator<Row> rowIterator = sheet.iterator();
        
        // Check if sheet has rows
        if (!rowIterator.hasNext()) {
            return tableData; // Return empty table if no rows
        }
        
        // Process header row (first row)
        Row headerRow = rowIterator.next();
        
        // Initialize with empty header if the first row is empty
        boolean hasHeaders = false;
        for (Cell cell : headerRow) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                hasHeaders = true;
                break;
            }
        }
        
        // Process header cells
        short minColIndex = headerRow.getFirstCellNum();
        short maxColIndex = headerRow.getLastCellNum();
        
        if (minColIndex < 0) {
            // No cells in header row, create default headers
            tableData.addHeader("Column 1");
            maxColIndex = 1; // Assume at least one column
        } else {
            // Process actual header cells
            for (int i = minColIndex; i < maxColIndex; i++) {
                Cell cell = headerRow.getCell(i);
                String headerValue = (cell != null) ? getCellValueAsString(cell) : "Column " + (i + 1);
                if (headerValue.trim().isEmpty()) {
                    headerValue = "Column " + (i + 1);
                }
                tableData.addHeader(headerValue);
            }
        }
        
        // If not treating first row as header, use it as data too
        if (!hasHeaders && headerRow.getPhysicalNumberOfCells() > 0) {
            addRowToTable(tableData, headerRow, minColIndex, maxColIndex);
        }
        
        // Process data rows
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getPhysicalNumberOfCells() > 0) {
                addRowToTable(tableData, row, minColIndex, maxColIndex);
            }
        }
        
        return tableData;
    }
    
    /**
     * Add a row of data from an Excel row to a TableData object
     * @param tableData The TableData to add to
     * @param row The Excel row
     * @param minColIndex Minimum column index
     * @param maxColIndex Maximum column index
     */
    private void addRowToTable(TableData tableData, Row row, short minColIndex, short maxColIndex) {
        List<String> rowData = new ArrayList<>();
        
        // Process each cell in the row up to the maximum column index from header
        for (int i = minColIndex; i < maxColIndex; i++) {
            Cell cell = row.getCell(i);
            String cellValue = (cell != null) ? getCellValueAsString(cell) : "";
            rowData.add(cellValue);
        }
        
        // Only add row if it has some non-empty cells
        boolean hasData = false;
        for (String value : rowData) {
            if (!value.trim().isEmpty()) {
                hasData = true;
                break;
            }
        }
        
        if (hasData) {
            tableData.addRow(rowData);
        }
    }
    
    /**
     * Convert a cell value to string, handling various cell types properly
     * @param cell Cell to convert
     * @return String representation of the cell value
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Handle date cells
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    return dateFormat.format(cell.getDateCellValue());
                }
                // Handle numeric cells - use formatted string if available
                double value = cell.getNumericCellValue();
                if (value == Math.floor(value)) {
                    // It's an integer value, remove decimal part
                    return String.valueOf((int) value);
                }
                return String.valueOf(value);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Try to evaluate formula
                try {
                    // First try to get cached formula result
                    CellType cachedType = cell.getCachedFormulaResultType();
                    if (cachedType == CellType.NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            return dateFormat.format(cell.getDateCellValue());
                        }
                        double numericValue = cell.getNumericCellValue();
                        if (numericValue == Math.floor(numericValue)) {
                            return String.valueOf((int) numericValue);
                        }
                        return String.valueOf(numericValue);
                    } else if (cachedType == CellType.STRING) {
                        return cell.getStringCellValue();
                    } else if (cachedType == CellType.BOOLEAN) {
                        return String.valueOf(cell.getBooleanCellValue());
                    }
                    // Fallback to formula string
                    return cell.getCellFormula();
                } catch (Exception e) {
                    return cell.getCellFormula();
                }
            case ERROR:
                return "ERROR";
            case BLANK:
            default:
                return "";
        }
    }
    
    public List<TableData> getTableDataList() {
        return tableDataList;
    }
} 