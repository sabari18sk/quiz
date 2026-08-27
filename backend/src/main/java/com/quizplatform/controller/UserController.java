package com.quizplatform.controller;

import com.quizplatform.dto.UserDto;
import com.quizplatform.enums.UserRole;
import com.quizplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current user's profile
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUserProfile() {
        // Get the current authenticated user's ID from security context
        // For now, we'll retrieve by finding the user associated with the authentication
        // This will be handled by the service using SecurityContextHolder
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        
        // We need to get user ID first - let's use a workaround
        // The service will handle authorization
        List<UserDto> allUsers = userService.getAllUsers();
        UserDto currentUser = allUsers.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        
        return ResponseEntity.ok(currentUser);
    }

    /**
     * Get user profile by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable Long id) {
        UserDto dto = userService.getUserProfile(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Get all users (ADMIN only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Update user profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateUserProfile(
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        UserDto updated = userService.updateUserProfile(id, firstName, lastName);
        return ResponseEntity.ok(updated);
    }

    /**
     * Update user role (ADMIN only)
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserRole(
            @PathVariable Long id,
            @RequestBody UserRole role) {
        UserDto updated = userService.updateUserRole(id, role);
        return ResponseEntity.ok(updated);
    }

    /**
     * Update user status (ADMIN only)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        UserDto updated = userService.updateUserStatus(id, active);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete user (ADMIN only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
