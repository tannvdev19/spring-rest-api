package com.tannv.jobhunter.domain.response.job;

import com.tannv.jobhunter.util.constant.LevelEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Length;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResCreateJobDTO {
    private Long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean active;

    private List<String> skills;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
}
