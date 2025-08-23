package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.domain.*;
import java.util.*;


public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
	  List<QuizAttempt> findByStudentIdAndLectureIdOrderByAttemptedAtDesc(Long studentId, Long lectureId);
	}