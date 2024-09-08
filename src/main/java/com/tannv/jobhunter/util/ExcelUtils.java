package com.tannv.jobhunter.util;

import com.tannv.jobhunter.model.excel.ExcelRow;
import com.tannv.jobhunter.model.excel.ExcelSheet;
import com.tannv.jobhunter.model.excel.ExcelWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExcelUtils {
    public static String[] HEADERS_TOOL_COMPARE = {
            "Product", "SerialNo", "Legacy Input Name", "Legacy Input Value",
            "Nxg Input Name", "Nxg Input Value"
    };
    public static String SHEET_NAME = "Compare Inputs";

    public static String getStringValue(Cell cell) {
        if (Objects.isNull(cell)) {
            return null;
        }
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                return Double.valueOf(cell.getNumericCellValue()).intValue() + "";
            default:
        }       return "";
    }

    public static Integer getIntegerValue(Cell cell) {
        if (Objects.isNull(cell)) {
            return null;
        }
        try {
            CellType type = cell.getCellType();
            switch (type) {
                case STRING:
                    return Integer.parseInt(cell.getRichStringCellValue().getString().trim());
                case NUMERIC:
                    return Double.valueOf(cell.getNumericCellValue()).intValue();
                default:
            }       return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Cell setText(Row row, int cellIndex, String value) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellValue(value);
        return cell;
    }

    public static CellStyle getCellStyleError(Workbook workbook) {
        CellStyle cellStyleError = workbook.createCellStyle();
        cellStyleError.setFillForegroundColor(IndexedColors.RED.getIndex());
        cellStyleError.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyleError;
    }

    public static  CellStyle getCellStyleHeader(Workbook workbook) {
        CellStyle cellStyleHeader = workbook.createCellStyle();
        cellStyleHeader.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        cellStyleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyleHeader;
    }

    public static List<ExcelWorkbook> parseExcelFiles(List<MultipartFile> files) throws IOException {
        List<ExcelWorkbook> workbooks = new ArrayList<>();
        for (MultipartFile file : files) {
            ExcelWorkbook workbook = parseExcelFile(file);
            workbooks.add(workbook);
        }
        return workbooks;
    }

    public static ExcelWorkbook parseExcelFile(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            String workbookName = file.getOriginalFilename();
            List<ExcelSheet> sheets = new ArrayList<>();

            // Iterate through all sheets
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                List<String> headers = new ArrayList<>();
                List<ExcelRow> data = new ArrayList<>();

                boolean isHeader = true;
                int maxColumns = 0;

                // Iterate through all rows
                int rowNumber = 0;
                for (Row row : sheet) {
                    List<String> rowData = new ArrayList<>();
                    maxColumns = Math.max(maxColumns, row.getLastCellNum());

                    // Iterate through all cells
                    for (int col = 0; col < maxColumns; col++) {
                        Cell cell = row.getCell(col);
                        String cellValue = cell != null ? getCellValueAsString(cell) : "";
                        rowData.add(cellValue);
                    }

                    if (isHeader) {
                        boolean isHeaderEmpty = rowData.stream().anyMatch(String::isEmpty);
                        if(isHeaderEmpty) throw new NullPointerException("Header is empty");
                        headers.addAll(rowData); // Set headers
                        isHeader = false;
                        ++rowNumber; // increase row number for header
                    } else {
                       // If any key is empty, skip the row
                        int separateIndexKey = separateIndexKeyHeader(headers);
                        boolean isKeyEmpty = rowData.subList(0, separateIndexKey).stream().anyMatch(String::isEmpty);
                        if (isKeyEmpty) continue;

                        // Create ExcelRow object and add it to the data list
                        ExcelRow excelRow = ExcelRow.builder()
                                .rowNumber(++rowNumber)
                                .data(rowData)
                                .build();
                        data.add(excelRow);
                    }
                }

                // Create an ExcelSheet object and add it to the list of sheets
                ExcelSheet excelSheet = new ExcelSheet(sheet.getSheetName(), headers, data);
                sheets.add(excelSheet);
            }

            // Create and return the ExcelWorkbook object
            ExcelWorkbook result = new ExcelWorkbook(workbookName, sheets);
            writeExcelFile(result, "src/main/resources/output");
            return result;
        }
    }

    public static int separateIndexKeyHeader(List<String> headers) {
        return headers.indexOf("END1");
    }

    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public static File writeExcelFile(ExcelWorkbook excelWorkbook, String directoryPath) throws IOException {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();

        // Iterate through sheets
        for (ExcelSheet excelSheet : excelWorkbook.getSheets()) {
            Sheet sheet = workbook.createSheet(excelSheet.getSheetName());

            // Write headers
            Row headerRow = sheet.createRow(0);
            List<String> headers = excelSheet.getHeaders();
            for (int col = 0; col < headers.size(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers.get(col));
            }

            // Write data
            List<ExcelRow> rows = excelSheet.getData();
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                ExcelRow excelRow = rows.get(rowIndex);
                Row row = sheet.createRow(rowIndex + 1); // Row 0 is for headers

                List<String> rowData = excelRow.getData();
                for (int col = 0; col < rowData.size(); col++) {
                    Cell cell = row.createCell(col);
                    cell.setCellValue(rowData.get(col));
                }
            }
        }

        // Create directory if not exists
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate file path
        String filePath = directoryPath + File.separator + excelWorkbook.getWorkbookName();
        File outputFile = new File(filePath);

        // Write the workbook to a file
        try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
            workbook.write(fileOut);
        }

        // Close the workbook
        workbook.close();

        return outputFile;
    }
}