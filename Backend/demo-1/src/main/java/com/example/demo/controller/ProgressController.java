package com.example.demo.controller;


import com.example.demo.domain.User;
import com.example.demo.service.ProgressService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/courses/{courseId}/progress")
public class ProgressController {
  private final ProgressService progressService;

  public ProgressController(ProgressService progressService) {
    this.progressService = progressService;
  }

  @GetMapping
  public Map<String, Object> get(@PathVariable Long courseId, Authentication auth) {
    var user = (User) auth.getPrincipal();
    int total = progressService.totalLectures(courseId);
    int completed = progressService.completedLectures(user.getId(), courseId);
    return Map.of("completed", completed, "total", total, "display", String.format("%d/%d lectures completed", completed, total));
  }
}
