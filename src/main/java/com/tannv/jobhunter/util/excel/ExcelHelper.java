package com.tannv.jobhunter.util.excel;

import com.tannv.jobhunter.domain.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelHelper {
    public static String[] HEADERS = {
            "id", "address", "age", "created_at", "email", "gender", "name"
    };

    public static String SHEET_NAME = "user_data";

    public static ByteArrayInputStream dataToExcel(List<User> users) throws IOException{
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);

            Row row = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;
            for (User user : users) {
                Row dataRow = sheet.createRow(rowIndex);
                rowIndex++;

                dataRow.createCell(0).setCellValue(user.getId());
                dataRow.createCell(1).setCellValue(user.getAddress());
                dataRow.createCell(2).setCellValue(user.getAge());
                dataRow.createCell(3).setCellValue(user.getCreatedAt() == null ? "": user.getCreatedAt().toString());
                dataRow.createCell(4).setCellValue(user.getEmail());
                dataRow.createCell(5).setCellValue(user.getGender().toString());
                dataRow.createCell(6).setCellValue(user.getName());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
