package com.example.demo.dto.lecture;


public record QuizSubmissionResult(
    int totalQuestions,
    int correctAnswers,
    double scorePercent,
    boolean passed
) {}

