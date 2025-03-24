class Controller {
  constructor() {
    /** @private @type {Model} */
    this.model = new Model();
    /** @private @type {View} */
    this.view = new View(this.model);

    /** @private @type {HTMLInputElement} */
    this.fileInput = document.getElementById("fileInput");
    /** @private @type {HTMLButtonElement} */
    this.uploadButton = document.getElementById("uploadBtn");

    if (!this.fileInput || !this.uploadButton) {
      throw new Error("Required elements not found");
    }
  }
  initialize() {
    this.view.initialize();
    this._setupEventListeners();
  }

  /**
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
        this.view.hideFileInfo();
      });
  }

  /**
   * @private
   * @returns {void}
   */
  _handleFileSelect() {
    const hasFile = this.fileInput.files.length > 0;
    this.uploadButton.disabled = !hasFile;
    if (!hasFile) {
      this.view.clearFeedback();
    }
  }

  /**
   * @private
   * @async
   * @returns {Promise<void>}
   */
  async _handleFileUpload() {
    const file = this.fileInput.files[0];
    if (!file) {
      this.view.showError("Please select a file.");
      return;
    }

    try {
      this.uploadButton.disabled = true;
      this.view.showLoading();

      const data = await this.model.readFile(file);
      this.model.setData(data);
      this.view.displaySchedule(file);

      this.uploadButton.disabled = false;
    } catch (error) {
      this.view.showError(error.message);
      this.fileInput.value = "";
      this.uploadButton.disabled = true;
    }
  }
}
