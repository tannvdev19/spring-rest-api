package com.tannv.jobhunter.domain;

import com.tannv.jobhunter.util.SecurityUtil;
import com.tannv.jobhunter.util.constant.GenderEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Length;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    private String email;

    private String name;

    @NotBlank(message = "Password is required")
    private String password;

    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;

    @Column(length = Length.LONG)
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
