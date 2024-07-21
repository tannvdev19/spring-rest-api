package com.tannv.jobhunter.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TechnicalCarModel {
    private String product;
    private String productFullParent;
    private String serialNo;
    private Map<String, String> attributes;
    private List<TechnicalCarModel> childrens;
}