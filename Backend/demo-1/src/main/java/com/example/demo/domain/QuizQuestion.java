package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizQuestion {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private QuizContent quiz;

  @Column(nullable = false, length = 2000)
  private String text;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<QuizOption> options = new ArrayList<>();

  // index of correct option within options list, 0-based
  @Column(nullable = false)
  private int correctIndex;
}
