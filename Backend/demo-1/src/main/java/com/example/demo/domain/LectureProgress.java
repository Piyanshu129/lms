package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"student_id","lecture_id"}))
public class LectureProgress {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private User student;

  @ManyToOne(optional = false)
  private Lecture lecture;

  @Column(nullable = false)
  private boolean completed;

  private Double scorePercent; // for quizzes (null for reading)
  private Instant completedAt;
}
