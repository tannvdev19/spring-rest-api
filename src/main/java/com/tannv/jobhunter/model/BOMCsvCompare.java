package com.tannv.jobhunter.model;

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
}
