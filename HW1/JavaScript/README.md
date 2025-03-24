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
├── app.html              # Main HTML file
├── app.css               # Custom styles
└── app/                  # Application code directory
    ├── app.model.js      # Data handling and file processing
    ├── app.view.js       # UI display and interactions
    ├── app.controller.js # Main application controller
```

## UML Class Diagrams

### Class Relationships

```mermaid
classDiagram
    Controller --> Model : uses
    Controller --> View : uses
    Model --> Controller : provides data
    View --> Controller : provides user interface

    class Controller {
        -Model model
        -View view
        -HTMLInputElement fileInput
        -HTMLButtonElement uploadButton
        +initialize()
        -_setupEventListeners()
        -_handleFileSelect()
        -_handleFileUpload()
    }

    class Model {
        -pdfjsLib pdfLib
        -XLSX XLSX
        -headers
        -rows
        +readFile(file)
        +setData(headers, rows)
        +getData()
        +clearData()
        +toHtml()
        -_readPdfFile(file)
        -_readExcelFile(file)
        -_processPdfContent(textContent)
        -_processExcelData(jsonData)
        -_getFileTypeFromName(fileName)
    }

    class View {
        -HTMLElement scheduleContent
        -HTMLInputElement fileInput
        -HTMLElement uploadFeedback
        -HTMLElement fileInfo
        +initialize()
        +displaySchedule(htmlContent)
        +showFileInfo(file)
        +hideFileInfo()
        +showError(message)
        +showLoading()
        +clearFeedback()
    }
```

### Detailed Class Specifications

#### Controller

```mermaid
classDiagram
    class Controller {
        -Model model
        -View view
        -HTMLInputElement fileInput
        -HTMLButtonElement uploadButton
        +constructor()
        +initialize() void
        -_setupEventListeners() void
        -_handleFileSelect() void
        -_handleFileUpload() Promise~void~
    }
```

#### Model

```mermaid
classDiagram
    class Model {
        -pdfjsLib pdfLib
        -XLSX XLSX
        -headers
        -rows
        +constructor()
        +readFile(File) Promise~void~
        +setData(Array, Array) void
        +getData() Object
        +clearData() void
        +toHtml() string
        -_readPdfFile(File) Promise~void~
        -_readExcelFile(File) Promise~void~
        -_processPdfContent(Object) void
        -_processExcelData(Array) void
        -_getFileTypeFromName(string) string
    }
```

#### View

```mermaid
classDiagram
    class View {
        -HTMLElement scheduleContent
        -HTMLInputElement fileInput
        -HTMLElement uploadFeedback
        -HTMLElement fileInfo
        -HTMLElement fileName
        -HTMLElement fileDetails
        -HTMLButtonElement closeButton
        +constructor()
        +initialize() void
        +displaySchedule(string, File) void
        +clearFeedback() void
        +showFileInfo(File) void
        +hideFileInfo() void
        +showError(string) void
        +showLoading() void
    }
```

## Dependencies

- [PDF.js](https://mozilla.github.io/pdf.js/) - PDF file parsing
- [SheetJS](https://sheetjs.com/) - Excel file parsing
- [Bootstrap 5.3.3](https://getbootstrap.com/) - UI framework
- [Bootstrap Icons](https://icons.getbootstrap.com/) - Icons

## Setup

1. Clone the repository
2. Open `app.html` in a web browser
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
