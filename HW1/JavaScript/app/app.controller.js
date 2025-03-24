class Controller {
  constructor() {
    /** @private @type {FileModel} - Handles file operations */
    this.fileModel = new Model();
    /** @private @type {TableModel} - Handles table data */
    this.tableModel = new TableModel();
    /** @private @type {AppView} - Handles all UI components */
    this.appView = new View(this.tableModel);

    /** @private @type {HTMLInputElement} - File input element */
    this.fileInput = document.getElementById("fileInput");
    /** @private @type {HTMLButtonElement} - Upload button element */
    this.uploadButton = document.getElementById("uploadBtn");

    if (!this.fileInput || !this.uploadButton) {
      throw new Error("Required elements not found");
    }
  }
  initialize() {
    this.appView.initialize();
    this._setupEventListeners();
  }

  /**
   * @method _setupEventListeners
   * @description Sets up event listeners for file input and upload button
   * @private
   * @returns {void}
   */
  _setupEventListeners() {
    this.fileInput.addEventListener("change", () => this._handleFileSelect());
    this.uploadButton.addEventListener("click", () => this._handleFileUpload());

    // Set up event listener for file info close button
    document
      .querySelector(".file-info .btn-close")
      .addEventListener("click", () => {
        this.appView.hideFileInfo();
      });
  }

  /**
   * @method _handleFileSelect
   * @description Handles file selection event
   * @private
   * @returns {void}
   */
  _handleFileSelect() {
    const hasFile = this.fileInput.files.length > 0;
    this.uploadButton.disabled = !hasFile;
    if (!hasFile) {
      this.appView.clearFeedback();
    }
  }

  /**
   * @method _handleFileUpload
   * @description Handles file upload event
   * @private
   * @async
   * @returns {Promise<void>}
   */
  async _handleFileUpload() {
    const file = this.fileInput.files[0];
    if (!file) {
      this.appView.showError("Please select a file.");
      return;
    }

    try {
      this.uploadButton.disabled = true;
      this.appView.showLoading();

      const data = await this.fileModel.readFile(file);
      this.tableModel.setData(data);
      this.appView.displaySchedule(file);

      this.uploadButton.disabled = false;
    } catch (error) {
      this.appView.showError(error.message);
      this.fileInput.value = "";
      this.uploadButton.disabled = true;
    }
  }
}
