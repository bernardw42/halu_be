package com.example.halu_be.controllers;

import com.example.halu_be.models.User;
import com.example.halu_be.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 🔹 Get all users
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * 🔹 Get a single user by ID
     */
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * 🔹 Create a new user
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    /**
     * 🔹 Update an existing user by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> existing = userService.getUserById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = existing.get();
        user.setUsername(updatedUser.getUsername());
        user.setPassword(updatedUser.getPassword());  // In real app: hash this
        user.setRole(updatedUser.getRole());

        // Optional image
        if (updatedUser.getProfileImageUrl() != null && !updatedUser.getProfileImageUrl().isBlank()) {
            user.setProfileImageUrl(updatedUser.getProfileImageUrl());
        }

        User saved = userService.saveUser(user);
        return ResponseEntity.ok(saved);
    }

    /**
     * 🔹 Delete a user by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> existing = userService.getUserById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }
}
