package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.domain.*;
import java.util.*;


public interface LectureProgressRepository extends JpaRepository<LectureProgress, Long> {
	  Optional<LectureProgress> findByStudentIdAndLectureId(Long studentId, Long lectureId);
	  @Query("select count(lp) from LectureProgress lp where lp.student.id = :studentId and lp.lecture.course.id = :courseId and lp.completed = true")
	  int countCompletedInCourse(Long studentId, Long courseId);
	}
