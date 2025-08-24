package com.example.demo.controller;


import com.example.demo.dto.course.*;
import com.example.demo.util.Mapper;
import com.example.demo.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
  private final CourseService courseService;
  private final LectureService lectureService;

  public CourseController(CourseService courseService, LectureService lectureService) {
    this.courseService = courseService;
    this.lectureService = lectureService;
  }

  @GetMapping
  public List<CourseListItem> list() {
    return courseService.listCourses().stream().map(Mapper::toListItem).toList();
  }

  @PostMapping
  public CourseResponse create(@Valid @RequestBody CourseCreateRequest req, Authentication auth) {
    var c = courseService.createCourse(req, auth);
    return Mapper.toCourseResponse(c, 0);
  }

  @GetMapping("/{courseId}")
  public CourseResponse detail(@PathVariable Long courseId) {
    var c = courseService.getCourseOrThrow(courseId);
    return Mapper.toCourseResponse(c, courseService.countLectures(courseId));
  }
  @GetMapping("/{courseId}/completed-lectures")
  public List<Long> getCompletedLectures(@PathVariable Long courseId, Authentication auth) {
    return lectureService.getCompletedLectureIds(courseId, auth);
  }
}
