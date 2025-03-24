class View {
  constructor(model) {
    /** @private @type {Model} */
    this._model = model;

    /** @private @type {HTMLElement} */
    this.scheduleContent = document.getElementById("scheduleContent");
    /** @private @type {HTMLInputElement} */
    this.fileInput = document.getElementById("fileInput");
    /** @private @type {HTMLElement} */
    this.uploadFeedback = document.getElementById("uploadFeedback");
    /** @private @type {HTMLElement} */
    this.fileInfo = document.querySelector(".file-info");
    /** @private @type {HTMLElement} */
    this.fileName = this.fileInfo.querySelector(".file-name");
    /** @private @type {HTMLElement} */
    this.fileDetails = this.fileInfo.querySelector(".file-details");

    if (
      !this.scheduleContent ||
      !this.fileInput ||
      !this.uploadFeedback ||
      !this.fileInfo ||
      !this.fileName ||
      !this.fileDetails
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
    // Clear loading state
    this.clearFeedback();

    // Update file information
    this.showFileInfo(file);

    // Render the table directly
    const tableHtml = this.renderTable();
    this.scheduleContent.innerHTML = tableHtml;
  }

  /**
   * @returns {string}
   */
  renderTable() {
    if (!this._model) {
      return '<div class="alert alert-warning">Model not set</div>';
    }

    try {
      return this._model.transformToHTML();
    } catch (error) {
      console.error("Error rendering table:", error);
      return '<div class="alert alert-danger">Error rendering table</div>';
    }
  }

  /**
   * @param {Model} model
   */
  setModel(model) {
    this._model = model;
  }

  /**
   * @returns {Model}
   */
  getModel() {
    return this._model;
  }

  /**
   * @public
   * @returns {void}
   */
  clearFeedback() {
    this.uploadFeedback.innerHTML = "";
  }

  /**
   * @public
   * @param {File} file
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
   * @public
   * @returns {void}
   */
  hideFileInfo() {
    this.fileInfo.classList.remove("show");
    this.fileInfo.style.display = "none";
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
   * @param {string} fileType
   * @returns {string}
   */
  _getFileTypeLabel(fileType) {
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
  _formatFileSize(bytes) {
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
    this.uploadFeedback.innerHTML = `
      <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>
        ${this._escapeHtml(message)}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
      </div>
    `;
  }

  /**
   * @public
   * @returns {void}
   */
  showLoading() {
    this.uploadFeedback.innerHTML = `
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
  _escapeHtml(unsafe) {
    return unsafe
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }
}
