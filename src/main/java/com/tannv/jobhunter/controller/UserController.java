package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.domain.User;
import com.tannv.jobhunter.domain.response.user.ResCreateUserDTO;
import com.tannv.jobhunter.domain.response.ResultPaginationDTO;
import com.tannv.jobhunter.domain.response.user.ResUpdateUserDTO;
import com.tannv.jobhunter.domain.response.user.ResUserDTO;
import com.tannv.jobhunter.util.anotation.ApiMessage;
import com.tannv.jobhunter.util.error.IdInvalidException;
import com.tannv.jobhunter.service.UserService;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
}
