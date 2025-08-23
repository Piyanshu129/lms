package com.example.demo.dto.auth;
import com.example.demo.domain.Role;

public record UserResponse(Long id, String email, String name, Role role) {}

