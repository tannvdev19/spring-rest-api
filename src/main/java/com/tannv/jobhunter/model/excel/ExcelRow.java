package com.tannv.jobhunter.model.excel;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelRow {
    private int rowNumber;
    private List<String> data;
}
