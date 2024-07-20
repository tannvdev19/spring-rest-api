package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.model.ProjectBOM;
import com.tannv.jobhunter.service.CsvService;
import com.tannv.jobhunter.service.XMLService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compare")
public class CompareController {
    private final XMLService xmlService;
    private final CsvService csvService;


    public CompareController(XMLService xmlService, CsvService csvService) {
        this.xmlService = xmlService;
        this.csvService = csvService;
    }

    @GetMapping
    public boolean readFile() {
        return xmlService.readFile();
    }

    @PostMapping("/csv")
    public void testCsv() {
        csvService.readFile();
    }


}
