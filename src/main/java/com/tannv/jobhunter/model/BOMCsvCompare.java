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
public class BOMCsvCompare {
    private String currentLevel;
    private BOMCsvItemModel legacy;
    private BOMCsvItemModel nxg;
    private String whatAreDifferent;

    public BOMCsvCompare getNewObjectWidthLevel() {
        BOMCsvCompare model = new BOMCsvCompare();
        int spacing = Integer.parseInt(getCurrentLevel()) * 4;
        model.setCurrentLevel(StringUtils.indentationString(spacing, getCurrentLevel()));
        model.setLegacy(this.getLegacy().getNewObjectWithLevel());
        model.setNxg(this.getNxg().getNewObjectWithLevel());
        model.setWhatAreDifferent(this.getWhatAreDifferent());
        return model;
    }
}
