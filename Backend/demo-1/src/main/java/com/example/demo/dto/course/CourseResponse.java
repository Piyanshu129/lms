package com.example.demo.dto.course;


public record CourseResponse(
    Long id,
    String title,
    String description,
    String instructorName,
    int lectureCount
) {}
