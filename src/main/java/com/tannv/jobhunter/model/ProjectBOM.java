package com.tannv.jobhunter.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProjectBOM {
    private String product;
    private String productFullParent;
    private Map<String, String> attributes;
    private List<ProjectBOM> childrens;
}
