package com.example.demo.dto.course;


public record CourseListItem(
    Long id,
    String title,
    String description,
    String instructorName
) {}
