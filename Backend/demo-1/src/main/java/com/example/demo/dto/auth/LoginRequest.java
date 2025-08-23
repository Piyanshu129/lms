package com.example.demo.dto.auth;

import jakarta.validation.constraints.*;
import com.example.demo.domain.Role;
public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}