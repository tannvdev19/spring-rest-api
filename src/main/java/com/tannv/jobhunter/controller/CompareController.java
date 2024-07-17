package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.service.XMLService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compare")
public class CompareController {
    private final XMLService xmlService;

    public CompareController(XMLService xmlService) {
        this.xmlService = xmlService;
    }

    @GetMapping
    public void readFile() {
        xmlService.readFile();
    }
}
