package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.domain.*;
import java.util.*;


public interface QuizOptionRepository extends JpaRepository<QuizOption, Long> {
	  List<QuizOption> findByQuestionIdOrderByIndexInQuestionAsc(Long questionId);
}