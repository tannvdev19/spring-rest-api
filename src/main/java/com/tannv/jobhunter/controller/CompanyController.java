package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.domain.Company;
import com.tannv.jobhunter.domain.response.ResultPaginationDTO;
import com.tannv.jobhunter.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getCompany(
            Specification<Company> spec,
            Pageable pageable
    ) {
        ResultPaginationDTO companies = this.companyService.handleGet(spec, pageable);
        return ResponseEntity.ok(companies);
    }

    @PostMapping
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        Company entityCreated = this.companyService.handleCreate(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(entityCreated);
    }

    @PutMapping
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        Company entityUpdated = this.companyService.handleUpdate(company);
        return ResponseEntity.ok(entityUpdated);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") Long id) {
        this.companyService.handleDelete(id);
        return ResponseEntity.ok(null);
    }
}
