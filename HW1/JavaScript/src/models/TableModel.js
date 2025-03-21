/**
 * @typedef {Object} TableData
 * @property {string[]} headers - Table column headers
 * @property {string[][]} rows - Table row data
 */

/**
 * @class TableModel
 * @description Handles table data processing and manipulation
 * @public
 */
class TableModel {
  /**
   * @constructor
   * @public
   */
  constructor() {
    /** @private @type {string[]} - Table headers */
    this.headers = [];
    /** @private @type {string[][]} - Table rows */
    this.rows = [];
  }

  /**
   * @method setData
   * @description Sets the table data
   * @public
   * @param {TableData} data - The table data to set
   * @returns {void}
   * @throws {Error} When data format is invalid
   */
  setData(data) {
    if (!this._validateData(data)) {
      throw new Error("Invalid table data format.");
    }

    this.headers = [...data.headers];
    this.rows = [...data.rows];
  }

  /**
   * @method getData
   * @description Gets the current table data
   * @public
   * @returns {TableData} The current table data
   */
  getData() {
    return {
      headers: [...this.headers],
      rows: [...this.rows],
    };
  }

  /**
   * @method clear
   * @description Clears all table data
   * @public
   * @returns {void}
   */
  clear() {
    this.headers = [];
    this.rows = [];
  }

  /**
   * @method _validateData
   * @description Validates table data format
   * @private
   * @param {TableData} data - The data to validate
   * @returns {boolean} True if data is valid, false otherwise
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
   * @method transformToHTML
   * @description Transforms the data into HTML table format
   * @returns {string} - HTML table string
   * @throws {Error} - Throws error if no data is available
   */
  transformToHTML() {
    if (!this.data) {
      throw new Error("No data available for transformation");
    }

    let html = "<table>";

    // Add headers
    html += "<thead><tr>";
    for (const header of this.data.headers) {
      html += `<th>${this._escapeHTML(header)}</th>`;
    }
    html += "</tr></thead>";

    // Add body
    html += "<tbody>";
    for (const row of this.data.rows) {
      html += "<tr>";
      for (const cell of row) {
        html += `<td>${this._escapeHTML(cell)}</td>`;
      }
      html += "</tr>";
    }
    html += "</tbody>";

    html += "</table>";
    return html;
  }

  /**
   * @private
   * @method _escapeHTML
   * @description Escapes HTML special characters to prevent XSS
   * @param {string} unsafe - The unsafe string that might contain HTML
   * @returns {string} - Escaped safe string
   */
  _escapeHTML(unsafe) {
    return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }
}
