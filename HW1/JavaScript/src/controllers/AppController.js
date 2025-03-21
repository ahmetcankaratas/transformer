/**
 * @class AppController
 * @description Main application controller that handles user interactions and coordinates models and views
 * @public
 */
class AppController {
  /**
   * @constructor
   * @public
   * @throws {Error} When required dependencies are not found
   */
  constructor() {
    /** @private @type {FileModel} - Handles file operations */
    this.fileModel = new FileModel();
    /** @private @type {ScheduleView} - Handles UI display */
    this.scheduleView = new ScheduleView();
    /** @private @type {HTMLInputElement} - File input element */
    this.fileInput = document.getElementById("fileInput");
    /** @private @type {HTMLButtonElement} - Upload button element */
    this.uploadButton = document.getElementById("uploadBtn");

    if (!this.fileInput || !this.uploadButton) {
      throw new Error("Required elements not found");
    }
  }

  /**
   * @method initialize
   * @description Initializes the application and sets up event listeners
   * @public
   * @returns {void}
   */
  initialize() {
    this.scheduleView.initialize();
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
      this.scheduleView.clearFeedback();
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
      this.scheduleView.showError("Please select a file.");
      return;
    }

    try {
      this.uploadButton.disabled = true;
      this.scheduleView.showLoading();

      const data = await this.fileModel.readFile(file);
      this.scheduleView.displaySchedule(data, file);

      this.uploadButton.disabled = false;
    } catch (error) {
      this.scheduleView.showError(error.message);
      this.fileInput.value = "";
      this.uploadButton.disabled = true;
    }
  }
}
