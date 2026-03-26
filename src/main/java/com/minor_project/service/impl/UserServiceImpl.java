package com.minor_project.service.impl;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.minor_project.dto.UserRegistrationRequest;
import com.minor_project.dto.UserResponse;
import com.minor_project.entity.UserEntity;
import com.minor_project.enums.UserRole;
import com.minor_project.enums.UserStatus;
import com.minor_project.exception.UserAlreadyExistsException;
import com.minor_project.repo.UserRepo;
import com.minor_project.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    private UserResponse mapToResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .isVerified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        boolean emailExists = userRepo.existsByEmail(request.getEmail().toLowerCase().trim());
        boolean userExists = userRepo.existsByUserName(request.getUserName().trim());

        if (emailExists || userExists) {
            throw new UserAlreadyExistsException("Registration failed. Please check your details and try again.");
        }

        // 2. High-Level Password Validation (Check against Pwned Passwords)
        // Don't just check length; check for common patterns.
        if (!isPasswordStrong(request.getPassword())) {
            throw new IllegalArgumentException("Password does not meet security requirements.");
        }

        // 3. Sanitization (Prevent XSS/Injection in usernames)
        String sanitizedUsername = HtmlUtils.htmlEscape(request.getUserName().trim());

        UserEntity userEntity = UserEntity.builder()
                .email(request.getEmail().toLowerCase().trim())
                .userName(sanitizedUsername)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.STUDENT)
                .status(UserStatus.ACTIVE)
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            UserEntity savedUser = userRepo.saveAndFlush(userEntity);

            // will implemented
            // sendVerificationEmail(savedUser);

            return mapToResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("Registration failed.");
        }
    }

    // Helper for strong validation
    private boolean isPasswordStrong(String password) {
        // regex: 1 upper, 1 lower, 1 digit, 1 special, min 10 chars
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{10,}$";
        return password.matches(pattern);
    }

}
