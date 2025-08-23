package com.example.demo.repository;

import com.example.demo.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;


public interface LectureRepository extends JpaRepository<Lecture, Long> {
	  List<Lecture> findByCourseIdOrderByPositionAsc(Long courseId);
	  Optional<Lecture> findByIdAndCourseId(Long id, Long courseId);
	  int countByCourseId(Long courseId);
	}
