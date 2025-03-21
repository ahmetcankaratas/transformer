/**
 * @typedef {Object} ParsedData
 * @property {string[]} headers - Table column headers
 * @property {string[][]} rows - Table row data
 */

/**
 * @class FileModel
 * @description Handles file operations and parsing for PDF and Excel files
 * @public
 */
class FileModel {
  /**
   * @constructor
   * @public
   */
  constructor() {
    /** @private @type {pdfjsLib} - PDF.js library instance */
    this.pdfLib = window.pdfjsLib;
    /** @private @type {XLSX} - SheetJS library instance */
    this.XLSX = window.XLSX;
    this.fileContent = null;
    this.fileType = null;
  }

  /**
   * @method readFile
   * @description Reads and parses a file based on its type
   * @public
   * @async
   * @param {File} file - The file to read (PDF or Excel)
   * @returns {Promise<ParsedData>} Parsed data from the file
   * @throws {Error} When file type is not supported or parsing fails
   */
  async readFile(file) {
    const fileType = file.type || this._getFileTypeFromName(file.name);

    if (fileType.includes("pdf")) {
      return await this._readPdfFile(file);
    } else if (fileType.includes("sheet") || /\.xlsx?$/.test(file.name)) {
      return await this._readExcelFile(file);
    } else {
      throw new Error(
        "Unsupported file format. Please upload a PDF or Excel file."
      );
    }
  }

  /**
   * @method _readPdfFile
   * @description Reads and parses a PDF file
   * @private
   * @async
   * @param {File} file - The PDF file to read
   * @returns {Promise<ParsedData>} Parsed data from the PDF
   * @throws {Error} When PDF parsing fails
   */
  async _readPdfFile(file) {
    try {
      const arrayBuffer = await file.arrayBuffer();
      const pdf = await this.pdfLib.getDocument(arrayBuffer).promise;
      const page = await pdf.getPage(1);
      const textContent = await page.getTextContent();

      return this._processPdfContent(textContent);
    } catch (error) {
      throw new Error(`Failed to read PDF file: ${error.message}`);
    }
  }

  /**
   * @method _readExcelFile
   * @description Reads and parses an Excel file
   * @private
   * @async
   * @param {File} file - The Excel file to read
   * @returns {Promise<ParsedData>} Parsed data from the Excel file
   * @throws {Error} When Excel parsing fails
   */
  async _readExcelFile(file) {
    try {
      const arrayBuffer = await file.arrayBuffer();
      const workbook = this.XLSX.read(arrayBuffer);
      const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
      const jsonData = this.XLSX.utils.sheet_to_json(firstSheet, { header: 1 });

      return this._processExcelData(jsonData);
    } catch (error) {
      throw new Error(`Failed to read Excel file: ${error.message}`);
    }
  }

  /**
   * @method _processPdfContent
   * @description Processes raw PDF content into structured data
   * @private
   * @param {Object} textContent - Raw PDF text content
   * @param {Array<{str: string}>} textContent.items - Text items from PDF
   * @returns {ParsedData} Structured data from PDF content
   */
  _processPdfContent(textContent) {
    const items = textContent.items
      .map((item) => item.str.trim())
      .filter((str) => str.length > 0);

    // Find table headers and data
    const headers = [];
    const rows = [];
    let currentRow = [];

    items.forEach((item, index) => {
      if (index < 7) {
        // Assuming first 7 items are headers
        headers.push(item);
      } else {
        currentRow.push(item);
        if (currentRow.length === headers.length) {
          rows.push([...currentRow]);
          currentRow = [];
        }
      }
    });

    return { headers, rows };
  }

  /**
   * @method _processExcelData
   * @description Processes Excel data into structured format
   * @private
   * @param {Array<Array<string>>} jsonData - Raw Excel data
   * @returns {ParsedData} Structured data from Excel
   */
  _processExcelData(jsonData) {
    if (!jsonData || jsonData.length === 0) {
      throw new Error("Excel file is empty or invalid.");
    }

    const headers = jsonData[0].map((header) => header.toString().trim());
    const rows = jsonData
      .slice(1)
      .map((row) => row.map((cell) => (cell || "").toString().trim()));

    return { headers, rows };
  }

  /**
   * @method _getFileTypeFromName
   * @description Determines file type from file name extension
   * @private
   * @param {string} fileName - Name of the file
   * @returns {string} MIME type of the file
   */
  _getFileTypeFromName(fileName) {
    const extension = fileName.split(".").pop().toLowerCase();
    switch (extension) {
      case "pdf":
        return "application/pdf";
      case "xlsx":
      case "xls":
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
      default:
        return "application/octet-stream";
    }
  }

