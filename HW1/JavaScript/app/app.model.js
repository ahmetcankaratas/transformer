class Model {
  #pdfLib;
  #XLSX;
  #headers = [];
  #rows = [];

  constructor() {
    this.#pdfLib = window.pdfjsLib;
    this.#XLSX = window.XLSX;
  }

  /**
   * @public
   * @async
   * @param {File} file
   * @returns {Promise<ParsedData>}
   */
  async readFile(file) {
    const fileType = file.type || this.#getFileTypeFromName(file.name);

    if (fileType.includes("pdf")) {
      return await this.#readPdfFile(file);
    } else if (fileType.includes("sheet") || /\.xlsx?$/.test(file.name)) {
      return await this.#readExcelFile(file);
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
  async #readPdfFile(file) {
    try {
      const arrayBuffer = await file.arrayBuffer();
      const pdf = await this.#pdfLib.getDocument(arrayBuffer).promise;
      const page = await pdf.getPage(1);
      const textContent = await page.getTextContent();

      return this.#processPdfContent(textContent);
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
  async #readExcelFile(file) {
    try {
      const arrayBuffer = await file.arrayBuffer();
      const workbook = this.#XLSX.read(arrayBuffer);
      const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
      const jsonData = this.#XLSX.utils.sheet_to_json(firstSheet, {
        header: 1,
      });

      return this.#processExcelData(jsonData);
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
  #processPdfContent(textContent) {
    const items = textContent.items
      .map((item) => item.str.trim())
      .filter((str) => str.length > 0);

    const rows = this.#groupTextItemsByRows(items);

    // Determine headers (first row) and data rows
    const headers = rows.length > 0 ? rows[rows.length - 1] : [];
    const dataRows = rows.length > 1 ? rows.slice(1) : [];

    return { headers, rows: dataRows };
  }

  /**
   * @private
   * @param {Array<Array<string>>} jsonData
   * @returns {ParsedData}
   */
  #processExcelData(jsonData) {
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
  #getFileTypeFromName(fileName) {
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
  #groupTextItemsByRows(items) {
    // simplified approach

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
      headers: [...this.#headers],
      rows: [...this.#rows],
    };
  }

  /**
   * @setter
   * @param {ParsedData} data
   */
  set data(data) {
    if (!this.#validateData(data)) {
      console.error("Data validation failed:", data);
      throw new Error("Invalid table data format.");
    }

    this.#headers = [...data.headers];
    this.#rows = [...data.rows];
  }

  /**
   * @public
   * @returns {void}
   */
  clear() {
    this.#headers = [];
    this.#rows = [];
  }

  /**
   * @private
   * @param {ParsedData} data
   * @returns {boolean}
   */
  #validateData(data) {
    if (!data || !Array.isArray(data.headers) || !Array.isArray(data.rows)) {
      console.error("Data structure invalid:", data);
      return false;
    }

    // Ensure at least one header
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
