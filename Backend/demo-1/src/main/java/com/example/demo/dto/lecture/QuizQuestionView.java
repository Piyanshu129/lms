package com.example.demo.dto.lecture;


import java.util.List;

public record QuizQuestionView(
    Long id,
    String text,
    List<String> options
) {}

