package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.domain.Job;
import com.tannv.jobhunter.domain.response.ResultPaginationDTO;
import com.tannv.jobhunter.domain.response.job.ResCreateJobDTO;
import com.tannv.jobhunter.domain.response.job.ResUpdateJobDTO;
import com.tannv.jobhunter.service.JobService;
import com.tannv.jobhunter.util.anotation.ApiMessage;
import com.tannv.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    @ApiMessage("Get jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(
            @Filter Specification<Job> spec,
            Pageable pageable
            ) {
        ResultPaginationDTO resultPaginationDTO = this.jobService.getAllJobs(spec, pageable);
        return ResponseEntity.ok(resultPaginationDTO);
    }

    @PostMapping
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job) {
        ResCreateJobDTO resCreateJobDTO = this.jobService.createJob(job);
        return ResponseEntity.status(HttpStatus.CREATED).body(resCreateJobDTO);
    }

    @PutMapping
    @ApiMessage("Create a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.getJobById(job.getId());
        if(currentJob.isEmpty()) {
            throw new IdInvalidException("Job not found");
        }
        ResUpdateJobDTO resCreateJobDTO = this.jobService.updateJob(job);
        return ResponseEntity.status(HttpStatus.CREATED).body(resCreateJobDTO);
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete job")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.getJobById(id);
        if(currentJob.isEmpty()) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.deleteJob(id);
        return ResponseEntity.ok(null);
    }
}
