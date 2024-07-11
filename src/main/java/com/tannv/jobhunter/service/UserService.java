package com.tannv.jobhunter.service;

import com.tannv.jobhunter.domain.Company;
import com.tannv.jobhunter.domain.User;
import com.tannv.jobhunter.domain.response.user.ResCreateUserDTO;
import com.tannv.jobhunter.domain.response.ResultPaginationDTO;
import com.tannv.jobhunter.domain.response.user.ResUpdateUserDTO;
import com.tannv.jobhunter.domain.response.user.ResUserDTO;
import com.tannv.jobhunter.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;


    public UserService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public User handleSave(User user) {
        if(user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
            user.setCompany(companyOptional.orElse(null));
        }
        return this.userRepository.save(user);
    }

    public User handleUpdate(User reqUser) {
        User dbUser = getUserById(reqUser.getId());
        if(dbUser != null) {
            dbUser.setEmail(reqUser.getEmail());
            dbUser.setName(reqUser.getName());
            dbUser.setPassword(reqUser.getPassword());

            // Verify company
            if(reqUser.getCompany() != null) {
                Optional<Company> companyOptional = this.companyRepository.findById(reqUser.getId());
                dbUser.setCompany(companyOptional.orElse(null));
            }

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
            ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
            userDto.setId(item.getId());
            userDto.setEmail(item.getEmail());
            userDto.setName(item.getName());
            userDto.setGender(item.getGender());
            userDto.setAddress(item.getAddress());
            userDto.setAge(item.getAge());
            userDto.setUpdatedAt(item.getUpdatedAt());
            userDto.setCreatedAt(item.getCreatedAt());
            if(item.getCompany() != null) {
                companyUser.setId(item.getCompany().getId());
                companyUser.setName(item.getCompany().getName());
                userDto.setCompany(companyUser);
            }
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
        ResCreateUserDTO.CompanyUser companyUser = new ResCreateUserDTO.CompanyUser();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());

        if(user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resCreateUserDTO.setCompany(companyUser);
        }
        return resCreateUserDTO;
    }

    public ResUserDTO convertResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());

        if(user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resUserDTO.setCompany(companyUser);
        }
        return resUserDTO;
    }

    public ResUpdateUserDTO convertResUpdateUserDTO(User user) {
        ResUpdateUserDTO resUpdateUserDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser companyUser = new ResUpdateUserDTO.CompanyUser();
        resUpdateUserDTO.setId(user.getId());
        resUpdateUserDTO.setName(user.getName());
        resUpdateUserDTO.setGender(user.getGender());
        resUpdateUserDTO.setAddress(user.getAddress());
        resUpdateUserDTO.setAge(user.getAge());
        resUpdateUserDTO.setUpdatedAt(user.getUpdatedAt());
        if(user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resUpdateUserDTO.setCompany(companyUser);
        }
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
