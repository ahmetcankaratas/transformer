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

    const headers = jsonData[0].map((header) => header.toString().trim());
    const rows = jsonData
      .slice(1)
      .map((row) => row.map((cell) => (cell || "").toString().trim()));

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
      return false;
    }

    // Check if all rows have the same number of columns as headers
    return data.rows.every(
      (row) => Array.isArray(row) && row.length === data.headers.length
    );
  }

  /**
   * @returns {string}
   */
  transformToHTML() {
    if (!this._headers || !this._rows) {
      return '<div class="alert alert-warning">No data available</div>';
    }

    let html = '<table class="table table-bordered table-striped table-hover">';

    // Add headers
    html += '<thead class="table-light"><tr>';
    for (const header of this._headers) {
      html += `<th>${this._escapeHTML(header)}</th>`;
    }
    html += "</tr></thead>";

    // Add body
    html += "<tbody>";
    for (const row of this._rows) {
      html += "<tr>";
      for (const cell of row) {
        html += `<td>${this._escapeHTML(cell)}</td>`;
      }
      html += "</tr>";
    }
    html += "</tbody></table>";

    return html;
  }

  /**
   * @private
   * @param {string} unsafe
   * @returns {string}
   */
  _escapeHTML(unsafe) {
    return unsafe
      .toString()
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }
}
