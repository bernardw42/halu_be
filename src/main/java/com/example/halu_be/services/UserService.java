package com.example.halu_be.services;

import com.example.halu_be.models.User;
import com.example.halu_be.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
            if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isBlank()) {
                user.setProfileImageUrl("https://ui-avatars.com/api/?name=User&background=random");
            }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
    userRepository.deleteById(id);
}
}
