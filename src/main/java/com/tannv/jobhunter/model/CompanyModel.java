package com.tannv.jobhunter.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Length;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyModel {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String logo;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

}
