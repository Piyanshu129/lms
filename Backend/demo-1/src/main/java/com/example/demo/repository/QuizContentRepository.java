package com.example.demo.repository;

import com.example.demo.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface QuizContentRepository extends JpaRepository<QuizContent, Long> {
	  Optional<QuizContent> findByLectureId(Long lectureId);
	}
