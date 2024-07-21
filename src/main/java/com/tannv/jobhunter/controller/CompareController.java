package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.service.ToolCompareInputService;
import com.tannv.jobhunter.service.ToolCompareOutputService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/compare")
public class CompareController {
    private final ToolCompareInputService toolCompareService;
    private final ToolCompareOutputService toolCompareOutputService;

    public CompareController(ToolCompareInputService toolCompareService, ToolCompareOutputService toolCompareOutputService) {
        this.toolCompareService = toolCompareService;
        this.toolCompareOutputService = toolCompareOutputService;
    }

    @PostMapping("/input")
    public Object compareTechnicalCar(
            @RequestParam(name = "legacyFile") MultipartFile legacyReq,
            @RequestParam(name = "nxgFile")MultipartFile nxgReq)
            throws IOException, ParserConfigurationException, SAXException {
        return toolCompareService.compareTechnicalCar(legacyReq, nxgReq);
    }

    @PostMapping("/output")
    public Object compareBomCsv(
            @RequestParam(name = "legacyFile") MultipartFile legacyReq,
            @RequestParam(name = "nxgFile")MultipartFile nxgReq)
            throws IOException, ParserConfigurationException, SAXException {
        return toolCompareOutputService.compareBomCsvModel(legacyReq, nxgReq);
    }

}
