package com.example.demo.service;


import com.example.demo.repository.*;
import org.springframework.stereotype.Service;

@Service
public class ProgressService {
  private final LectureRepository lectureRepo;
  private final LectureProgressRepository progressRepo;

  public ProgressService(LectureRepository lectureRepo, LectureProgressRepository progressRepo) {
    this.lectureRepo = lectureRepo;
    this.progressRepo = progressRepo;
  }

  public int totalLectures(Long courseId) { return lectureRepo.countByCourseId(courseId); }

  public int completedLectures(Long userId, Long courseId) {
    return progressRepo.countCompletedInCourse(userId, courseId);
  }
}
