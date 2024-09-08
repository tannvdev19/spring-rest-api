package com.tannv.jobhunter.service;

import com.tannv.jobhunter.model.BOMCsvCompare;
import com.tannv.jobhunter.model.BOMCsvItemModel;
import com.tannv.jobhunter.model.BOMCsvModel;
import com.tannv.jobhunter.util.CsvUtils;
import com.tannv.jobhunter.util.ExcelUtils;
import com.tannv.jobhunter.util.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class ToolCompareOutputService {
    public Object compareBomCsvModel(MultipartFile legacyReq, MultipartFile nxgReq) throws IOException {
        File legacyFile = FileUtils.multipartToFile(legacyReq, "legacy.csv");
        File nxgFile = FileUtils.multipartToFile(nxgReq, "nxg.csv");

        BOMCsvModel legacyBom = parseToBomCsvModel(legacyFile);
        BOMCsvModel nxgBom = parseToBomCsvModel(nxgFile);

        List<BOMCsvCompare> compareResult = compareBOMCsvModels(legacyBom, nxgBom);
        byte[] excelFile = dataToExcel(compareResult);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "compare.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile);
    }

    public BOMCsvModel parseToBomCsvModel(File file) throws FileNotFoundException {
        List<BOMCsvItemModel> scannedData = scanData(file);
        return parseBOMCsvItems(scannedData);
    }

    public List<BOMCsvItemModel> scanData(File file) throws FileNotFoundException {
        List<List<String>> scannedData = CsvUtils.scanFile(file);
        List<BOMCsvItemModel> items = new ArrayList<>();
        for (List<String> item : scannedData) {
            BOMCsvItemModel bomCsvItemModel = new BOMCsvItemModel(item);
            items.add(bomCsvItemModel);
        }
        items.remove(0); // remove header
        items.get(0).setLevel("0"); // first item level is value "TOP", set "0" to parse int
        return items;
    }

    public BOMCsvModel parseBOMCsvItems(List<BOMCsvItemModel> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        Stack<BOMCsvModel> stack = new Stack<>();
        BOMCsvModel root = new BOMCsvModel();
        root.setBomCsvItemModel(items.get(0));
        stack.push(root);

        for (int i = 1; i < items.size(); i++) {
            BOMCsvModel currentModel = new BOMCsvModel(items.get(i));

            int currentLevel = Integer.parseInt(items.get(i).getLevel());
            while (!stack.isEmpty() && Integer.parseInt(stack.peek().getBomCsvItemModel().getLevel()) >= currentLevel) {
                stack.pop();
            }

            if (!stack.isEmpty()) {
                stack.peek().getChildrens().add(currentModel);
            }

            stack.push(currentModel);
        }

        return root;
    }

    // Method for debugging
    public void printBOMCsvModel(BOMCsvModel model, int indent) {
        if (model == null) return;

        // Print the current item with the appropriate indentation
        String indentation = " ".repeat(indent);
        BOMCsvItemModel item = model.getBomCsvItemModel();
        System.out.println(indentation + "Level: " + item.getLevel() + ", Item: " + item.getItem());
        // Recursively print the children
        for (BOMCsvModel child : model.getChildrens()) {
            printBOMCsvModel(child, indent + 2);
        }
    }


    public List<BOMCsvCompare> compareBOMCsvModels(BOMCsvModel legacyModel, BOMCsvModel nxgModel) {
        List<BOMCsvCompare> differences = new ArrayList<>();
        compareBOMCsvModelsRecursively(legacyModel, nxgModel, differences);
        return differences;
    }

    private void compareBOMCsvModelsRecursively(BOMCsvModel legacyModel, BOMCsvModel nxgModel, List<BOMCsvCompare> compares) {
        BOMCsvCompare compare = compareBOMCsvItemModels(legacyModel, nxgModel);
        compares.add(compare);

        List<BOMCsvModel> legacyChildren = legacyModel.getChildrens();
        List<BOMCsvModel> nxgChildren = nxgModel.getChildrens();

        int legacyChildrenSize = legacyChildren.size();
        int nxgChildrenSize = nxgChildren.size();
        boolean isErrorParent = !compare.getWhatAreDifferent().isEmpty(); // If have message, the parent have an error -> Not need to compare in children

        if(legacyChildrenSize > 0 && nxgChildrenSize > 0 && !isErrorParent) {
            int max = legacyChildren.size();
            for (int i = 0; i < max; i++) {
                BOMCsvModel legacyChild = legacyChildren.get(i);
                BOMCsvModel nxgChild = nxgChildren.get(i) ;

                if (legacyChild != null && nxgChild != null) {
                    compareBOMCsvModelsRecursively(legacyChild, nxgChild, compares);
                }
            }
        }

    }

    private BOMCsvCompare compareBOMCsvItemModels(BOMCsvModel legacy, BOMCsvModel nxg) {
        BOMCsvCompare result;

        BOMCsvItemModel legacyItem = legacy.getBomCsvItemModel();
        BOMCsvItemModel nxgItem = nxg.getBomCsvItemModel();

        String currentLevel = legacyItem != null ? legacyItem.getLevel() : nxgItem.getLevel();
        StringBuilder whatAreDifferent = new StringBuilder();

        if (legacyItem != null && nxgItem != null) {
            // Compare size of children
            List<BOMCsvModel> legacyChildren = legacy.getChildrens();
            List<BOMCsvModel> nxgChildren = nxg.getChildrens();

            int legacyChildrenSize = legacyChildren.size();
            int nxgChildrenSize = nxgChildren.size();

            if(legacyChildrenSize > 0 && nxgChildrenSize > 0) {
                if(legacyChildrenSize != nxgChildrenSize) {
                    whatAreDifferent.append("Children isn't size same ")
                            .append(legacyChildrenSize).append(" vs ").append(nxgChildrenSize).append("\n");
                }
            }

            // Use reflection to get all fields of the BOMCsvItemModel class
            Field[] fields = BOMCsvItemModel.class.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true); // Allow access to private fields
                try {
                    Object legacyValue = field.get(legacyItem);
                    Object nxgValue = field.get(nxgItem);

                    if (!Objects.equals(legacyValue, nxgValue)) {
                        whatAreDifferent.append(field.getName()).append(": ")
                                .append(legacyValue).append(" vs ").append(nxgValue).append("\n");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace(); // Handle exception properly in production code
                }
            }


        } else {
            whatAreDifferent.append(legacyItem == null ? "Missing in Legacy" : "Missing in NXG");
        }
        result = new BOMCsvCompare(currentLevel, legacyItem, nxgItem, whatAreDifferent.toString());
        return result;
    }

    private byte[] dataToExcel(List<BOMCsvCompare> compareLines) throws IOException {
        final String SHEET_NAME = "Compare Inputs";
        final String SHEET_NAME_ERROR = "Error Inputs";
        compareLines = compareLines.stream().map(BOMCsvCompare::getNewObjectWidthLevel).toList();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Sheet compare inputs
            Sheet sheet = createDefaultSheetColumn(workbook, SHEET_NAME);
            int rowIndex = 1; // Index 0 is created for header
            CellStyle cellStyleError = ExcelUtils.getCellStyleError(workbook);
            for (BOMCsvCompare compareLine : compareLines) {
                Row dataRow = sheet.createRow(rowIndex);
                rowIndex++;

                int countCell = 0;
                Cell levelCell = ExcelUtils.setText(dataRow, countCell++, compareLine.getCurrentLevel());
                Cell legacyItem = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getItem());
                Cell nxgItem = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getItem());
                Cell legacyDescription = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getDescription());
                Cell nxgDescription = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getDescription());
                Cell legacyQty = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getQty());
                Cell nxgQty = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getQty());
                Cell legacyPackingDOKAR = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getPackingDOKAR());
                Cell nxgPackingDOKAR = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getPackingDOKAR());
                Cell legacyPackingDOKNR = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getPackingDOKNR());
                Cell nxgPackingDOKNR = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getPackingDOKNR());
                Cell legacyLabelDOKAR = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getLabelDOKAR());
                Cell nxgLabelDOKAR = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getLabelDOKAR());
                Cell legacyLabelDOKNR = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getLabelDOKNR());
                Cell nxgLabelDOKNR = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getLabelDOKNR());
                Cell legacyDrawId = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getDrawId());
                Cell nxgDrawId = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getDrawId());
                Cell legacyGenericItem = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getGenericItem());
                Cell nxgGenericItem = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getGenericItem());
                Cell legacyFQty = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getFQty());
                Cell nxgFQty = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getFQty());
                Cell legacy2Bom = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getBom2());
                Cell nxg2Bom = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getBom2());
                Cell legacySAPMaterialId = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getSapMaterialId());
                Cell nxgSAPMaterialId = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getSapMaterialId());
                Cell legacySAPTemplate = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getSapTemplate());
                Cell nxgSAPTemplate = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getSapTemplate());
                Cell legacyBlue = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getBlue());
                Cell nxgBlue = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getBlue());
                Cell legacyBblock = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getBBlock());
                Cell nxgBblock = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getBBlock());
                Cell legacyBBtype = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getBBType());
                Cell nxgBBtype = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getBBType());
                Cell legacySmartPart = ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getSmartPart());
                Cell nxgSmartPart = ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getSmartPart());
                Cell different = ExcelUtils.setText(dataRow, countCell, compareLine.getWhatAreDifferent());

                boolean isNotSame = !compareLine.getWhatAreDifferent().isEmpty();
                if(isNotSame){
                    List<Cell> cells = Arrays.asList(levelCell, legacyItem, nxgItem, legacyDescription, nxgDescription,
                            legacyQty, nxgQty, legacyPackingDOKAR, nxgPackingDOKAR, legacyPackingDOKNR, nxgPackingDOKNR,
                            legacyLabelDOKAR, nxgLabelDOKAR, legacyLabelDOKNR, nxgLabelDOKNR, legacyDrawId, nxgDrawId,
                            legacyGenericItem, nxgGenericItem, legacyFQty, nxgFQty, legacy2Bom, nxg2Bom, legacySAPMaterialId, nxgSAPMaterialId,
                            legacySAPTemplate, nxgSAPTemplate, legacyBlue, nxgBlue, legacyBblock, nxgBblock, legacyBBtype, nxgBBtype,
                            legacySmartPart, nxgSmartPart);
                    setRowError(cellStyleError, cells);
                }
            }
            // Sheet error inputs
            Sheet sheetError = createDefaultSheetColumn(workbook, SHEET_NAME_ERROR);
            List<BOMCsvCompare> comparesError = compareLines.stream().filter(
                    compare -> !compare.getWhatAreDifferent().isEmpty()
            ).toList();
            int rowIndexSheetError = 1; // Index 0 is created for header
            for (BOMCsvCompare compareLine : comparesError) {
                Row dataRow = sheetError.createRow(rowIndexSheetError);
                rowIndexSheetError++;

                int countCell = 0;
                ExcelUtils.setText(dataRow, countCell++, compareLine.getCurrentLevel());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getItem());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getItem());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getDescription());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getDescription());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getQty());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getQty());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getPackingDOKAR());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getPackingDOKAR());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getPackingDOKNR());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getPackingDOKNR());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getLabelDOKAR());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getLabelDOKAR());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getLabelDOKNR());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getLabelDOKNR());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getDrawId());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getDrawId());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getGenericItem());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getGenericItem());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getFQty());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getFQty());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getBom2());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getBom2());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getSapMaterialId());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getSapMaterialId());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getSapTemplate());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getSapTemplate());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getBlue());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getBlue());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getBBlock());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getBBlock());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getBBType());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getBBType());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getLegacy().getSmartPart());
                ExcelUtils.setText(dataRow, countCell++, compareLine.getNxg().getSmartPart());
                ExcelUtils.setText(dataRow, countCell, compareLine.getWhatAreDifferent());

            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void setRowError(CellStyle cellStyleError, List<Cell> cells) {
        for (Cell cell : cells) {
            cell.setCellStyle(cellStyleError);
        }
    }

    private Sheet createDefaultSheetColumn(Workbook workbook, String sheetName) {
        final String[] HEADERS_TOOL_COMPARE_OUTPUT = {
                "Level", "Legacy Item", "Nxg Item", "Legacy Description", "Nxg Description", "Legacy Qty", "Nxg Qty",
                "Legacy Packing DOKAR", "Nxg Packing DOKAR", "Legacy Packing DOKNR", "Nxg Packing DOKNR",
                "Legacy Label DOKAR", "Nxg Label DOKAR", "Legacy Label DOKNR", "Nxg Label DOKNR",
                "Legacy Draw ID", "Nxg Draw ID", "Legacy Generic Item", "Nxg Generic Item",
                "Legacy FQty", "Nxg FQty", "Legacy 2BOM", "Nxg 2BOM", "Legacy SAP Material ID", "Nxg SAP Material ID",
                "Legacy SAP Template", "Nxg SAP Template", "Legacy Blue", "Nxg Blue", "Legacy Bblock", "Nxg Bblock",
                "Legacy BBtype", "Nxg BBtype", "Legacy Smartpart", "Nxg Smartpart", "Different"
        };
        int defaultWidth = 20 * 256; // 20 characters

        Sheet sheet = workbook.createSheet(sheetName);
        for (int i = 0; i < 50; i++) {
            if(i == 0) {
                sheet.setColumnWidth(i, defaultWidth / 2);
            } else if (i == 5|| i == 6) {
                sheet.setColumnWidth(i, (defaultWidth * 2)/3);
            }
            else {
                sheet.setColumnWidth(i, defaultWidth);
            }
        }

        Row row = sheet.createRow(0);
        CellStyle cellStyleHeader = ExcelUtils.getCellStyleHeader(workbook);
        for (int i = 0; i < HEADERS_TOOL_COMPARE_OUTPUT.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(HEADERS_TOOL_COMPARE_OUTPUT[i]);
            cell.setCellStyle(cellStyleHeader);
        }
        return sheet;
    }
}
