/**
 * @class TabView
 * @description Handles the tab interface for displaying multiple tables using Bootstrap
 */
class TabView {
  /**
   * @constructor
   * @param {string} containerId - The ID of the container element
   * @throws {Error} - Throws error if container element is not found
   */
  constructor(containerId) {
    this.container = document.getElementById(containerId);
    if (!this.container) {
      throw new Error(
        `Tab container element with ID '${containerId}' not found`
      );
    }

    this.buttonsContainer = this.container.querySelector(".nav-tabs");
    this.contentContainer = this.container.querySelector(".tab-content");

    if (!this.buttonsContainer || !this.contentContainer) {
      throw new Error(
        "Required tab elements not found. Make sure the container has .nav-tabs and .tab-content elements"
      );
    }

    this.tabs = [];
    this.activeTab = null;
  }

  /**
   * @method addTab
   * @description Adds a new tab to the interface
   * @param {string} tabId - Unique identifier for the tab
   * @param {string} tabName - Display name for the tab
   * @param {string} content - HTML content for the tab
   * @throws {Error} - Throws error if required parameters are missing
   */
  addTab(tabId, tabName, content) {
    if (!tabId || !tabName || !content) {
      throw new Error("Tab ID, name, and content are required");
    }

    // Check if tab with same ID already exists
    if (this.tabs.some((tab) => tab.id === tabId)) {
      throw new Error(`Tab with ID '${tabId}' already exists`);
    }

    this.tabs.push({
      id: tabId,
      name: tabName,
      content: content,
    });

    this._renderTabs();

    if (!this.activeTab) {
      this.activateTab(tabId);
    }
  }

  /**
   * @method activateTab
   * @description Activates the specified tab
   * @param {string} tabId - ID of the tab to activate
   * @throws {Error} - Throws error if tab ID is not found
   */
  activateTab(tabId) {
    const tab = this.tabs.find((t) => t.id === tabId);
    if (!tab) {
      throw new Error(`Tab with ID '${tabId}' not found`);
    }

    // Remove active classes from all tabs
    this.buttonsContainer.querySelectorAll(".nav-link").forEach((button) => {
      button.classList.remove("active");
      button.setAttribute("aria-selected", "false");
    });

    // Hide all tab content
    this.contentContainer.querySelectorAll(".tab-pane").forEach((content) => {
      content.classList.remove("show", "active");
    });

    // Activate selected tab
    const selectedButton = this.buttonsContainer.querySelector(
      `[data-bs-target="#${tabId}"]`
    );
    const selectedContent = this.contentContainer.querySelector(`#${tabId}`);

    if (selectedButton && selectedContent) {
      selectedButton.classList.add("active");
      selectedButton.setAttribute("aria-selected", "true");
      selectedContent.classList.add("show", "active");
      this.activeTab = tabId;
    } else {
      console.error("Tab elements not found in DOM");
      this._renderTabs();
    }
  }

  /**
   * @private
   * @method _renderTabs
   * @description Renders the tab interface using Bootstrap components
   */
  _renderTabs() {
    try {
      // Clear existing tabs
      this.buttonsContainer.innerHTML = "";
      this.contentContainer.innerHTML = "";

      // Create tab buttons and content
      this.tabs.forEach((tab, index) => {
        // Create list item for nav
        const li = document.createElement("li");
        li.className = "nav-item";
        li.setAttribute("role", "presentation");

        // Create button
        const button = document.createElement("button");
        button.className = "nav-link";
        button.setAttribute("data-bs-toggle", "tab");
        button.setAttribute("data-bs-target", `#${tab.id}`);
        button.setAttribute("type", "button");
        button.setAttribute("role", "tab");
        button.setAttribute("aria-controls", tab.id);
        button.setAttribute("aria-selected", "false");
        button.textContent = tab.name;

        li.appendChild(button);
        this.buttonsContainer.appendChild(li);

        // Create content
        const content = document.createElement("div");
        content.className = "tab-pane fade";
        content.id = tab.id;
        content.setAttribute("role", "tabpanel");
        content.setAttribute("aria-labelledby", `${tab.id}-tab`);

        // Wrap table in responsive div
        const tableWrapper = document.createElement("div");
        tableWrapper.className = "table-responsive";
        tableWrapper.innerHTML = tab.content;
        content.appendChild(tableWrapper);

        this.contentContainer.appendChild(content);
      });
    } catch (error) {
      console.error("Error rendering tabs:", error);
      throw new Error("Failed to render tabs: " + error.message);
    }
  }

  /**
   * @method updateTabContent
   * @description Updates the content of a specific tab
   * @param {string} tabId - ID of the tab to update
   * @param {string} content - New HTML content for the tab
   * @throws {Error} - Throws error if tab ID is not found
   */
  updateTabContent(tabId, content) {
    const tab = this.tabs.find((t) => t.id === tabId);
    if (!tab) {
      throw new Error(`Tab with ID '${tabId}' not found`);
    }

    tab.content = content;
    this._renderTabs();

    if (this.activeTab === tabId) {
      this.activateTab(tabId);
    }
  }

  /**
   * @method removeTab
   * @description Removes a tab from the interface
   * @param {string} tabId - ID of the tab to remove
   * @throws {Error} - Throws error if tab ID is not found
   */
  removeTab(tabId) {
    const tabIndex = this.tabs.findIndex((t) => t.id === tabId);
    if (tabIndex === -1) {
      throw new Error(`Tab with ID '${tabId}' not found`);
    }

    this.tabs.splice(tabIndex, 1);
    this._renderTabs();

    // If we removed the active tab, activate the first available tab
    if (this.activeTab === tabId && this.tabs.length > 0) {
      this.activateTab(this.tabs[0].id);
    } else if (this.tabs.length === 0) {
      this.activeTab = null;
    }
  }
}
