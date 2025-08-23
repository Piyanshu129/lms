package com.example.demo.dto.lecture;


import com.example.demo.domain.LectureType;

public record LectureListItem(
    Long id,
    String title,
    LectureType type,
    Integer position,
    String summary,        // small text preview for READING (nullable)
    Integer questionCount  // number of questions for QUIZ (nullable)
) {}
