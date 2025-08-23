package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.domain.*;
import java.util.*;


public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
	  List<QuizQuestion> findByQuizIdOrderByIdAsc(Long quizId);
	}