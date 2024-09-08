package com.tannv.jobhunter.model;

import com.tannv.jobhunter.util.excel.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompareInputLineModel {
    private String product;
    private String productFullParent;
    private String legacySerialNo;
    private String nxgSerialNo;
    private String legacyInputName;
    private String legacyInputValue;
    private String nxgInputName;
    private String nxgInputValue;

    public boolean isSameInputLegacyNxg() {
        return this.getLegacyInputValue().equals(this.getNxgInputValue());
    }

    public boolean isHeader() {
        return this.getLegacyInputName().isBlank() && this.getNxgInputName().isBlank();
    }

    public int getCurrentLevel() {
        String[] parts = this.getProductFullParent().split("\\.");
        return parts.length - 1;
    }

    public CompareInputLineModel getNewObjectWidthLevel() {
        CompareInputLineModel model = new CompareInputLineModel();
        int currentLevel = getCurrentLevel() * 4;
        model.setProduct(StringUtils.indentationString(currentLevel, this.getProduct()));
        model.setProductFullParent(this.getProductFullParent());
        model.setLegacySerialNo((StringUtils.indentationString(currentLevel, this.getLegacySerialNo())));
        model.setNxgSerialNo(StringUtils.indentationString(currentLevel, this.getNxgSerialNo()));
        model.setLegacyInputName(StringUtils.indentationString(currentLevel, this.getLegacyInputName()));
        model.setLegacyInputValue(StringUtils.indentationString(currentLevel, this.getLegacyInputValue()));
        model.setNxgInputName(StringUtils.indentationString(currentLevel, this.getNxgInputName()));
        model.setNxgInputValue(StringUtils.indentationString(currentLevel, this.getNxgInputValue()));
        return model;
    }
}