class View {
  #model;
  #scheduleContent;
  #fileInput;
  #uploadFeedback;
  #fileInfo;
  #fileName;
  #fileDetails;

  constructor(model) {
    this.#model = model;
    this.#scheduleContent = document.getElementById("scheduleContent");
    this.#fileInput = document.getElementById("fileInput");
    this.#uploadFeedback = document.getElementById("uploadFeedback");
    this.#fileInfo = document.querySelector(".file-info");
    this.#fileName = this.#fileInfo.querySelector(".file-name");
    this.#fileDetails = this.#fileInfo.querySelector(".file-details");

    if (
      !this.#scheduleContent ||
      !this.#fileInput ||
      !this.#uploadFeedback ||
      !this.#fileInfo ||
      !this.#fileName ||
      !this.#fileDetails
    ) {
      throw new Error("Required elements not found");
    }
  }

  initialize() {}

  /**
   * @public
   * @param {File} file
   * @returns {void}
   */
  displaySchedule(file) {
    this.clearFeedback();
    this.showFileInfo(file);

    const tableHtml = this.renderTable();
    this.#scheduleContent.innerHTML = tableHtml;
  }

  /**
   * @returns {string}
   */
  renderTable() {
    if (!this.#model) {
      return '<div class="alert alert-warning">Model not set</div>';
    }

    try {
      const data = this.#model.data;
      return this.#generateTableHTML(data);
    } catch (error) {
      console.error("Error rendering table:", error);
      return `<div class="alert alert-danger">Error rendering table: ${this.#escapeHtml(
        error.message
      )}</div>`;
    }
  }

  /**
   * @private
   * @param {ParsedData} data
   * @returns {string}
   */
  #generateTableHTML(data) {
    const { headers, rows } = data;

    if (!headers || !rows || headers.length === 0) {
      console.warn("No data available for HTML transformation");
      return '<div class="alert alert-warning">No data available</div>';
    }

    let html = '<table class="table table-bordered table-striped table-hover">';

    // Add headers
    html += '<thead class="table-light"><tr>';
    for (const header of headers) {
      html += `<th>${this.#escapeHtml(header || "")}</th>`;
    }
    html += "</tr></thead>";

    // Add body
    html += "<tbody>";
    for (const row of rows) {
      html += "<tr>";
      for (let i = 0; i < headers.length; i++) {
        const cell = i < row.length ? row[i] : "";
        html += `<td>${this.#escapeHtml(cell || "")}</td>`;
      }
      html += "</tr>";
    }
    html += "</tbody></table>";

    return html;
  }

  /**
   * @getter
   * @returns {Model}
   */
  get model() {
    return this.#model;
  }

  /**
   * @setter
   * @param {Model} model
   */
  set model(model) {
    this.#model = model;
  }

  /**
   * @public
   * @returns {void}
   */
  clearFeedback() {
    this.#uploadFeedback.innerHTML = "";
  }

  /**
   * @public
   * @param {File} file
   * @returns {void}
   */
  showFileInfo(file) {
    const fileSize = this.#formatFileSize(file.size);
    const lastModified = new Date(file.lastModified).toLocaleString();
    const fileType = file.type || this.#getFileTypeFromName(file.name);

    this.#fileName.textContent = file.name;
    this.#fileDetails.textContent = `${this.#getFileTypeLabel(
      fileType
    )} • ${fileSize} • Last modified: ${lastModified}`;

    this.#fileInfo.classList.add("show");
    this.#fileInfo.style.display = "block";
  }

  /**
   * @public
   * @returns {void}
   */
  hideFileInfo() {
    this.#fileInfo.classList.remove("show");
    this.#fileInfo.style.display = "none";
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
   * @param {string} fileType
   * @returns {string}
   */
  #getFileTypeLabel(fileType) {
    if (fileType.includes("pdf")) {
      return "PDF Document";
    } else if (fileType.includes("sheet") || fileType.includes("excel")) {
      return "Excel Spreadsheet";
    } else {
      return "Unknown File Type";
    }
  }

  /**
   * @private
   * @param {number} bytes
   * @returns {string}
   */
  #formatFileSize(bytes) {
    if (bytes === 0) return "0 Bytes";

    const k = 1024;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
  }

  /**
   * @public
   * @param {string} message
   * @returns {void}
   */
  showError(message) {
    this.#uploadFeedback.innerHTML = `
      <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>
        ${this.#escapeHtml(message)}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
      </div>
    `;
  }

  /**
   * @public
   * @returns {void}
   */
  showLoading() {
    this.#uploadFeedback.innerHTML = `
      <div class="d-flex align-items-center">
        <div class="spinner-border spinner-border-sm text-primary me-2" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <span>Processing file...</span>
      </div>
    `;
  }

  /**
   * @private
   * @param {string} unsafe
   * @returns {string}
   */
  #escapeHtml(unsafe) {
    if (unsafe === undefined || unsafe === null) {
      return "";
    }
    return unsafe
      .toString()
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }
}
