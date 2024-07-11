package com.tannv.jobhunter.service;

import com.tannv.jobhunter.domain.Skill;
import com.tannv.jobhunter.domain.response.ResultPaginationDTO;
import com.tannv.jobhunter.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill saveSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public boolean existByName(String name) {
        return this.skillRepository.existsByName(name);
    }

    public void deleteSkill(Long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.orElse(null);
        if(currentSkill != null) {
            currentSkill.getJobs().forEach(job -> {
                job.getSkills().remove(currentSkill);
            });
            this.skillRepository.delete(currentSkill);
        }
    }

    public ResultPaginationDTO getAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageSkill.getSize());

        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageSkill.getContent());
        return rs;
    }

    public Skill getSkillById(Long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        return skillOptional.orElse(null);
    }
}
