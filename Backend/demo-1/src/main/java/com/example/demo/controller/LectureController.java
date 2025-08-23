package com.example.demo.controller;


import com.example.demo.dto.lecture.*;
import com.example.demo.service.LectureService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/lectures")
public class LectureController {
  private final LectureService lectureService;

  public LectureController(LectureService lectureService) {
    this.lectureService = lectureService;
  }
  
  @GetMapping
  public LectureListResponse list(@PathVariable Long courseId) {
    return lectureService.listLectures(courseId);
  }


  @PostMapping
  public LectureResponse create(@PathVariable Long courseId,
                                @Valid @RequestBody LectureCreateRequest req,
                                Authentication auth) {
    var lec = lectureService.createLecture(courseId, req, auth);
    return lectureService.getLecture(courseId, lec.getId(), auth);
  }

  @GetMapping("/{lectureId}")
  public LectureResponse get(@PathVariable Long courseId, @PathVariable Long lectureId, Authentication auth) {
    return lectureService.getLecture(courseId, lectureId, auth);
  }

  @PostMapping("/{lectureId}/complete")
  public ResponseEntity<Void> completeReading(@PathVariable Long courseId,
                                              @PathVariable Long lectureId,
                                              Authentication auth) {
    lectureService.completeReading(courseId, lectureId, auth);
    return ResponseEntity.ok().build();
  }

  // SUBMIT quiz and grade
  @PostMapping("/{lectureId}/quiz/submit")
  public QuizSubmissionResult submitQuiz(@PathVariable Long courseId,
                                         @PathVariable Long lectureId,
                                         @RequestBody QuizSubmissionRequest req,
                                         Authentication auth) {
    return lectureService.submitQuiz(courseId, lectureId, req, auth);
  }
}
