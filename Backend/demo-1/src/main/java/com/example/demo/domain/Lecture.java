package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(indexes = @Index(columnList = "course_id,position", unique = true))
public class Lecture {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Course course;

  @Column(nullable = false)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LectureType type;

  // Sequential order inside a course (0..n)
  @Column(nullable = false)
  private int position;
}

