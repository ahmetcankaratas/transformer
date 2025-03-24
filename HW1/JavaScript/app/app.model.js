class Model {
  constructor() {
    /** @private @type {pdfjsLib} */
    this.pdfLib = window.pdfjsLib;
    /** @private @type {XLSX} */
    this.XLSX = window.XLSX;

    /** @private @type {string[]} */
    this._headers = [];
    /** @private @type {string[][]} */
    this._rows = [];
  }

  /**
   * @public
   * @async
   * @param {File} file
   * @returns {Promise<ParsedData>}
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
   * @private
   * @async
   * @param {File} file
   * @returns {Promise<ParsedData>}
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
   * @private
   * @async
   * @param {File} file
   * @returns {Promise<ParsedData>}
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
   * @private
   * @param {Object} textContent
   * @param {Array<{str: string}>} textContent.items
   * @returns {ParsedData}
   */
  _processPdfContent(textContent) {
    const items = textContent.items
      .map((item) => item.str.trim())
      .filter((str) => str.length > 0);

    // Group text items into rows based on their position
    const rows = this._groupTextItemsByRows(items);

    // Determine headers (first row) and data rows
    const headers = rows.length > 0 ? rows[0] : [];
    const dataRows = rows.length > 1 ? rows.slice(1) : [];

    return { headers, rows: dataRows };
  }

  /**
   * @private
   * @param {Array<Array<string>>} jsonData
   * @returns {ParsedData}
   */
  _processExcelData(jsonData) {
    if (!jsonData || jsonData.length === 0) {
      throw new Error("Excel file is empty or invalid.");
    }

    // Find the first non-empty row to use as headers
    let headerRowIndex = 0;
    while (headerRowIndex < jsonData.length) {
      if (
        jsonData[headerRowIndex] &&
        jsonData[headerRowIndex].some((cell) => cell)
      ) {
        break;
      }
      headerRowIndex++;
    }

    if (headerRowIndex >= jsonData.length) {
      throw new Error("Could not find valid headers in Excel file.");
    }

    // Process headers
    const headers = jsonData[headerRowIndex].map((header) =>
      header !== undefined && header !== null ? header.toString().trim() : ""
    );

    // Find maximum number of columns
    const maxCols = Math.max(1, headers.length);

    // Process data rows
    const rows = jsonData
      .slice(headerRowIndex + 1)
      .filter(
        (row) =>
          row &&
          row.some(
            (cell) =>
              cell !== undefined &&
              cell !== null &&
              cell.toString().trim() !== ""
          )
      )
      .map((row) => {
        // Ensure each row has the same number of columns as headers
        const processedRow = [];
        for (let i = 0; i < maxCols; i++) {
          const cell =
            i < row.length && row[i] !== undefined && row[i] !== null
              ? row[i].toString().trim()
              : "";
          processedRow.push(cell);
        }
        return processedRow;
      });

    return { headers, rows };
  }

  /**
   * @private
   * @param {string} fileName
   * @returns {string}
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
   * @private
   * @param {string[]} items
   * @returns {string[][]}
   */
  _groupTextItemsByRows(items) {
    // This is a simplified approach - a more robust solution would use
    // the y-coordinates from the PDF to determine rows

    // Guess the number of columns based on the first few items
    const columnCount = 7; // Assuming we have 7 columns
    const rows = [];

    // Group items into rows of column count
    for (let i = 0; i < items.length; i += columnCount) {
      const row = items.slice(i, i + columnCount);
      if (row.length === columnCount) {
        rows.push(row);
      }
    }

    return rows;
  }

  /**
   * @getter
   * @returns {ParsedData}
   */
  get data() {
    return {
      headers: [...this._headers],
      rows: [...this._rows],
    };
  }

  /**
   * @setter
   * @param {ParsedData} data
   */
  set data(data) {
    if (!this._validateData(data)) {
      console.error("Data validation failed:", data);
      throw new Error("Invalid table data format.");
    }

    this._headers = [...data.headers];
    this._rows = [...data.rows];
  }

  /**
   * @public
   * @returns {void}
   */
  clear() {
    this._headers = [];
    this._rows = [];
  }

  /**
   * @private
   * @param {ParsedData} data
   * @returns {boolean}
   */
  _validateData(data) {
    if (!data || !Array.isArray(data.headers) || !Array.isArray(data.rows)) {
      console.error("Data structure invalid:", data);
      return false;
    }

    // Ensure we have at least one header
    if (data.headers.length === 0) {
      console.error("No headers found");
      return false;
    }

    // Check if all rows have the same number of columns as headers
    const valid = data.rows.every(
      (row) => Array.isArray(row) && row.length === data.headers.length
    );

    if (!valid) {
      console.error(
        "Row length mismatch with headers:",
        data.headers.length,
        data.rows.map((r) => r.length)
      );
    }

    return valid;
  }
}
