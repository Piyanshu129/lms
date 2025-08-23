package com.example.demo.dto.lecture;


import com.example.demo.domain.LectureType;
import java.util.List;

public record LectureResponse(
    Long id,
    String title,
    LectureType type,
    Integer position,

    // READING content
    String text,
    String linkUrl,

    // QUIZ content
    List<QuizQuestionView> questions
) {}
