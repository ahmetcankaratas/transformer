# SEDS519 Homework 1 - PDF and Excel Converter

This project is a Java application that converts PDF and Excel files to HTML tables. It is developed using the MVC (Model-View-Controller) design pattern.

## Project Structure

- **Model**: Contains data structures (`TableData`)
- **View**: Contains user interface (`MainView`)
- **Controller**: Contains business logic (`FileController`, `HtmlController`)
- **Util**: Contains utility classes (`FileValidator`)

## Required Libraries

The following libraries need to be downloaded and placed in the `HW1/Java/lib` directory:

### For PDF Processing:

- pdfbox-2.0.27.jar
- pdfbox-tools-2.0.27.jar
- fontbox-2.0.27.jar
- commons-logging-1.2.jar

### For Excel Processing:

- poi-5.2.3.jar
- poi-ooxml-5.2.3.jar
- poi-ooxml-lite-5.2.3.jar
- xmlbeans-5.1.1.jar
- commons-compress-1.21.jar
- commons-collections4-4.4.jar
- commons-io-2.11.0.jar
- log4j-api-2.18.0.jar

### Easy Installation

For your convenience, we've included scripts to download all required libraries:

- **Linux/Mac**: Run `./download_libraries.sh` from the `HW1/Java` directory
- **Windows**: Run `download_libraries.bat` from the `HW1/Java` directory

Alternatively, you can download these libraries manually from:

- Apache PDFBox: https://pdfbox.apache.org/download.html
- Apache POI: https://poi.apache.org/download.html
- Apache Commons: https://commons.apache.org/proper/commons-logging/download_logging.cgi

## Running in Eclipse IDE

1. Open Eclipse IDE
2. File -> Import -> General -> Existing Projects into Workspace
3. Select the root directory of the project with "Select root directory" option
4. Import the project
5. Make sure all required libraries are in the `HW1/Java/lib` directory
6. Find Main.java and run it with Run As -> Java Application

## Usage

1. Click "Open PDF" button to select a PDF file
2. Click "Open Excel" button to select an Excel file
3. Click "Generate HTML" button to generate HTML code
4. Click "Save HTML" button to save the HTML code to a file

## UML Diagrams

### Class Diagram

```
+-------------------+     +-------------------+     +-------------------+
|       Main        |---->|     MainView      |---->| FileController    |
+-------------------+     +-------------------+     +-------------------+
                          |  - tabbedPane     |     |  - tableDataList  |
                          |  - statusPanel    |     +-------------------+
                          |  - statusLabel    |     | + processPdfFile()|
                          |  - htmlTextArea   |     | + processExcelFile|
                          |  - fileController |     | + getTableDataList|
                          |  - htmlController |     +-------------------+
                          +-------------------+            |
                          | + MainView()      |            |
                          | - createToolBar() |            v
                          | - openPdfFile()   |     +-------------------+
                          | - openExcelFile() |     |     TableData     |
                          | - generateHtml()  |     +-------------------+
                          | - saveHtml()      |     |  - tableName      |
                          +-------------------+     |  - headers        |
                                |                   |  - rows           |
                                |                   +-------------------+
                                |                   | + addHeader()     |
                                |                   | + addRow()        |
                                |                   | + getTableName()  |
                                |                   | + getHeaders()    |
                                |                   | + getRows()       |
                                v                   +-------------------+
                          +-------------------+            ^
                          |  HtmlController   |            |
                          +-------------------+            |
                          | + generateHtmlTable |----------+
                          | + generateHtmlTabs  |
                          +-------------------+
                                                    +-------------------+
                                                    |  FileValidator    |
                                                    +-------------------+
                                                    | + isValidPdfFile()|
                                                    | + isValidExcelFile|
                                                    +-------------------+
```

### MVC Pattern Diagram

```
+---------------------+      +-----------------------+      +--------------------+
|       MODEL         |      |        VIEW           |      |     CONTROLLER     |
+---------------------+      +-----------------------+      +--------------------+
|                     |      |                       |      |                    |
|     TableData       |<-----+      MainView         +----->| FileController    |
|                     |      |                       |      | HtmlController    |
+---------------------+      +-----------------------+      +--------------------+
| Data Model          |      | User Interface        |      | Business Logic     |
+---------------------+      +-----------------------+      +--------------------+
```

## Notes

- This project now processes actual PDF and Excel files using Apache PDFBox and Apache POI libraries
- For PDF files, the system attempts to extract tabular data from the text
- For Excel files, it reads the first sheet and converts it to an HTML table
- Please ensure you have appropriate PDF and Excel files for testing. Tables in PDF files might not always be detected perfectly, as PDF extraction depends on the structure of the document
