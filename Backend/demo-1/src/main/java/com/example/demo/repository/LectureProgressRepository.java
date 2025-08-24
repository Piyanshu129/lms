package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.domain.*;
import java.util.*;


public interface LectureProgressRepository extends JpaRepository<LectureProgress, Long> {
	  Optional<LectureProgress> findByStudentIdAndLectureId(Long studentId, Long lectureId);
	  @Query("select count(lp) from LectureProgress lp where lp.student.id = :studentId and lp.lecture.course.id = :courseId and lp.completed = true")
	  int countCompletedInCourse(Long studentId, Long courseId);
	  Optional<LectureCompletion> findByStudentAndLecture(User student, Lecture lecture);

	  @Query("SELECT lp.lecture.id FROM LectureProgress lp WHERE lp.student = ?1 AND lp.lecture.course.id = ?2 AND lp.completed = true")
	  List<Long> findCompletedLectureIdsByStudentAndCourseId(User student, Long courseId);
	}
