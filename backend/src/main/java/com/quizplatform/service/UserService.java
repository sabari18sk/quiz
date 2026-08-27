package com.quizplatform.service;

import com.quizplatform.dto.UserDto;
import com.quizplatform.entity.User;
import com.quizplatform.enums.UserRole;
import com.quizplatform.exception.BadRequestException;
import com.quizplatform.exception.ResourceNotFoundException;
import com.quizplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get current authenticated user's email
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("No authenticated user found");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        throw new BadRequestException("Invalid authentication principal");
    }

    /**
     * Get current authenticated user entity
     */
    private User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Get user profile by ID
     */
    @Transactional(readOnly = true)
    public UserDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Users can only view their own profile unless they are ADMIN
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Unauthorized to view this profile");
        }
        
        return convertToDto(user);
    }

    /**
     * Get all users (ADMIN only)
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Only ADMIN can retrieve all users");
        }
        
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Update user profile (own profile or ADMIN updating others)
     */
    @Transactional
    public UserDto updateUserProfile(Long userId, String firstName, String lastName) {
        User currentUser = getCurrentUser();
        
        // Check authorization
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Unauthorized to modify this profile");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (firstName != null && !firstName.trim().isEmpty()) {
            user.setFirstName(firstName.trim());
        }
        
        if (lastName != null && !lastName.trim().isEmpty()) {
            user.setLastName(lastName.trim());
        }
        
        User updated = userRepository.save(user);
        return convertToDto(updated);
    }

    /**
     * Update user role (ADMIN only)
     */
    @Transactional
    public UserDto updateUserRole(Long userId, UserRole newRole) {
        User currentUser = getCurrentUser();
        
        // Only ADMIN can update roles
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Only ADMIN can update user roles");
        }
        
        // Prevent privilege escalation - cannot change own role
        if (currentUser.getId().equals(userId)) {
            throw new BadRequestException("Cannot change your own role. Use another admin account.");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setRole(newRole);
        User updated = userRepository.save(user);
        return convertToDto(updated);
    }

    /**
     * Update user status (ADMIN only)
     */
    @Transactional
    public UserDto updateUserStatus(Long userId, boolean isActive) {
        User currentUser = getCurrentUser();
        
        // Only ADMIN can update status
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Only ADMIN can update user status");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Prevent disabling own account
        if (currentUser.getId().equals(userId)) {
            throw new BadRequestException("Cannot disable your own account. Use another admin account.");
        }
        
        user.setStatus(isActive ? com.quizplatform.enums.UserStatus.ACTIVE : com.quizplatform.enums.UserStatus.INACTIVE);
        User updated = userRepository.save(user);
        return convertToDto(updated);
    }

    /**
     * Delete user (ADMIN only)
     */
    @Transactional
    public void deleteUser(Long userId) {
        User currentUser = getCurrentUser();
        
        // Only ADMIN can delete users
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("Only ADMIN can delete users");
        }
        
        // Prevent deleting own account
        if (currentUser.getId().equals(userId)) {
            throw new BadRequestException("Cannot delete your own account. Use another admin account.");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        userRepository.delete(user);
    }

    /**
     * Convert User entity to DTO
     */
    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getStatus()
        );
    }
}
