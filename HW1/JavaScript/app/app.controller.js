class Controller {
  #model;
  #view;
  #fileInput;
  #uploadButton;

  constructor() {
    this.#model = new Model();
    this.#view = new View(this.#model);

    this.#fileInput = document.getElementById("fileInput");
    this.#uploadButton = document.getElementById("uploadBtn");

    if (!this.#fileInput || !this.#uploadButton) {
      throw new Error("Required elements not found");
    }
  }
  initialize() {
    this.#view.initialize();
    this.#setupEventListeners();
  }

  /**
   * @method #setupEventListeners
   * @private
   * @returns {void}
   */
  #setupEventListeners() {
    this.#fileInput.addEventListener("change", () => this.#handleFileSelect());
    this.#uploadButton.addEventListener("click", () =>
      this.#handleFileUpload()
    );

    document
      .querySelector(".file-info .btn-close")
      .addEventListener("click", () => {
        this.#view.hideFileInfo();
      });
  }

  /**
   * @method #handleFileSelect
   * @private
   * @returns {void}
   */
  #handleFileSelect() {
    const hasFile = this.#fileInput.files.length > 0;
    this.#uploadButton.disabled = !hasFile;
    if (!hasFile) {
      this.#view.clearFeedback();
    }
  }

  /**
   * @method #handleFileUpload
   * @private
   * @async
   * @returns {Promise<void>}
   */
  async #handleFileUpload() {
    const file = this.#fileInput.files[0];
    if (!file) {
      this.#view.showError("Please select a file.");
      return;
    }

    try {
      this.#uploadButton.disabled = true;
      this.#view.showLoading();

      console.log(
        `Processing file: ${file.name} (${file.type}, ${file.size} bytes)`
      );
      const data = await this.#model.readFile(file);

      console.log(`File processed successfully:`, {
        headers: data.headers.length,
        rows: data.rows.length,
      });

      this.#model.data = data;
      this.#view.displaySchedule(file);

      this.#uploadButton.disabled = false;
    } catch (error) {
      console.error("File upload error:", error);
      this.#view.showError(error.message);
      this.#fileInput.value = "";
      this.#uploadButton.disabled = true;
    }
  }
}
