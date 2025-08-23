package com.example.demo.dto.lecture;


import java.util.List;

public record LectureListResponse(
    Long courseId,
    List<LectureListItem> lectures
) {}
