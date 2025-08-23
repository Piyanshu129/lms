package com.example.demo.service;


import com.example.demo.dto.course.*;
import com.example.demo.domain.*;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
  private final CourseRepository courseRepo;
  private final LectureRepository lectureRepo;

  public CourseService(CourseRepository courseRepo, LectureRepository lectureRepo) {
    this.courseRepo = courseRepo;
    this.lectureRepo = lectureRepo;
  }

  public Course createCourse(CourseCreateRequest req, Authentication auth) {
    User user = (User) auth.getPrincipal();
    if (user.getRole() != Role.INSTRUCTOR) {
      throw new ForbiddenException("Only instructors can create courses");
    }
    Course course = Course.builder()
        .title(req.title())
        .description(req.description())
        .instructor(user)
        .build();
    return courseRepo.save(course);
  }

  public List<Course> listCourses() { return courseRepo.findAll(); }

  public Course getCourseOrThrow(Long id) {
    return courseRepo.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));
  }

  public void ensureOwner(Course course, User user) {
    if (!course.getInstructor().getId().equals(user.getId())) {
      throw new ForbiddenException("Only the owner instructor may modify this course");
    }
  }

  public int countLectures(Long courseId) { return lectureRepo.countByCourseId(courseId); }
}
