package com.tannv.jobhunter.model.excel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExcelSheet {
    private String sheetName;
    private List<String> headers = new ArrayList<>();
    private List<ExcelRow> data = new ArrayList<>();

    public ExcelRow getRow(int rowNumber) {
        return this.data.stream().filter(item -> item.getRowNumber() == rowNumber).findFirst().orElse(null);
    }

    public ExcelColumn getColumn(int columnNumber) {
        return ExcelColumn.builder()
                .header(this.headers.get(columnNumber))
                .columnNumber(columnNumber)
                .data(this.data.stream().map(item -> item.getData().get(columnNumber)).toList())
                .build();
    }

    public List<String> getColumnKeys() {
        int indexSeparateKey = this.headers.indexOf("END1");
        return this.headers.subList(0, indexSeparateKey);
    }

    public List<String> getColumnValues() {
        int indexSeparateKey = this.headers.indexOf("END1");
        return this.headers.subList(indexSeparateKey + 1, this.headers.size() - 1); // Remove end column = END2
    }
}
