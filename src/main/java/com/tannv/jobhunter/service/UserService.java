package com.tannv.jobhunter.service;

import com.tannv.jobhunter.domain.User;
import com.tannv.jobhunter.repository.UserRepository;
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

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }
}
