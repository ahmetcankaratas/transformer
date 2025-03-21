/**
 * @typedef {Object} TableData
 * @property {string[]} headers - Table column headers
 * @property {string[][]} rows - Table row data
 */

/**
 * @class ScheduleView
 * @description Handles the display of course schedules
 * @public
 */
class ScheduleView {
  /**
   * @constructor
   * @public
   * @throws {Error} When required DOM elements are not found
   */
  constructor() {
    /** @private @type {HTMLElement} - Container for schedule content */
    this.scheduleContent = document.getElementById("scheduleContent");
    /** @private @type {HTMLInputElement} - File input element */
    this.fileInput = document.getElementById("fileInput");
    /** @private @type {HTMLElement} - Container for upload feedback */
    this.uploadFeedback = document.getElementById("uploadFeedback");
    /** @private @type {HTMLElement} - Container for file information */
    this.fileInfo = document.querySelector(".file-info");
    /** @private @type {HTMLElement} - Element displaying file name */
    this.fileName = this.fileInfo.querySelector(".file-name");
    /** @private @type {HTMLElement} - Element displaying file details */
    this.fileDetails = this.fileInfo.querySelector(".file-details");
    /** @private @type {HTMLButtonElement} - Close button for file info */
    this.closeButton = this.fileInfo.querySelector(".btn-close");

    if (
      !this.scheduleContent ||
      !this.fileInput ||
      !this.uploadFeedback ||
      !this.fileInfo ||
      !this.fileName ||
      !this.fileDetails ||
      !this.closeButton
    ) {
      throw new Error("Required elements not found");
    }

    // Bind event listeners
    this.closeButton.addEventListener("click", () => this.hideFileInfo());
  }

  /**
   * @method initialize
   * @description Initializes the schedule view
   * @public
   * @returns {void}
   */
  initialize() {
    // Nothing to initialize for now
  }

  /**
   * @method displaySchedule
   * @description Displays the schedule data in the table
   * @public
   * @param {TableData} data - The parsed schedule data
   * @param {File} file - The uploaded file
   * @returns {void}
   * @throws {Error} When data format is invalid
   */
  displaySchedule(data, file) {
    if (!data || !data.headers || !data.rows) {
      this.showError("Invalid data format");
      return;
    }

    // Clear loading state
    this.clearFeedback();

    // Update file information
    this.showFileInfo(file);

    // Create and display table
    const tableHtml = this._createTableHtml(data);
    this.scheduleContent.innerHTML = tableHtml;
  }

  /**
   * @method clearFeedback
   * @description Clears any feedback messages (loading, error, etc.)
   * @public
   * @returns {void}
   */
  clearFeedback() {
    this.uploadFeedback.innerHTML = "";
  }

  /**
   * @method showFileInfo
   * @description Shows file information in the UI
   * @public
   * @param {File} file - The file to display information for
   * @returns {void}
   */
  showFileInfo(file) {
    const fileSize = this._formatFileSize(file.size);
    const lastModified = new Date(file.lastModified).toLocaleString();
    const fileType = file.type || this._getFileTypeFromName(file.name);

    this.fileName.textContent = file.name;
    this.fileDetails.textContent = `${this._getFileTypeLabel(
      fileType
    )} • ${fileSize} • Last modified: ${lastModified}`;

    this.fileInfo.classList.add("show");
    this.fileInfo.style.display = "block";
  }

  /**
   * @method hideFileInfo
   * @description Hides the file information section
   * @public
   * @returns {void}
   */
  hideFileInfo() {
    this.fileInfo.classList.remove("show");
    this.fileInfo.style.display = "none";
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
   * @method _getFileTypeLabel
   * @description Gets a user-friendly label for file type
   * @private
   * @param {string} fileType - MIME type of the file
   * @returns {string} User-friendly label in English
   */
  _getFileTypeLabel(fileType) {
    if (fileType.includes("pdf")) {
      return "PDF File";
    } else if (fileType.includes("sheet")) {
      return "Excel File";
    }
    return "File";
  }

  /**
   * @method _formatFileSize
   * @description Formats file size in bytes to human readable format
   * @private
   * @param {number} bytes - File size in bytes
   * @returns {string} Formatted file size with unit
   */
  _formatFileSize(bytes) {
    if (bytes === 0) return "0 Bytes";

    const k = 1024;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
  }

  /**
   * @method _createTableHtml
   * @description Creates HTML table from data
   * @private
   * @param {TableData} data - The table data with headers and rows
   * @returns {string} HTML string for the table
   */
  _createTableHtml(data) {
    if (!data.headers || !data.rows) {
      return '<div class="alert alert-warning">No data available</div>';
    }

    let html = '<table class="table table-bordered table-striped table-hover">';

    // Add headers
    html += '<thead class="table-light"><tr>';
    data.headers.forEach((header) => {
      html += `<th>${this._escapeHtml(header)}</th>`;
    });
    html += "</tr></thead>";

    // Add body
    html += "<tbody>";
    data.rows.forEach((row) => {
      html += "<tr>";
      row.forEach((cell) => {
        html += `<td>${this._escapeHtml(cell)}</td>`;
      });
      html += "</tr>";
    });
    html += "</tbody></table>";

    return html;
  }

  /**
   * @method _escapeHtml
   * @description Escapes HTML special characters
   * @private
   * @param {string} unsafe - The string to escape
   * @returns {string} Escaped string safe for HTML insertion
   */
  _escapeHtml(unsafe) {
    return unsafe
      .toString()
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

  /**
   * @method showError
   * @description Displays an error message
   * @public
   * @param {string} message - The error message to display
   * @returns {void}
   */
  showError(message) {
    this.hideFileInfo();
    this.uploadFeedback.innerHTML = `
      <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
      </div>
    `;
    this.scheduleContent.innerHTML = "";
  }

  /**
   * @method showLoading
   * @description Shows a loading indicator
   * @public
   * @returns {void}
   */
  showLoading() {
    this.hideFileInfo();
    this.clearFeedback();
    this.uploadFeedback.innerHTML = `
      <div class="alert alert-info loading-alert" role="alert">
        <div class="d-flex align-items-center">
          <div class="spinner-border spinner-border-sm me-2" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          Processing file...
        </div>
      </div>
    `;
  }
}
