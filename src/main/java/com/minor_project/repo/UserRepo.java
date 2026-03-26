package com.minor_project.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minor_project.entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity,UUID>{

     boolean existsByEmail(String email);

     boolean existsByUserName(String trim);
    
}
