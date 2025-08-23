package com.example.demo.dto.auth;
import jakarta.validation.constraints.*;
import com.example.demo.domain.Role;
public record RegisterRequest(@NotBlank @Email String email, @NotBlank String password, @NotBlank String name, @NotNull Role role) {}