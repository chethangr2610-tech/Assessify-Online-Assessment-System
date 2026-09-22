package com.example.exam.service;

import com.example.exam.model.User;
import com.example.exam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void saveStudent(User user) {
        if (user.getUsername() != null) {
            user.setUsername(user.getUsername().trim().toLowerCase());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_STUDENT");
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        return userRepository.findByUsername(username.trim().toLowerCase()).orElse(null);
    }
}
