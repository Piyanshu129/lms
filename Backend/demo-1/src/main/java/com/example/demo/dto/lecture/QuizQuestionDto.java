package com.example.demo.dto.lecture;


import jakarta.validation.constraints.*;
import java.util.List;

public record QuizQuestionDto(
    @NotBlank String text,
    @NotEmpty List<@NotBlank String> options,
    @Min(0) int correctIndex
) {}
