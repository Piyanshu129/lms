package com.example.demo.dto.auth;
import jakarta.validation.constraints.*;
import com.example.demo.domain.Role;
public record LoginResponse(String token, String email, String name, Role role) {}