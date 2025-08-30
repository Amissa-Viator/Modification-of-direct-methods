package task.steady_heat_transfer.excel_worker;

import org.apache.poi.ss.usermodel.*;
import task.steady_heat_transfer.algorithm.OutputData;
import java.util.List;

public class ExcelWriter {
    public static void exportToExcelValues(List<OutputData> dataList, String methodName) {
        Workbook workbook = WorkbookManager.getWorkbook();
        if (workbook.getSheet(methodName) != null) {
            System.err.println("Sheet with name " + methodName + " already exists");
            return;
        }
        Sheet sheet = workbook.createSheet(methodName);

        String[] headers = {
                "X", "U", "V"
        };
        createHeaderRow(sheet, headers);

        int rowNum = 1;
        for (OutputData data : dataList) {
            Row row = sheet.createRow(rowNum++);
            setCellValue(row.createCell(0), data.getxValue());
            setCellValue(row.createCell(1), data.getuValue());
            setCellValue(row.createCell(2), data.getvValue());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public static void exportToExcelFunctional(List<Double> values, String sheetName) {
        Workbook workbook = WorkbookManager.getWorkbook();
        if (workbook.getSheet(sheetName) != null) {
            System.err.println("Sheet with name " + sheetName + " already exists");
            return;
        }
        Sheet sheet = workbook.createSheet(sheetName);

        String[] headers = {"Iteration", "J(v)"};
        createHeaderRow(sheet, headers);

        int rowNum = 1;
        for (int i=0; i < values.size(); i++) {
            Row row = sheet.createRow(rowNum++);
            setCellValue(row.createCell(0), i);
            setCellValue(row.createCell(1), values.get(i));
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    private static void createHeaderRow(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        Workbook workbook = sheet.getWorkbook();
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private static void setCellValue(Cell cell, double value) {
        if (Double.isInfinite(value)) {
            cell.setCellValue(value > 0 ? "Infinity" : "-Infinity");
        } else if (Double.isNaN(value)) {
            cell.setCellValue("NaN");
        } else {
            cell.setCellValue(value);
        }
    }

    public static void exportToExcelGradient(double[] gradient, String sheetName) {
        Workbook workbook = WorkbookManager.getWorkbook();
        if (workbook.getSheet(sheetName) != null) {
            System.err.println("Sheet with name " + sheetName + " already exists");
            return;
        }
        Sheet sheet = workbook.createSheet(sheetName);

        String[] headers = {"Iteration", "J'(v)"};
        createHeaderRow(sheet, headers);

        int rowNum = 1;
        for (int i = 0; i < gradient.length; i++) {
            Row row = sheet.createRow(rowNum++);
            setCellValue(row.createCell(0), i);
            setCellValue(row.createCell(1), gradient[i]);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}

