package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.domain.Skill;
import com.tannv.jobhunter.domain.response.ResultPaginationDTO;
import com.tannv.jobhunter.service.SkillService;
import com.tannv.jobhunter.util.anotation.ApiMessage;
import com.tannv.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    @ApiMessage("Get skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
            @Filter Specification<Skill> spec,
            Pageable pageable
            ) {
        ResultPaginationDTO resultPaginationDTO = this.skillService.getAllSkills(spec, pageable);
        return ResponseEntity.ok(resultPaginationDTO);
    }

    @GetMapping("/{id}")
    @ApiMessage("Get skill by id")
    public ResponseEntity<Skill> getId(@PathVariable("id") Long id) throws IdInvalidException {
        // Verify id
        Skill currentSkill = this.skillService.getSkillById(id);
        if(currentSkill == null)
            throw new IdInvalidException("Skill id = " + id + "isn't existed");
        return ResponseEntity.ok(currentSkill);
    }

    @PostMapping
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> create(@Valid @RequestBody Skill skill) throws IdInvalidException {
        // Verify name
        if(skill.getName() != null && this.skillService.existByName(skill.getName())) {
            throw new IdInvalidException("Skill name = " + skill.getName() + "is existed");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.saveSkill(skill));
    }

    @PutMapping
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> update(@Valid @RequestBody Skill skill) throws IdInvalidException {
        // Verify id
        Skill currentSkill = this.skillService.getSkillById(skill.getId());
        if(currentSkill == null)
            throw new IdInvalidException("Skill id = " + skill.getId() + "isn't existed");
        // Verify name
        if(skill.getName() != null && this.skillService.existByName(skill.getName()))
            throw new IdInvalidException("Skill name = " + skill.getName() + "is existed");
        currentSkill.setName(skill.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.saveSkill(currentSkill));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.getSkillById(id);
        if(currentSkill == null) {
            throw new IdInvalidException("Skill id = " + id + " isn't existed");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

}
