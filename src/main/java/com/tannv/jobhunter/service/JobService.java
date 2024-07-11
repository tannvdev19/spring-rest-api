package com.tannv.jobhunter.service;

import com.tannv.jobhunter.domain.Job;
import com.tannv.jobhunter.domain.Skill;
import com.tannv.jobhunter.domain.response.ResultPaginationDTO;
import com.tannv.jobhunter.domain.response.job.ResCreateJobDTO;
import com.tannv.jobhunter.domain.response.job.ResUpdateJobDTO;
import com.tannv.jobhunter.repository.JobRepository;
import com.tannv.jobhunter.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO createJob(Job job) {
        // Verify skill
        if(job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }
        Job currentJob = this.jobRepository.save(job);
        return convertToResCreateJobDTO(currentJob);
    }

    public ResUpdateJobDTO updateJob(Job job) {
        // Verify skill
        if(job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }
        Job currentJob = this.jobRepository.save(job);
        return convertToResUpdateJobDTO(currentJob);
    }

    public Optional<Job> getJobById(Long id) {
        return this.jobRepository.findById(id);
    }

    public ResultPaginationDTO getAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageJob.getSize());

        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageJob.getContent());
        return rs;
    }

    public void deleteJob(Long id) {
        this.jobRepository.deleteById(id);
    }

    public ResCreateJobDTO convertToResCreateJobDTO(Job job) {
        ResCreateJobDTO resCreateJobDTO = new ResCreateJobDTO();
        resCreateJobDTO.setId(job.getId());
        resCreateJobDTO.setName(job.getName());
        resCreateJobDTO.setSalary(job.getSalary());
        resCreateJobDTO.setQuantity(job.getQuantity());
        resCreateJobDTO.setLocation(job.getLocation());
        resCreateJobDTO.setDescription(job.getDescription());
        resCreateJobDTO.setLevel(job.getLevel());
        resCreateJobDTO.setStartDate(job.getStartDate());
        resCreateJobDTO.setEndDate(job.getEndDate());
        resCreateJobDTO.setActive(job.isActive());
        resCreateJobDTO.setCreatedAt(job.getCreatedAt());
        resCreateJobDTO.setUpdatedAt(job.getUpdatedAt());
        resCreateJobDTO.setCreatedBy(job.getCreatedBy());
        resCreateJobDTO.setUpdatedBy(job.getUpdatedBy());

        if(job.getSkills() != null) {
            List<String> skills = job.getSkills().stream().map(Skill::getName).toList();
            resCreateJobDTO.setSkills(skills);
        }
        return resCreateJobDTO;
    }

    public ResUpdateJobDTO convertToResUpdateJobDTO(Job job) {
        ResUpdateJobDTO resUpdateJobDTO = new ResUpdateJobDTO();
        resUpdateJobDTO.setId(job.getId());
        resUpdateJobDTO.setName(job.getName());
        resUpdateJobDTO.setSalary(job.getSalary());
        resUpdateJobDTO.setQuantity(job.getQuantity());
        resUpdateJobDTO.setLocation(job.getLocation());
        resUpdateJobDTO.setLevel(job.getLevel());
        resUpdateJobDTO.setDescription(job.getDescription());
        resUpdateJobDTO.setStartDate(job.getStartDate());
        resUpdateJobDTO.setEndDate(job.getEndDate());
        resUpdateJobDTO.setActive(job.isActive());
        resUpdateJobDTO.setCreatedAt(job.getCreatedAt());
        resUpdateJobDTO.setUpdatedAt(job.getUpdatedAt());
        resUpdateJobDTO.setCreatedBy(job.getCreatedBy());
        resUpdateJobDTO.setUpdatedBy(job.getUpdatedBy());

        if(job.getSkills() != null) {
            List<String> skills = job.getSkills().stream().map(Skill::getName).toList();
            resUpdateJobDTO.setSkills(skills);
        }
        return resUpdateJobDTO;
    }
}
