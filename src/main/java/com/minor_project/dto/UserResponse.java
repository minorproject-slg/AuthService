package com.minor_project.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.minor_project.enums.UserRole;
import com.minor_project.enums.UserStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String userName;
    private UserRole role;
    private UserStatus status;
    private boolean isVerified;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}