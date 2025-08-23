package com.example.demo.dto.course;

import jakarta.validation.constraints.NotBlank;

public record CourseCreateRequest(
    @NotBlank String title,
    @NotBlank String description
) {}