package com.tannv.jobhunter.service;

import com.tannv.jobhunter.domain.response.ApiResponse;
import com.tannv.jobhunter.model.CompareInputLineModel;
import com.tannv.jobhunter.model.TechnicalCarModel;
import com.tannv.jobhunter.util.ExcelUtils;
import com.tannv.jobhunter.util.FileUtils;
import com.tannv.jobhunter.util.ToolCompareUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ToolCompareInputService {
    public Object compareTechnicalCar(MultipartFile legacyReq, MultipartFile nxgReq) throws ParserConfigurationException, IOException, SAXException {
        File legacyFile = FileUtils.multipartToFile(legacyReq, "legacy.xml");
        File nxgFile = FileUtils.multipartToFile(nxgReq, "nxg.xml");

        Element legacyElement = getRootElement(legacyFile);
        Element nxgElement = getRootElement(nxgFile);

        TechnicalCarModel legacyTCModel = parseToTechnicalCarModel(legacyElement, "");
        TechnicalCarModel nxgTCModel = parseToTechnicalCarModel(nxgElement, "");

        boolean isSameStructure = ToolCompareUtils.compareStructure(legacyTCModel, nxgTCModel);
        if(isSameStructure) {
            List<CompareInputLineModel> compareInputLineModels = ToolCompareUtils.listOutInputTechnicalCar(legacyTCModel, nxgTCModel);
            byte[] excelFile = dataToExcel(compareInputLineModels);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "compare.xlsx");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
        } else {
            ApiResponse<List<CompareInputLineModel>> res;
            res = new ApiResponse<>(HttpStatus.BAD_REQUEST);
            res.setErrorMessage("Structure isn't same");
            res.setData(null);
            return res;
        }
    }


    private Element getRootElement(File fileXML) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fileXML);
        doc.getDocumentElement().normalize();

        return doc.getDocumentElement();
    }

    private TechnicalCarModel parseToTechnicalCarModel(Element element, String parentProduct) {
        TechnicalCarModel technicalCarModel = new TechnicalCarModel();
        technicalCarModel.setProduct(element.getTagName());
        String productFullParent = (parentProduct.isEmpty() ? "" : parentProduct + ".") + element.getTagName();
        technicalCarModel.setProductFullParent(productFullParent);
        NodeList childNodes = element.getChildNodes();
        List<TechnicalCarModel> children = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementOfNode = ((Element) node);
                String ATTRIBUTES_TAG = "Attributes";
                if(elementOfNode.getTagName().equals(ATTRIBUTES_TAG)) {
                    NamedNodeMap attributes = node.getAttributes();
                    Map<String, String> attributesMap = new LinkedHashMap<>();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attr = attributes.item(j);
                        attributesMap.put(attr.getNodeName(), attr.getNodeValue());
                    }
                    String SERIAL_NO_KEY = "CONFIGURATION_SERIAL_NO";
                    String serialNo = attributesMap.get(SERIAL_NO_KEY);
                    technicalCarModel.setSerialNo(serialNo);
                    technicalCarModel.setAttributes(attributesMap);
                } else {
                    children.add(parseToTechnicalCarModel(elementOfNode, productFullParent));
                }
            }
        }
        technicalCarModel.setChildrens(children);

        return technicalCarModel;
    }

    private byte[] dataToExcel(List<CompareInputLineModel> inputsLine) throws IOException {
        final String SHEET_NAME = "Compare Inputs";
        final String SHEET_NAME_ERROR = "Error Inputs";
        inputsLine = inputsLine.stream().map(CompareInputLineModel::getNewObjectWidthLevel).toList(); // format data
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Sheet compare inputs
            Sheet sheet = createDefaultSheetColumn(workbook, SHEET_NAME);
            int rowIndex = 1; // Index 0 is created for header
            CellStyle cellStyleError = ExcelUtils.getCellStyleError(workbook);
            for (CompareInputLineModel inputLine : inputsLine) {
                Row dataRow = sheet.createRow(rowIndex);
                rowIndex++;
                int cellCount = 0;
                Cell productCell = ExcelUtils.setText(dataRow, cellCount++,
                        inputLine.isHeader() ? inputLine.getProductFullParent() : inputLine.getProduct());
                Cell legacySerialNoCell = ExcelUtils.setText(dataRow, cellCount++, inputLine.getLegacySerialNo());
                Cell nxgSerialNoCell = ExcelUtils.setText(dataRow, cellCount++, inputLine.getNxgSerialNo());
                Cell legacyInputNameCell = ExcelUtils.setText(dataRow, cellCount++, inputLine.getLegacyInputName());
                Cell legacyInputValueCell = ExcelUtils.setText(dataRow, cellCount++, inputLine.getLegacyInputValue());
                Cell nxgInputNameCell = ExcelUtils.setText(dataRow, cellCount++, inputLine.getNxgInputName());
                Cell ngxInputValueCell = ExcelUtils.setText(dataRow, cellCount, inputLine.getNxgInputValue());

                boolean isSameValue = inputLine.isSameInputLegacyNxg();
                if(!isSameValue){
                    productCell.setCellStyle(cellStyleError);
                    legacySerialNoCell.setCellStyle(cellStyleError);
                    nxgSerialNoCell.setCellStyle(cellStyleError);
                    legacyInputNameCell.setCellStyle(cellStyleError);
                    legacyInputValueCell.setCellStyle(cellStyleError);
                    nxgInputNameCell.setCellStyle(cellStyleError);
                    ngxInputValueCell.setCellStyle(cellStyleError);
                }
            }
            // Sheet error inputs
            Sheet sheetError = createDefaultSheetColumn(workbook, SHEET_NAME_ERROR);
            List<CompareInputLineModel> inputsError = inputsLine.stream().filter(
                    input -> !input.isSameInputLegacyNxg()
            ).toList();
            int rowIndexSheetError = 1; // Index 0 is created for header
            for (CompareInputLineModel inputLine : inputsError) {
                Row dataRow = sheetError.createRow(rowIndexSheetError);
                rowIndexSheetError++;
                int cellCount = 0;
                ExcelUtils.setText(dataRow, cellCount++, inputLine.getProductFullParent());
                ExcelUtils.setText(dataRow, cellCount++, inputLine.getLegacySerialNo());
                ExcelUtils.setText(dataRow, cellCount++, inputLine.getNxgSerialNo());
                ExcelUtils.setText(dataRow, cellCount++, inputLine.getLegacyInputName());
                ExcelUtils.setText(dataRow, cellCount++, inputLine.getLegacyInputValue());
                ExcelUtils.setText(dataRow, cellCount++, inputLine.getNxgInputName());
                ExcelUtils.setText(dataRow, cellCount, inputLine.getNxgInputValue());

            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private Sheet createDefaultSheetColumn(Workbook workbook, String sheetName) {
        final String[] HEADERS_TOOL_COMPARE = {
                "Product", "Legacy Serial No", "Nxg Serial No", "Legacy Input Name", "Legacy Input Value",
                "Nxg Input Name", "Nxg Input Value"
        };

        Sheet sheet = workbook.createSheet(sheetName);
        int countColumn = 0;
        sheet.setColumnWidth(countColumn++, 35 * 256);
        sheet.setColumnWidth(countColumn++, 20 * 256);
        sheet.setColumnWidth(countColumn++, 30 * 256);
        sheet.setColumnWidth(countColumn++, 30 * 256);
        sheet.setColumnWidth(countColumn++, 30 * 256);
        sheet.setColumnWidth(countColumn++, 30 * 256);
        sheet.setColumnWidth(countColumn, 30 * 256);

        Row row = sheet.createRow(0);
        CellStyle cellStyleHeader = ExcelUtils.getCellStyleHeader(workbook);
        for (int i = 0; i < HEADERS_TOOL_COMPARE.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(HEADERS_TOOL_COMPARE[i]);
            cell.setCellStyle(cellStyleHeader);
        }
        return sheet;
    }

}
