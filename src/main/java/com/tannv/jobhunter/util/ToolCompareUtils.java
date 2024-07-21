package com.tannv.jobhunter.util;

import com.tannv.jobhunter.model.CompareInputLineModel;
import com.tannv.jobhunter.model.TechnicalCarModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ToolCompareUtils {
    public static TechnicalCarModel findBySerialNo(TechnicalCarModel technicalCarModel, String serialNo) {
        if (technicalCarModel == null) {
            return null;
        }

        if (serialNo.equals(technicalCarModel.getSerialNo())) {
            return technicalCarModel;
        }

        if (technicalCarModel.getChildrens() != null) {
            for (TechnicalCarModel child : technicalCarModel.getChildrens()) {
                TechnicalCarModel result = findBySerialNo(child, serialNo);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    public static boolean compareStructure(TechnicalCarModel legacy, TechnicalCarModel nxg) {
        // Base case: if both objects are null, they are equal
        if (legacy == null && nxg == null) {
            return true;
        }

        // If only one of the objects is null, they are not equal
        if ( legacy == null || nxg == null) {
            return false;
        }

        // Check if the products are not the same
        if (!legacy.getProduct().equals(nxg.getProduct())) {
            return false;
        }

        // Check if the sizes of children lists are different
        if (legacy.getChildrens().size() != nxg.getChildrens().size()) {
            return false;
        }

        // Recursively compare children lists
        for (int i = 0; i < legacy.getChildrens().size(); i++) {
            TechnicalCarModel child1 = legacy.getChildrens().get(i);
            TechnicalCarModel child2 = nxg.getChildrens().get(i);
            if (!compareStructure(child1, child2)) {
                return false;
            }
        }

        // If all checks pass, objects are considered equal
        return true;
    }

    public static List<CompareInputLineModel> listOutInputTechnicalCar(TechnicalCarModel legacy, TechnicalCarModel nxg) {
        List<CompareInputLineModel> inputs = new ArrayList<>();
        compareTechnicalCars(legacy, nxg, inputs);
        return inputs;
    }

    private static void compareTechnicalCars(TechnicalCarModel legacy, TechnicalCarModel nxg, List<CompareInputLineModel> inputs) {
        if (legacy == null && nxg == null) {
            return;
        }

        if (legacy != null && nxg != null) {
            CompareInputLineModel inputLineHeader = new CompareInputLineModel(
                    legacy.getProduct(), legacy.getProductFullParent(), legacy.getSerialNo(),
                    "", "", "" ,""
            );
            inputs.add(inputLineHeader);

            Map<String, String> legacyAttributes = legacy.getAttributes() != null ? legacy.getAttributes() : new LinkedHashMap<>();
            Map<String, String> nxgAttributes = nxg.getAttributes() != null ? nxg.getAttributes() : new LinkedHashMap<>();

            for (String key : legacyAttributes.keySet()) {
                String legacyValue = legacyAttributes.get(key);
                String nxgValue = nxgAttributes.getOrDefault(key, null);

                CompareInputLineModel inputLine = new CompareInputLineModel();
                inputLine.setProduct(legacy.getProduct());
                inputLine.setProductFullParent(legacy.getProductFullParent());
                inputLine.setSerialNo(legacy.getSerialNo());
                inputLine.setLegacyInputName(key);
                inputLine.setLegacyInputValue(legacyValue);
                if(nxgValue == null) {
                    inputLine.setNxgInputName("");
                    inputLine.setNxgInputValue("");
                } else {
                    inputLine.setNxgInputName(key);
                    inputLine.setNxgInputValue(nxgValue);
                }
                inputs.add(inputLine);
            }

            for (String key : nxgAttributes.keySet()) {
                if (!legacyAttributes.containsKey(key)) {
                    String nxgValue = nxgAttributes.get(key);
                    CompareInputLineModel inputLine = new CompareInputLineModel();
                    inputLine.setProduct(nxg.getProduct());
                    inputLine.setProductFullParent(nxg.getProductFullParent());
                    inputLine.setSerialNo(nxg.getSerialNo());
                    inputLine.setLegacyInputName("");
                    inputLine.setLegacyInputValue("");
                    inputLine.setNxgInputName(key);
                    inputLine.setNxgInputValue(nxgValue);
                    inputs.add(inputLine);
                }
            }

            List<TechnicalCarModel> legacyChildren = legacy.getChildrens() != null ? legacy.getChildrens() : new ArrayList<>();
            List<TechnicalCarModel> nxgChildren = nxg.getChildrens() != null ? nxg.getChildrens() : new ArrayList<>();

            int maxSize = Math.max(legacyChildren.size(), nxgChildren.size());

            for (int i = 0; i < maxSize; i++) {
                TechnicalCarModel legacyChild = i < legacyChildren.size() ? legacyChildren.get(i) : null;
                TechnicalCarModel nxgChild = i < nxgChildren.size() ? nxgChildren.get(i) : null;
                compareTechnicalCars(legacyChild, nxgChild, inputs);
            }
        } else if (legacy != null) {
            CompareInputLineModel inputLineHeaderLegacy = new CompareInputLineModel(
                    legacy.getProduct(), legacy.getProductFullParent(), legacy.getSerialNo(),
                    "", "", "", ""
            );
            inputs.add(inputLineHeaderLegacy);

            for (Map.Entry<String, String> entry : legacy.getAttributes().entrySet()) {
                CompareInputLineModel inputLineLegacy = new CompareInputLineModel();
                inputLineLegacy.setProduct(legacy.getProduct());
                inputLineLegacy.setProductFullParent(legacy.getProductFullParent());
                inputLineLegacy.setSerialNo(legacy.getSerialNo());
                inputLineLegacy.setLegacyInputName(entry.getKey());
                inputLineLegacy.setLegacyInputValue(entry.getValue());
                inputLineLegacy.setNxgInputName("");
                inputLineLegacy.setNxgInputValue("");
                inputs.add(inputLineLegacy);
            }

            for (TechnicalCarModel child : legacy.getChildrens()) {
                compareTechnicalCars(child, null, inputs);
            }
        } else {
            CompareInputLineModel inputLineHeaderNxg = new CompareInputLineModel();
            inputLineHeaderNxg.setProduct(nxg.getProduct());
            inputLineHeaderNxg.setProductFullParent(nxg.getProductFullParent());
            inputLineHeaderNxg.setSerialNo(nxg.getSerialNo());
            inputs.add(inputLineHeaderNxg);

            for (Map.Entry<String, String> entry : nxg.getAttributes().entrySet()) {
                CompareInputLineModel inputLineNxg = new CompareInputLineModel();
                inputLineNxg.setProduct(nxg.getProduct());
                inputLineNxg.setProductFullParent(nxg.getProductFullParent());
                inputLineNxg.setSerialNo(nxg.getSerialNo());
                inputLineNxg.setLegacyInputName("");
                inputLineNxg.setLegacyInputValue("");
                inputLineNxg.setNxgInputName(entry.getKey());
                inputLineNxg.setNxgInputValue(entry.getValue());
                inputs.add(inputLineNxg);
            }

            for (TechnicalCarModel child : nxg.getChildrens()) {
                compareTechnicalCars(child, null, inputs);
            }
        }
    }
}