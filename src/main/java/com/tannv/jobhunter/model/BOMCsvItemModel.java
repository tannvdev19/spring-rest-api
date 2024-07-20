package com.tannv.jobhunter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BOMCsvItemModel {
    private String level;
    private String item;
    private String description;
    private String qty;
    private String packingDOKAR;
    private String packingDOKNR;
    private String labelDOKAR;
    private String labelDOKNR;
    private String drawId;
    private String genericItem;
    private String fQty;
    private String bom2;
    private String sapMaterialId;
    private String sapTemplate;
    private String blue;
    private String bBlock;
    private String bBType;
    private String smartPart;

    public BOMCsvItemModel(List<String> rowData) {
        this.level = rowData.get(0);
        this.item = rowData.get(1);
        this.description = rowData.get(2);
        this.qty = rowData.get(3);
        this.packingDOKAR = rowData.get(4);
        this.packingDOKNR = rowData.get(5);
        this.labelDOKAR = rowData.get(6);
        this.labelDOKNR = rowData.get(7);
        this.drawId = rowData.get(8);
        this.genericItem = rowData.get(9);
        this.fQty = rowData.get(10);
        this.bom2 = rowData.get(11);
        this.sapMaterialId = rowData.get(12);
        this.sapTemplate = rowData.get(13);
        this.blue = rowData.get(14);
        this.bBlock = rowData.get(15);
        this.bBType = rowData.get(16);
        this.smartPart = rowData.get(17);
    }

    @Override
    public String toString() {
        return "BOMCsvItemModel{" +
                "level='" + level + '\'' +
                ", item='" + item + '\'' +
                ", description='" + description + '\'' +
                ", qty='" + qty + '\'' +
                ", packingDOKAR='" + packingDOKAR + '\'' +
                ", packingDOKNR='" + packingDOKNR + '\'' +
                ", labelDOKAR='" + labelDOKAR + '\'' +
                ", labelDOKNR='" + labelDOKNR + '\'' +
                ", drawId='" + drawId + '\'' +
                ", genericItem='" + genericItem + '\'' +
                ", fQty='" + fQty + '\'' +
                ", bom2='" + bom2 + '\'' +
                ", sapMaterialId='" + sapMaterialId + '\'' +
                ", sapTemplate='" + sapTemplate + '\'' +
                ", blue='" + blue + '\'' +
                ", bBlock='" + bBlock + '\'' +
                ", bBType='" + bBType + '\'' +
                ", smartPart='" + smartPart + '\'' +
                '}';
    }
}
