package com.example.halu_be.services;

import com.example.halu_be.models.User;
import com.example.halu_be.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // ✅ Add logging
public class UserService {

    private final UserRepository userRepository;

    /**
     * ✅ Get all users — no risky DB writes so no try/catch needed.
     */
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.warn("No users found in the system."); // just warn, no throw
        }
        return users;
    }

    /**
     * ✅ Find user by ID.
     */
    public Optional<User> getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("User with ID {} not found.", id);
        }
        return userOpt;
    }

    /**
     * ✅ Find user by username (for auth)
     */
    public Optional<User> getUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("User with username '{}' not found.", username);
        }
        return userOpt;
    }

    /**
     * ✅ Find by national ID.
     */
    public Optional<User> getUserByNationalId(String nationalId) {
        Optional<User> userOpt = userRepository.findByNationalId(nationalId);
        if (userOpt.isEmpty()) {
            log.warn("User with national ID '{}' not found.", nationalId);
        }
        return userOpt;
    }

    /**
     * ✅ Save user — has DB write so wrap with try/catch.
     */
    public User saveUser(User user) {
        // Defensive default for profile image
        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isBlank()) {
            user.setProfileImageUrl("https://ui-avatars.com/api/?name=User&background=random");
        }

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to save user {}: {}", user.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Could not save user. Please try again later.", e);
        }
    }

    /**
     * ✅ Delete user — also risky DB write, so wrap with try/catch.
     */
    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete user with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not delete user. Please try again later.", e);
        }
    }
}