  /**
   * @method parseContent
   * @description Parses the loaded file content based on file type
   * @returns {Promise<Object>} - Returns parsed data in a structured format
   * @throws {Error} - Throws error if parsing fails
   */
  async parseContent() {
    if (!this.fileContent) {
      throw new Error("No file content loaded");
    }

    try {
      if (this.fileType.includes("pdf")) {
        return await this._parsePDF();
      } else {
        return await this._parseExcel();
      }
    } catch (error) {
      console.error("Error parsing content:", error);
      throw new Error("Failed to parse file content: " + error.message);
    }
  }

  /**
   * @private
   * @method _parsePDF
   * @description Internal method to parse PDF files
   * @returns {Promise<Object>} - Returns parsed PDF data
   */
  async _parsePDF() {
    try {
      const pdf = await this.pdfLib.getDocument({ data: this.fileContent })
        .promise;
      const numPages = pdf.numPages;
      const tables = [];

      // Process each page
      for (let pageNum = 1; pageNum <= numPages; pageNum++) {
        const page = await pdf.getPage(pageNum);
        const textContent = await page.getTextContent();

        // Extract text items and their positions
        const textItems = textContent.items.map((item) => ({
          text: item.str,
          x: item.transform[4],
          y: item.transform[5],
        }));

        // Group items by rows (similar y-coordinates)
        const rows = this._groupTextItemsByRows(textItems);

        // Add page data to tables
        if (rows.length > 0) {
          tables.push({
            pageNum,
            headers: rows[0],
            rows: rows.slice(1),
          });
        }
      }

      // Combine all tables
      return {
        headers: tables[0]?.headers || [],
        rows: tables.flatMap((table) => table.rows),
      };
    } catch (error) {
      console.error("Error parsing PDF:", error);
      throw new Error("Failed to parse PDF file: " + error.message);
    }
  }

  /**
   * @private
   * @method _parseExcel
   * @description Internal method to parse Excel files
   * @returns {Promise<Object>} - Returns parsed Excel data
   */
  async _parseExcel() {
    try {
      // Parse the workbook
      const workbook = this.XLSX.read(this.fileContent, { type: "binary" });

      // Get the first worksheet
      const firstSheetName = workbook.SheetNames[0];
      const worksheet = workbook.Sheets[firstSheetName];

      // Convert to JSON with header row
      const jsonData = this.XLSX.utils.sheet_to_json(worksheet, {
        header: 1,
        defval: "", // Default value for empty cells
        blankrows: true, // Skip empty rows
      });

      // Filter out completely empty rows
      const filteredData = jsonData.filter((row) =>
        row.some((cell) => cell !== "")
      );

      if (filteredData.length === 0) {
        throw new Error("No data found in Excel file");
      }

      return {
        headers: filteredData[0].map((header) => header.toString()),
        rows: filteredData.slice(1),
      };
    } catch (error) {
      console.error("Error parsing Excel:", error);
      throw new Error("Failed to parse Excel file: " + error.message);
    }
  }

  /**
   * @private
   * @method _groupTextItemsByRows
   * @description Groups PDF text items into rows based on their y-coordinates
   * @param {Array} items - Array of text items with positions
   * @returns {Array} - Array of rows
   */
  _groupTextItemsByRows(items) {
    const yThreshold = 5; // Threshold for considering items on the same row
    const rows = [];
    let currentRow = [];
    let currentY = null;

    // Sort items by y-coordinate (top to bottom) and then x-coordinate (left to right)
    const sortedItems = items.sort((a, b) => {
      if (Math.abs(a.y - b.y) <= yThreshold) {
        return a.x - b.x;
      }
      return b.y - a.y;
    });

    // Group items into rows
    for (const item of sortedItems) {
      if (currentY === null || Math.abs(item.y - currentY) <= yThreshold) {
        currentRow.push(item.text.trim());
        currentY = item.y;
      } else {
        if (currentRow.length > 0) {
          rows.push(currentRow);
        }
        currentRow = [item.text.trim()];
        currentY = item.y;
      }
    }

    // Add the last row
    if (currentRow.length > 0) {
      rows.push(currentRow);
    }

    // Filter out empty rows and clean up data
    return rows.filter((row) => row.some((cell) => cell.length > 0));
  }

  /**
   * @method getTableData
   * @description Returns the parsed data in a format suitable for table generation
   * @returns {Object} - Returns structured table data
   */
  getTableData() {
    if (!this.fileContent) {
      throw new Error("No data available");
    }
    return this.fileContent;
  }
}
