class Controller {
  constructor() {
    /** @private @type {Model} */
    this._model = new Model();
    /** @private @type {View} */
    this._view = new View(this._model);

    /** @private @type {HTMLInputElement} */
    this._fileInput = document.getElementById("fileInput");
    /** @private @type {HTMLButtonElement} */
    this._uploadButton = document.getElementById("uploadBtn");

    if (!this._fileInput || !this._uploadButton) {
      throw new Error("Required elements not found");
    }
  }
  initialize() {
    this._view.initialize();
    this._setupEventListeners();
  }

  /**
   * @method _setupEventListeners
   * @private
   * @returns {void}
   */
  _setupEventListeners() {
    this._fileInput.addEventListener("change", () => this._handleFileSelect());
    this._uploadButton.addEventListener("click", () =>
      this._handleFileUpload()
    );

    // Set up event listener for file info close button
    document
      .querySelector(".file-info .btn-close")
      .addEventListener("click", () => {
        this._view.hideFileInfo();
      });
  }

  /**
   * @method _handleFileSelect
   * @private
   * @returns {void}
   */
  _handleFileSelect() {
    const hasFile = this._fileInput.files.length > 0;
    this._uploadButton.disabled = !hasFile;
    if (!hasFile) {
      this._view.clearFeedback();
    }
  }

  /**
   * @method _handleFileUpload
   * @private
   * @async
   * @returns {Promise<void>}
   */
  async _handleFileUpload() {
    const file = this._fileInput.files[0];
    if (!file) {
      this._view.showError("Please select a file.");
      return;
    }

    try {
      this._uploadButton.disabled = true;
      this._view.showLoading();

      console.log(
        `Processing file: ${file.name} (${file.type}, ${file.size} bytes)`
      );
      const data = await this._model.readFile(file);

      console.log(`File processed successfully:`, {
        headers: data.headers.length,
        rows: data.rows.length,
      });

      this._model.data = data;
      this._view.displaySchedule(file);

      this._uploadButton.disabled = false;
    } catch (error) {
      console.error("File upload error:", error);
      this._view.showError(error.message);
      this._fileInput.value = "";
      this._uploadButton.disabled = true;
    }
  }
}
