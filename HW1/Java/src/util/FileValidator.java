package util;
import java.io.File;

public class FileValidator {
    
    public static boolean isValidPdfFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".pdf");
    }
    
    public static boolean isValidExcelFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }
} 