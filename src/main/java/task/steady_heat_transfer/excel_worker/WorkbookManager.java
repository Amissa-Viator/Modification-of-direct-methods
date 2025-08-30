package task.steady_heat_transfer.excel_worker;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorkbookManager {
    private static Workbook workbook = null;
    private static String filePath = null;

    private WorkbookManager() {}

    public static Workbook getWorkbook() {
        if (workbook == null) {
            workbook = new XSSFWorkbook();
            filePath = getUniqueFilePath("src/main/resources/result", "outputData", ".xlsx");
        }
        return workbook;
    }

    private static String getUniqueFilePath(String directoryPath, String fileName, String extension) {
        String uniqueFilePath = directoryPath + "/" + fileName + extension;
        File file = new File(uniqueFilePath);
        int counter = 1;
        while (file.exists()) {
            uniqueFilePath = directoryPath + "/" + fileName + counter + extension;
            file = new File(uniqueFilePath);
            counter++;
        }
        return uniqueFilePath;
    }

    public static void closeWorkbook() {
        if (workbook != null) {
            try {
                workbook.close();
                System.out.println("Workbook closed successfully.");
            } catch (IOException e) {
                System.err.println("Error closing workbook: " + e.getMessage());
            }
        }
    }

    public static void saveWorkbook() {
        if (filePath == null || workbook == null) {
            System.err.println("Workbook or filePath wasn't initialized");
            return;
        }

        if (!isWorkbookFilledWithData()) {
            System.err.println("Workbook is empty or not filled with data");
            return;
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            System.out.println("Data successfully exported to " + filePath);
        } catch (IOException e) {
            System.err.println("Error with writing to Excel-file: " + e.getMessage());
        }
    }

    private static boolean isWorkbookFilledWithData() {
        if (workbook.getNumberOfSheets() == 0) {
            return false;
        }

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (cell.getCellType() != CellType.BLANK) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static void createDirectoryIfNotExists() {
        String directoryPath = "src/main/resources/result";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created: " + directoryPath);
            } else {
                System.err.println("Failed to create directory: " + directoryPath);
            }
        }
    }
}
