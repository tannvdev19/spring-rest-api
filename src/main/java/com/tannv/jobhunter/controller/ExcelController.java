package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.model.excel.ExcelWorkbook;
import com.tannv.jobhunter.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/read-file")
    public ResponseEntity<List<ExcelWorkbook>> uploadExcelFile(@RequestParam("file") List<MultipartFile> files) {
        try {
            // Parse and map Excel data into ExcelWorkbook object
            List<ExcelWorkbook> workbook = excelService.getExcelWorkbook(files);
            return ResponseEntity.ok(workbook);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
