# Course Schedule Viewer

A web-based application that transforms PDF and Excel course schedules into HTML tables. Built using the Model-View-Controller (MVC) design pattern.

## Features

- PDF and Excel file support
- Real-time file processing
- Responsive table display
- File information display
- Error handling and validation
- Bootstrap-based UI

## Project Structure

```
├── index.html
└── src/
    ├── models/
    │   ├── FileModel.js    # Handles file operations and parsing
    │   └── TableModel.js   # Manages table data structure
    ├── views/
    │   └── ScheduleView.js # Handles UI display and interactions
    ├── controllers/
    │   └── AppController.js # Main application controller
    └── styles/
        └── main.css        # Custom styles
```

## UML Class Diagrams

### Class Relationships

```mermaid
classDiagram
    AppController --> FileModel : uses
    AppController --> ScheduleView : uses
    FileModel --> TableData : creates
    ScheduleView --> TableData : displays

    class AppController {
        -FileModel fileModel
        -ScheduleView scheduleView
        -HTMLInputElement fileInput
        -HTMLButtonElement uploadButton
        +initialize()
        -_setupEventListeners()
        -_handleFileSelect()
        -_handleFileUpload()
    }

    class FileModel {
        -pdfjsLib pdfLib
        -XLSX XLSX
        +readFile(file)
        -_readPdfFile(file)
        -_readExcelFile(file)
        -_processPdfContent(textContent)
        -_processExcelData(jsonData)
        -_getFileTypeFromName(fileName)
    }

    class ScheduleView {
        -HTMLElement scheduleContent
        -HTMLInputElement fileInput
        -HTMLElement uploadFeedback
        -HTMLElement fileInfo
        +initialize()
        +displaySchedule(data, file)
        +showFileInfo(file)
        +hideFileInfo()
        +showError(message)
        +showLoading()
        -_createTableHtml(data)
        -_formatFileSize(bytes)
        -_getFileTypeLabel(fileType)
        -_escapeHtml(unsafe)
    }

    class TableData {
        +string[] headers
        +string[][] rows
    }
```

### Detailed Class Specifications

#### AppController

```mermaid
classDiagram
    class AppController {
        -FileModel fileModel
        -ScheduleView scheduleView
        -HTMLInputElement fileInput
        -HTMLButtonElement uploadButton
        +constructor()
        +initialize() void
        -_setupEventListeners() void
        -_handleFileSelect() void
        -_handleFileUpload() Promise~void~
    }
```

#### FileModel

```mermaid
classDiagram
    class FileModel {
        -pdfjsLib pdfLib
        -XLSX XLSX
        +constructor()
        +readFile(File) Promise~TableData~
        -_readPdfFile(File) Promise~TableData~
        -_readExcelFile(File) Promise~TableData~
        -_processPdfContent(Object) TableData
        -_processExcelData(Array) TableData
        -_getFileTypeFromName(string) string
    }
```

#### ScheduleView

```mermaid
classDiagram
    class ScheduleView {
        -HTMLElement scheduleContent
        -HTMLInputElement fileInput
        -HTMLElement uploadFeedback
        -HTMLElement fileInfo
        -HTMLElement fileName
        -HTMLElement fileDetails
        -HTMLButtonElement closeButton
        +constructor()
        +initialize() void
        +displaySchedule(TableData, File) void
        +clearFeedback() void
        +showFileInfo(File) void
        +hideFileInfo() void
        +showError(string) void
        +showLoading() void
        -_createTableHtml(TableData) string
        -_formatFileSize(number) string
        -_getFileTypeLabel(string) string
        -_escapeHtml(string) string
    }
```

## Dependencies

- [PDF.js](https://mozilla.github.io/pdf.js/) - PDF file parsing
- [SheetJS](https://sheetjs.com/) - Excel file parsing
- [Bootstrap 5.3.3](https://getbootstrap.com/) - UI framework
- [Bootstrap Icons](https://icons.getbootstrap.com/) - Icons

## Setup

1. Clone the repository
2. Open `index.html` in a web browser
3. Upload a PDF or Excel file containing a course schedule

## Usage

1. Click the "Choose File" button or drag and drop a file
2. Select a PDF or Excel file containing a course schedule
3. Click "Upload Schedule" to process the file
4. The schedule will be displayed as an HTML table
5. File information will be shown above the table

## Error Handling

The application handles various error scenarios:

- Invalid file types
- Empty or corrupted files
- PDF parsing errors
- Excel parsing errors
- Network issues

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Notes

- The application uses client-side processing only
- No server-side components required
- All processing is done in the browser
- Supports both PDF and Excel file formats
