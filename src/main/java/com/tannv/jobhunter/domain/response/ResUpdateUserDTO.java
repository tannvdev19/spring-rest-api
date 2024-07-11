package com.tannv.jobhunter.domain.response;

import com.tannv.jobhunter.util.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private Long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private CompanyUser company;
    @Getter
    @Setter
    public static class CompanyUser {
        private Long id;
        private String name;
    }
}
