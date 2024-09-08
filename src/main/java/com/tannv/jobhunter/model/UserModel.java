package com.tannv.jobhunter.model;

import com.tannv.jobhunter.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private Long id;
    private String email;
    private String name;
    private String password;
    private int age;
    private GenderEnum gender;
    private String address;
    private String refreshToken;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private CompanyModel company;
}
