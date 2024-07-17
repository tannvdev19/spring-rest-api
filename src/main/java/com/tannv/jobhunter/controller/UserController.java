package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.domain.User;
import com.tannv.jobhunter.domain.response.user.ResCreateUserDTO;
import com.tannv.jobhunter.domain.response.ResultPaginationDTO;
import com.tannv.jobhunter.domain.response.user.ResUpdateUserDTO;
import com.tannv.jobhunter.domain.response.user.ResUserDTO;
import com.tannv.jobhunter.service.ExcelService;
import com.tannv.jobhunter.util.anotation.ApiMessage;
import com.tannv.jobhunter.util.error.IdInvalidException;
import com.tannv.jobhunter.service.UserService;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final ExcelService excelService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, ExcelService excelService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.excelService = excelService;
    }

    @GetMapping
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {
        ResultPaginationDTO resultPaginationDTO = this.userService.getAllUsers(spec,pageable);
        return ResponseEntity.ok(resultPaginationDTO);
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") Long id) throws IdInvalidException {
        User currentUser = this.userService.getUserById(id);
        if(currentUser == null) {
            throw new IdInvalidException("User id =" + id + " is not existed");
        }
        User user = this.userService.getUserById(id);
        ResUserDTO resUserDTO = this.userService.convertResUserDTO(user);
        return ResponseEntity.ok(resUserDTO);
    }

    @PostMapping
    @ApiMessage("Create user")
    public ResponseEntity<ResCreateUserDTO> createUser(@RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if(isEmailExist) {
            throw new IdInvalidException("Email" + user.getEmail() + "is existed, please use another email instead");
        }

        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User createdUser =  this.userService.handleSave(user);
        ResCreateUserDTO resUser = this.userService.convertCreateUserDTO(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(resUser);
    }

    @PutMapping
    @ApiMessage("Update user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User currentUser = this.userService.getUserById(user.getId());
        if(currentUser == null) {
            throw new IdInvalidException("User id =" + user.getId() + " is not existed");
        }
        User updatedUser = this.userService.handleUpdate(user);
        ResUpdateUserDTO resUpdateUserDTO = this.userService.convertResUpdateUserDTO(updatedUser);
        return ResponseEntity.ok(resUpdateUserDTO);
    }

    @DeleteMapping("{id}")
    @ApiMessage("Delete user")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws IdInvalidException {
        User currentUser = this.userService.getUserById(id);
        if(currentUser == null) {
            throw new IdInvalidException("User id =" + id + " is not existed");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/excel")
    @ApiMessage("Export excel")
    public ResponseEntity<Resource> download() throws IOException {
        String fileName = "users.xlsx";
        ByteArrayInputStream actualData = excelService.getActualData();
        InputStreamResource file = new InputStreamResource(actualData);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

}
