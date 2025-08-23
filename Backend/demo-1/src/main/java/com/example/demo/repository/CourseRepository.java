package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.domain.*;
import java.util.*;


public interface CourseRepository extends JpaRepository<Course, Long> {
	  List<Course> findByInstructorId(Long instructorId);
	}