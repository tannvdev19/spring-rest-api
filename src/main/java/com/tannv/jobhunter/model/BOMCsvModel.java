package com.tannv.jobhunter.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BOMCsvModel {
    private BOMCsvItemModel bomCsvItemModel;
    private List<BOMCsvModel> childrens;

    public BOMCsvModel(BOMCsvItemModel bomCsvItemModel) {
        this.bomCsvItemModel = bomCsvItemModel;
        this.childrens = new ArrayList<>();
    }


    public void addChild(BOMCsvModel child) {
        this.childrens.add(child);
    }
}