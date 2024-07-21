package com.tannv.jobhunter.model;

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
    private String serialNo;
    private String legacyInputName;
    private String legacyInputValue;
    private String nxgInputName;
    private String nxgInputValue;

    public boolean isSameInputLegacyNxg() {
        return this.getLegacyInputValue().equals(this.getNxgInputValue());
    }

    public boolean isHeader() {
        return this.getLegacyInputName().isEmpty() && this.getNxgInputName().isEmpty();
    }
}