package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReadingContent {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  private Lecture lecture;

  @Column(length = 10000)
  private String text;

  private String linkUrl; // optional URL alternative to text
}
