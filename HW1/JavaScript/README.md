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

## MVC Architecture

The application follows the Model-View-Controller (MVC) design pattern with clear separation of concerns:

- **Model**: Handles data processing and business logic
- **View**: Manages the presentation layer and user interface
- **Controller**: Coordinates between Model and View

## UML Class Diagrams

### Class Relationships

```mermaid
classDiagram
    Controller --> Model : uses
    Controller --> View : uses
    Model --> Controller : provides data
    View --> Controller : provides user interface

    class Controller {
        -Model #model
        -View #view
        -HTMLInputElement #fileInput
        -HTMLButtonElement #uploadButton
        +initialize()
        -#setupEventListeners()
        -#handleFileSelect()
        -#handleFileUpload()
    }

    class Model {
        -pdfjsLib #pdfLib
        -XLSX #XLSX
        -#headers
        -#rows
        +readFile(file)
        +get data()
        +set data(value)
        +clear()
        -#readPdfFile(file)
        -#readExcelFile(file)
        -#processPdfContent(textContent)
        -#processExcelData(jsonData)
        -#validateData(data)
        -#getFileTypeFromName(fileName)
    }

    class View {
        -Model #model
        -HTMLElement #scheduleContent
        -HTMLInputElement #fileInput
        -HTMLElement #uploadFeedback
        -HTMLElement #fileInfo
        +get model()
        +set model(value)
        +initialize()
        +displaySchedule(file)
        +renderTable()
        -#generateTableHTML(data)
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
        -Model #model
        -View #view
        -HTMLInputElement #fileInput
        -HTMLButtonElement #uploadButton
        +constructor()
        +initialize() void
        -#setupEventListeners() void
        -#handleFileSelect() void
        -#handleFileUpload() Promise~void~
    }
```

#### Model

```mermaid
classDiagram
    class Model {
        -pdfjsLib #pdfLib
        -XLSX #XLSX
        -#headers
        -#rows
        +constructor()
        +readFile(File) Promise~void~
        +get data() Object
        +set data(Object) void
        +clear() void
        -#readPdfFile(File) Promise~void~
        -#readExcelFile(File) Promise~void~
        -#processPdfContent(Object) void
        -#processExcelData(Array) void
        -#validateData(Object) boolean
        -#getFileTypeFromName(string) string
    }
```

#### View

```mermaid
classDiagram
    class View {
        -Model #model
        -HTMLElement #scheduleContent
        -HTMLInputElement #fileInput
        -HTMLElement #uploadFeedback
        -HTMLElement #fileInfo
        -HTMLElement #fileName
        -HTMLElement #fileDetails
        -HTMLButtonElement #closeButton
        +constructor()
        +get model() Model
        +set model(Model) void
        +initialize() void
        +displaySchedule(File) void
        +renderTable() string
        -#generateTableHTML(Object) string
        +clearFeedback() void
        +showFileInfo(File) void
        +hideFileInfo() void
        +showError(string) void
        +showLoading() void
        -#escapeHtml(string) string
    }
```

## Dependencies

- [PDF.js](https://mozilla.github.io/pdf.js/) - PDF file parsing
- [SheetJS](https://sheetjs.com/) - Excel file parsing
- [Bootstrap 5.3.3](https://getbootstrap.com/) - UI framework
- [Bootstrap Icons](https://icons.getbootstrap.com/) - Icons

## JavaScript Implementation

The application uses modern JavaScript features including:

- ES6 Classes
- Private class fields (using the `#` prefix)
- Async/await for asynchronous operations

For more information on JavaScript classes and private fields, see the [Mozilla Developer Network documentation](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes).

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


## Notes

- The application uses client-side processing only
- No server-side components required
- All processing is done in the browser
- Supports both PDF and Excel file formats
