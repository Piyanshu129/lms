package com.example.demo.repository;
import com.example.demo.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface ReadingContentRepository extends JpaRepository<ReadingContent, Long> {
	  Optional<ReadingContent> findByLectureId(Long lectureId);
	}
