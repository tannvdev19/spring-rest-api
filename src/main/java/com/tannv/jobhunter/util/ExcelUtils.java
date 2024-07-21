package com.tannv.jobhunter.util;

import org.apache.poi.ss.usermodel.*;

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
}