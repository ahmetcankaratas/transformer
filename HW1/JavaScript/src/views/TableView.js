/**
 * @class TableView
 * @description Handles the rendering of individual tables
 */
class TableView {
  /**
   * @constructor
   * @param {TableModel} tableModel - The table model instance
   */
  constructor(tableModel) {
    this.tableModel = tableModel;
  }

  /**
   * @method render
   * @description Renders the table using the current table model data
   * @returns {string} - HTML string representation of the table
   * @throws {Error} - Throws error if table model is not set
   */
  render() {
    if (!this.tableModel) {
      throw new Error("Table model not set");
    }

    try {
      return this.tableModel.transformToHTML();
    } catch (error) {
      console.error("Error rendering table:", error);
      return '<div class="error">Error rendering table</div>';
    }
  }

  /**
   * @method setModel
   * @description Sets a new table model
   * @param {TableModel} tableModel - The new table model instance
   */
  setModel(tableModel) {
    this.tableModel = tableModel;
  }

  /**
   * @method getModel
   * @description Gets the current table model
   * @returns {TableModel} - The current table model instance
   */
  getModel() {
    return this.tableModel;
  }
}
