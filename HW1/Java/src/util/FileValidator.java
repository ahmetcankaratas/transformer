package util;

import java.io.File;

/**
 * Utility class for validating files
 */
public class FileValidator {
    
    /**
     * Check if file is a valid PDF file
     * @param file File to check
     * @return true if file is a valid PDF
     */
    public static boolean isValidPdfFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".pdf");
    }
    
    /**
     * Check if file is a valid Excel file
     * @param file File to check
     * @return true if file is a valid Excel
     */
    public static boolean isValidExcelFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }
} 