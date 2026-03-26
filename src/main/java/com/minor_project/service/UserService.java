package com.minor_project.service;

import com.minor_project.dto.UserRegistrationRequest;
import com.minor_project.dto.UserResponse;

public interface UserService {

    public UserResponse register(UserRegistrationRequest userRegistrationRequest);
} 