package com.tannv.jobhunter.model.excel;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExcelColumn {
    private String header;
    private int columnNumber;
    private List<String> data;
}
