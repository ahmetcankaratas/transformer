package view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import controller.FileController;
import controller.HtmlController;
import model.TableData;
import util.FileValidator;

/**
 * Main view class for the application
 * Compatible with Eclipse IDE
 */
public class MainView extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JTabbedPane tabbedPane;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JTextArea htmlTextArea;
    private JPanel tablePreviewPanel;
    
    private FileController fileController;
    private HtmlController htmlController;
    
    // Keep track of the last generated HTML file
    private File lastGeneratedHtmlFile;
    
    public MainView() {
        super("Table Transformer - SEDS519");
        
        // Initialize controllers
        fileController = new FileController();
        htmlController = new HtmlController();
        
        // Set up the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());
        
        // Create toolbar
        JToolBar toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);
        
        // Create main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        
        // Create tabbed pane for the left side (tables and previews)
        tabbedPane = new JTabbedPane();
        
        // Create table preview panel
        tablePreviewPanel = new JPanel(new BorderLayout());
        tablePreviewPanel.add(new JLabel("No data available. Please load a file first."), BorderLayout.CENTER);
        tabbedPane.addTab("Table Preview", tablePreviewPanel);
        
        // HTML Preview panel for the right side
        JPanel htmlPanel = new JPanel(new BorderLayout());
        htmlTextArea = new JTextArea();
        htmlTextArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(htmlTextArea);
        htmlPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to split pane
        splitPane.setLeftComponent(tabbedPane);
        splitPane.setRightComponent(htmlPanel);
        
        // Add split pane to frame
        add(splitPane, BorderLayout.CENTER);
        
        // Create status bar
        statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Center the window
        setLocationRelativeTo(null);
        
        setVisible(true);
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton openPdfButton = new JButton("Open PDF");
        openPdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPdfFile();
            }
        });
        toolBar.add(openPdfButton);
        
        JButton openExcelButton = new JButton("Open Excel");
        openExcelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openExcelFile();
            }
        });
        toolBar.add(openExcelButton);
        
        JButton generateHtmlButton = new JButton("Generate HTML");
        generateHtmlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateHtml();
            }
        });
        toolBar.add(generateHtmlButton);
        
        JButton saveHtmlButton = new JButton("Save HTML");
        saveHtmlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveHtml();
            }
        });
        toolBar.add(saveHtmlButton);
        
        JButton viewInBrowserButton = new JButton("View in Browser");
        viewInBrowserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openHtmlInBrowser();
            }
        });
        toolBar.add(viewInBrowserButton);
        
        return toolBar;
    }
    
    private void openPdfFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select PDF File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            if (!FileValidator.isValidPdfFile(selectedFile)) {
                JOptionPane.showMessageDialog(this, "Invalid PDF file!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            statusLabel.setText("Processing PDF file: " + selectedFile.getName());
            
            // Process PDF file
            List<TableData> tableDataList = fileController.processPdfFile(selectedFile);
            
            // Update UI with the new data
            updateTablePreviews(tableDataList);
            
            statusLabel.setText("PDF file processed: " + selectedFile.getName());
        }
    }
    
    private void openExcelFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Excel File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx", "xls"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            if (!FileValidator.isValidExcelFile(selectedFile)) {
                JOptionPane.showMessageDialog(this, "Invalid Excel file!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            statusLabel.setText("Processing Excel file: " + selectedFile.getName());
            
            // Process Excel file
            List<TableData> tableDataList = fileController.processExcelFile(selectedFile);
            
            // Update UI with the new data
            updateTablePreviews(tableDataList);
            
            statusLabel.setText("Excel file processed: " + selectedFile.getName());
        }
    }
    
    /**
     * Updates the UI with the new table data
     * @param tableDataList The list of table data to display
     */
    private void updateTablePreviews(List<TableData> tableDataList) {
        // Clear existing tabs except the first tab (which is the permanent "Table Preview" tab)
        while (tabbedPane.getTabCount() > 1) {
            tabbedPane.removeTabAt(1);
        }
        
        // Update the main table preview panel
        tablePreviewPanel.removeAll();
        
        if (tableDataList.isEmpty()) {
            tablePreviewPanel.add(new JLabel("No data available. Please load a file first."), BorderLayout.CENTER);
        } else {
            // Create tabs for each table
            for (int i = 0; i < tableDataList.size(); i++) {
                TableData tableData = tableDataList.get(i);
                
                // Create a new panel for this table
                JPanel tablePanel = createTablePanel(tableData);
                
                // Add to tabs if there are multiple tables
                if (tableDataList.size() > 1) {
                    tabbedPane.addTab(tableData.getTableName(), tablePanel);
                } else {
                    // If only one table, update the main preview panel
                    tablePreviewPanel.add(tablePanel, BorderLayout.CENTER);
                }
            }
        }
        
        // Refresh the UI
        tablePreviewPanel.revalidate();
        tablePreviewPanel.repaint();
    }
    
    /**
     * Creates a panel containing a visual representation of the table data
     * @param tableData The table data to display
     * @return A panel containing the table
     */
    private JPanel createTablePanel(TableData tableData) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table headers
        String[] headers = tableData.getHeaders().toArray(new String[0]);
        
        // Create table data
        Object[][] data = new Object[tableData.getRows().size()][headers.length];
        for (int i = 0; i < tableData.getRows().size(); i++) {
            List<String> row = tableData.getRows().get(i);
            for (int j = 0; j < Math.min(row.size(), headers.length); j++) {
                data[i][j] = row.get(j);
            }
        }
        
        // Create the table
        JTable table = new JTable(new DefaultTableModel(data, headers));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add a label with table name
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("Table: " + tableData.getTableName()));
        panel.add(headerPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private void generateHtml() {
        List<TableData> tableDataList = fileController.getTableDataList();
        
        if (tableDataList.isEmpty()) {
            statusLabel.setText("No table data available. Please open PDF or Excel file first.");
            JOptionPane.showMessageDialog(this, "No table data available. Please open PDF or Excel file first.", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Generate HTML
        String html = htmlController.generateHtmlTabs(tableDataList);
        
        // Add Bootstrap CSS and JS for tabs
        String fullHtml = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "  <meta charset=\"UTF-8\">\n"
                + "  <title>Course Schedule</title>\n"
                + "  <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n"
                + "</head>\n"
                + "<body>\n"
                + "  <div class=\"container mt-4\">\n"
                + "    <h2>Course Schedule</h2>\n"
                + html + "\n"
                + "  </div>\n"
                + "  <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js\"></script>\n"
                + "</body>\n"
                + "</html>";
        
        // Update HTML text area
        htmlTextArea.setText(fullHtml);
        
        statusLabel.setText("HTML generated successfully.");
        
        // Create a temporary file and open it in browser
        try {
            File tempFile = File.createTempFile("table_preview_", ".html");
            tempFile.deleteOnExit();
            
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(fullHtml);
            }
            
            lastGeneratedHtmlFile = tempFile;
            
            // Open the HTML file in the default browser
            openInBrowser(tempFile);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating temporary HTML file: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void saveHtml() {
        if (htmlTextArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "You need to generate HTML first!", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save HTML File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Files", "html"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Add .html extension if not present
            if (!file.getName().toLowerCase().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }
            
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(htmlTextArea.getText());
                statusLabel.setText("HTML file saved: " + file.getName());
                JOptionPane.showMessageDialog(this, "HTML file saved successfully.", 
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                
                lastGeneratedHtmlFile = file;
                
                // Ask if user wants to open in browser
                int openInBrowserChoice = JOptionPane.showConfirmDialog(this, 
                        "Do you want to open the HTML file in your browser?", 
                        "Open in Browser", JOptionPane.YES_NO_OPTION);
                
                if (openInBrowserChoice == JOptionPane.YES_OPTION) {
                    openInBrowser(file);
                }
                
            } catch (IOException e) {
                statusLabel.setText("Failed to save HTML file: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Failed to save HTML file: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void openHtmlInBrowser() {
        if (lastGeneratedHtmlFile != null && lastGeneratedHtmlFile.exists()) {
            openInBrowser(lastGeneratedHtmlFile);
        } else if (!htmlTextArea.getText().isEmpty()) {
            // If we have HTML content but no file, create a temporary one
            try {
                File tempFile = File.createTempFile("table_preview_", ".html");
                tempFile.deleteOnExit();
                
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(htmlTextArea.getText());
                }
                
                lastGeneratedHtmlFile = tempFile;
                openInBrowser(tempFile);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error creating temporary HTML file: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No HTML content available. Please generate HTML first.", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void openInBrowser(File htmlFile) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(htmlFile.toURI());
            } else {
                // Fallback for platforms where Desktop is not supported
                String osName = System.getProperty("os.name").toLowerCase();
                
                if (osName.contains("win")) {
                    // Windows
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + htmlFile.getAbsolutePath());
                } else if (osName.contains("mac")) {
                    // macOS
                    Runtime.getRuntime().exec("open " + htmlFile.getAbsolutePath());
                } else if (osName.contains("nix") || osName.contains("nux")) {
                    // Linux
                    Runtime.getRuntime().exec("xdg-open " + htmlFile.getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Could not detect how to open the browser on your operating system.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            statusLabel.setText("HTML opened in browser: " + htmlFile.getName());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening HTML in browser: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
} 