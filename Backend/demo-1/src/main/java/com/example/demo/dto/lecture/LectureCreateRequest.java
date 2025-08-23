package com.example.demo.dto.lecture;


import com.example.demo.domain.LectureType;
import jakarta.validation.constraints.*;
import java.util.List;

public record LectureCreateRequest(
    @NotBlank String title,
    @NotNull LectureType type,

    // For READING
    String text,
    String linkUrl,

    // For QUIZ
    List<QuizQuestionDto> questions
) {}
