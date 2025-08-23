package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.domain.*;
import java.util.*;


public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
	  Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
	  int countByCourseId(Long courseId);
	}

