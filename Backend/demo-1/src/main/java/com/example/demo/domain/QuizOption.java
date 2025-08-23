package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizOption {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private QuizQuestion question;

  @Column(nullable = false, length = 2000)
  private String text;

  @Column(nullable = false)
  private int indexInQuestion; // 0..n
}

