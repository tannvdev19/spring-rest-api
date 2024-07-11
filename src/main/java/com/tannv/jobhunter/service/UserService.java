package com.tannv.jobhunter.service;

import com.tannv.jobhunter.domain.User;
import com.tannv.jobhunter.domain.response.ResCreateUserDTO;
import com.tannv.jobhunter.domain.response.ResultPaginationDTO;
import com.tannv.jobhunter.domain.response.ResUpdateUserDTO;
import com.tannv.jobhunter.domain.response.ResUserDTO;
import com.tannv.jobhunter.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleSave(User user) {
        return this.userRepository.save(user);
    }

    public User handleUpdate(User reqUser) {
        User dbUser = getUserById(reqUser.getId());
        if(dbUser != null) {
            dbUser.setEmail(reqUser.getEmail());
            dbUser.setName(reqUser.getName());
            dbUser.setPassword(reqUser.getPassword());

            dbUser = this.userRepository.save(dbUser);
        }
        return dbUser;
    }

    public User getUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        return userOptional.orElse(null);
    }

    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageUser.getSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        List<ResUserDTO> listUser = pageUser.getContent().stream().map(item -> {
            ResUserDTO userDto = new ResUserDTO();
            userDto.setId(item.getId());
            userDto.setEmail(item.getEmail());
            userDto.setName(item.getName());
            userDto.setGender(item.getGender());
            userDto.setAddress(item.getAddress());
            userDto.setAge(item.getAge());
            userDto.setUpdatedAt(item.getUpdatedAt());
            userDto.setCreatedAt(item.getCreatedAt());
            return userDto;
        }).toList();

        rs.setResult(listUser);

        return rs;
    }

    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }
    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username).orElse(null);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertCreateUserDTO(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        return resCreateUserDTO;
    }

    public ResUserDTO convertResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resUserDTO;
    }

    public ResUpdateUserDTO convertResUpdateUserDTO(User user) {
        ResUpdateUserDTO resUpdateUserDTO = new ResUpdateUserDTO();
        resUpdateUserDTO.setId(user.getId());
        resUpdateUserDTO.setName(user.getName());
        resUpdateUserDTO.setGender(user.getGender());
        resUpdateUserDTO.setAddress(user.getAddress());
        resUpdateUserDTO.setAge(user.getAge());
        resUpdateUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resUpdateUserDTO;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if(currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
