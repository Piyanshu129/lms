package com.example.demo.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(length = 2000)
  private String description;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private User instructor;
}

