package com.example.demo.dto.lecture;


import java.util.List;

public record QuizSubmissionRequest(
    List<Integer> selectedIndices
) {}
