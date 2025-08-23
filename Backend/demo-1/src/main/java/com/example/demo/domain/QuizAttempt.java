package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizAttempt {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private User student;

  @ManyToOne(optional = false)
  private Lecture lecture;

  @Column(nullable = false)
  private int totalQuestions;

  @Column(nullable = false)
  private int correctAnswers;

  @Column(nullable = false)
  private double scorePercent;

  private boolean passed;

  private Instant attemptedAt;
}
