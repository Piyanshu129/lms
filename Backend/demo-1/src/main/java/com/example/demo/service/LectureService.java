package com.example.demo.service;


import com.example.demo.domain.*;
import com.example.demo.dto.lecture.*;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class LectureService {
  private final CourseRepository courseRepo;
  private final LectureRepository lectureRepo;
  private final ReadingContentRepository readingRepo;
  private final QuizContentRepository quizRepo;
  private final QuizQuestionRepository questionRepo;
  private final QuizOptionRepository optionRepo;
  private final LectureProgressRepository progressRepo;
  private final QuizAttemptRepository attemptRepo;
  private final UserRepository userRepo;



  @Value("${app.quiz.passingPercent:70}")
  private double passingPercent;

//  public LectureService(CourseRepository courseRepo, LectureRepository lectureRepo,
//                        ReadingContentRepository readingRepo, QuizContentRepository quizRepo,
//                        QuizQuestionRepository questionRepo, QuizOptionRepository optionRepo,
//                        LectureProgressRepository progressRepo, QuizAttemptRepository attemptRepo) {
//    this.courseRepo = courseRepo;
//    this.lectureRepo = lectureRepo;
//    this.readingRepo = readingRepo;
//    this.quizRepo = quizRepo;
//    this.questionRepo = questionRepo;
//    this.optionRepo = optionRepo;
//    this.progressRepo = progressRepo;
//    this.attemptRepo = attemptRepo;
//  }
  public LectureService(CourseRepository courseRepo, LectureRepository lectureRepo,
		  ReadingContentRepository readingRepo, QuizContentRepository quizRepo,
		  QuizQuestionRepository questionRepo, QuizOptionRepository optionRepo,
		  LectureProgressRepository progressRepo, QuizAttemptRepository attemptRepo,
		  UserRepository userRepo) {
		  this.courseRepo = courseRepo;
		  this.lectureRepo = lectureRepo;
		  this.readingRepo = readingRepo;
		  this.quizRepo = quizRepo;
		  this.questionRepo = questionRepo;
		  this.optionRepo = optionRepo;
		  this.progressRepo = progressRepo;
		  this.attemptRepo = attemptRepo;
		  this.userRepo = userRepo;
		  }
  
  private User currentUser(Authentication auth) {
	  Object p = auth.getPrincipal();
	  if (p instanceof User u) return u;
	  String email = String.valueOf(p);
	  return userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
	  }

  @Transactional
  public Lecture createLecture(Long courseId, LectureCreateRequest req, Authentication auth) {
//    User user = (User) auth.getPrincipal();
	  User user = currentUser(auth);
    var course = courseRepo.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));
    if (user.getRole() != Role.INSTRUCTOR || !course.getInstructor().getId().equals(user.getId())) {
      throw new ForbiddenException("Only the owning instructor can add lectures");
    }
    int position = lectureRepo.findByCourseIdOrderByPositionAsc(courseId).size();
    Lecture lecture = Lecture.builder()
        .course(course)
        .title(req.title())
        .type(req.type())
        .position(position)
        .build();
    lecture = lectureRepo.save(lecture);

    if (req.type() == LectureType.READING) {
      ReadingContent rc = ReadingContent.builder()
          .lecture(lecture)
          .text(req.text())
          .linkUrl(req.linkUrl())
          .build();
      readingRepo.save(rc);
    } else {
      // QUIZ
      QuizContent qc = QuizContent.builder().lecture(lecture).build();
      qc = quizRepo.save(qc);
      if (req.questions() != null) {
        for (QuizQuestionDto qd : req.questions()) {
          QuizQuestion qq = QuizQuestion.builder()
              .quiz(qc)
              .text(qd.text())
              .correctIndex(qd.correctIndex())
              .build();
          qq = questionRepo.save(qq);
          for (int i = 0; i < qd.options().size(); i++) {
            QuizOption opt = QuizOption.builder()
                .question(qq)
                .text(qd.options().get(i))
                .indexInQuestion(i)
                .build();
            optionRepo.save(opt);
          }
        }
      }
    }
    return lecture;
  }
  
  


	@Transactional(readOnly = true)
	public LectureListResponse listLectures(Long courseId) {
	 // Ensure course exists (optional, but helpful for 404 semantics)
	 var lectures = lectureRepo.findByCourseIdOrderByPositionAsc(courseId);
	
	 // Build lightweight list. We’ll enrich with summary/questionCount by type.
	 var items = new java.util.ArrayList<LectureListItem>(lectures.size());
	 for (var lec : lectures) {
	   if (lec.getType() == LectureType.READING) {
	     var rcOpt = readingRepo.findByLectureId(lec.getId());
	     String summary = rcOpt.map(rc -> {
	       String t = rc.getText();
	       if (t == null || t.isBlank()) return rc.getLinkUrl();
	       return t.length() > 120 ? t.substring(0, 120) + "..." : t;
	     }).orElse(null);
	     items.add(new LectureListItem(lec.getId(), lec.getTitle(), lec.getType(), lec.getPosition(), summary, null));
	   } else {
	     var qcOpt = quizRepo.findByLectureId(lec.getId());
	     Integer count = qcOpt.map(qc -> questionRepo.findByQuizIdOrderByIdAsc(qc.getId()).size()).orElse(0);
	     items.add(new LectureListItem(lec.getId(), lec.getTitle(), lec.getType(), lec.getPosition(), null, count));
	   }
	 }
	 return new LectureListResponse(lectures.isEmpty() ? courseId : lectures.get(0).getCourse().getId(), items);
	}


  @Transactional(readOnly = true)
  public LectureResponse getLecture(Long courseId, Long lectureId, Authentication auth) {
    var lecture = lectureRepo.findByIdAndCourseId(lectureId, courseId)
        .orElseThrow(() -> new NotFoundException("Lecture not found"));

    if (lecture.getType() == LectureType.READING) {
      var rc = readingRepo.findByLectureId(lecture.getId())
          .orElseThrow(() -> new NotFoundException("Reading content missing"));
      return new LectureResponse(
          lecture.getId(), lecture.getTitle(), lecture.getType(), lecture.getPosition(),
          rc.getText(), rc.getLinkUrl(), List.of()
      );
    } else {
      var qc = quizRepo.findByLectureId(lecture.getId())
          .orElseThrow(() -> new NotFoundException("Quiz content missing"));
      var questions = questionRepo.findByQuizIdOrderByIdAsc(qc.getId());
      List<QuizQuestionView> views = new ArrayList<>();
      for (QuizQuestion q : questions) {
        var opts = optionRepo.findByQuestionIdOrderByIndexInQuestionAsc(q.getId());
        List<String> optionTexts = opts.stream().map(QuizOption::getText).toList();
        views.add(new QuizQuestionView(q.getId(), q.getText(), optionTexts));
      }
      return new LectureResponse(
          lecture.getId(), lecture.getTitle(), lecture.getType(), lecture.getPosition(),
          null, null, views
      );
    }
  }

  @Transactional
  public void completeReading(Long courseId, Long lectureId, Authentication auth) {
    var lecture = lectureRepo.findByIdAndCourseId(lectureId, courseId)
        .orElseThrow(() -> new NotFoundException("Lecture not found"));
    if (lecture.getType() != LectureType.READING) throw new ForbiddenException("Not a reading lecture");

//    User student = (User) auth.getPrincipal();
    User student = currentUser(auth);
    var lp = progressRepo.findByStudentIdAndLectureId(student.getId(), lecture.getId())
        .orElse(LectureProgress.builder().student(student).lecture(lecture).build());
    lp.setCompleted(true);
    lp.setScorePercent(null);
    lp.setCompletedAt(Instant.now());
    progressRepo.save(lp);
  }

  @Transactional
  public QuizSubmissionResult submitQuiz(Long courseId, Long lectureId,
  QuizSubmissionRequest req,
  Authentication auth) {
  // Debug line
  System.out.println("submitQuiz called: courseId=" + courseId + ", lectureId=" + lectureId);

  var lecture = lectureRepo.findByIdAndCourseId(lectureId, courseId)
  .orElseThrow(() -> new NotFoundException("Lecture not found"));

  if (lecture.getType() != LectureType.QUIZ) {
  throw new ForbiddenException("Not a quiz lecture");
  }

  var qc = quizRepo.findByLectureId(lecture.getId())
  .orElseThrow(() -> new NotFoundException("Quiz content missing"));

  var questions = questionRepo.findByQuizIdOrderByIdAsc(qc.getId());
  if (questions.isEmpty()) {
  throw new NotFoundException("Quiz has no questions");
  }

  // Validate each question has options and correctIndex is within bounds
  // And build the list of correct indices in order
  java.util.List<Integer> correctIndices = new java.util.ArrayList<>(questions.size());
  for (var q : questions) {
  var opts = optionRepo.findByQuestionIdOrderByIndexInQuestionAsc(q.getId());
  if (opts == null || opts.isEmpty()) {
  throw new IllegalArgumentException("Quiz question has no options");
  }
  if (q.getCorrectIndex() < 0 || q.getCorrectIndex() >= opts.size()) {
  throw new IllegalArgumentException("Quiz question has invalid correctIndex");
  }
  correctIndices.add(q.getCorrectIndex());
  }

  // Null-guard the request body
  java.util.List<Integer> selected =
  (req != null && req.selectedIndices() != null) ? req.selectedIndices() : java.util.List.of();

  int total = correctIndices.size();
  int correct = 0;
  for (int i = 0; i < total; i++) {
  int chosen = (i < selected.size()) ? selected.get(i) : -1;
  if (chosen == correctIndices.get(i)) correct++;
  }
  double score = total == 0 ? 0.0 : (correct * 100.0 / total);
  boolean passed = score >= passingPercent;

  var student = currentUser(auth);

  attemptRepo.save(QuizAttempt.builder()
  .student(student)
  .lecture(lecture)
  .totalQuestions(total)
  .correctAnswers(correct)
  .scorePercent(score)
  .passed(passed)
  .attemptedAt(java.time.Instant.now())
  .build());

  if (passed) {
  var lp = progressRepo.findByStudentIdAndLectureId(student.getId(), lecture.getId())
  .orElse(LectureProgress.builder().student(student).lecture(lecture).build());
  lp.setCompleted(true);
  lp.setScorePercent(score);
  lp.setCompletedAt(java.time.Instant.now());
  progressRepo.save(lp);
  }

  return new QuizSubmissionResult(total, correct, score, passed);
  }
  
//  @Transactional
//  public QuizSubmissionResult submitQuiz(Long courseId, Long lectureId, QuizSubmissionRequest req, Authentication auth) {
//	  System.out.println("submitQuiz called: courseId=" + courseId + ", lectureId=" + lectureId);
//
//	  var lecture = lectureRepo.findByIdAndCourseId(lectureId, courseId)
//        .orElseThrow(() -> new NotFoundException("Lecture not found"));
//    if (lecture.getType() != LectureType.QUIZ) throw new ForbiddenException("Not a quiz lecture");
//
//    var qc = quizRepo.findByLectureId(lecture.getId())
//        .orElseThrow(() -> new NotFoundException("Quiz content missing"));
//    var questions = questionRepo.findByQuizIdOrderByIdAsc(qc.getId());
//    if (questions.isEmpty()) {
//    	throw new NotFoundException("Quiz has no questions");
//    	}
//    	for (var q : questions) {
//    	var opts = optionRepo.findByQuestionIdOrderByIndexInQuestionAsc(q.getId());
//    	if (opts == null || opts.isEmpty()) {
//    	throw new IllegalArgumentException("Quiz question has no options");
//    	}
//    	if (q.getCorrectIndex() < 0 || q.getCorrectIndex() >= opts.size()) {
//    	throw new IllegalArgumentException("Quiz question has invalid correctIndex");
//    	}
//    }
//    int total = questions.size();
//    int correct = 0;
//
////    List<Integer> selected = req.selectedIndices() == null ? List.of() : req.selectedIndices();
//    List<Integer> selected = (req != null && req.selectedIndices() != null) ? req.selectedIndices() : List.of();
//    if (selected.size() != total) {
//      // You may choose to reject or treat missing answers as incorrect; here we treat missing as incorrect.
//    }
//
//    for (int i = 0; i < total; i++) {
//      int chosen = (i < selected.size()) ? selected.get(i) : -1;
//      if (chosen == questions.get(i).getCorrectIndex()) correct++;
//    }
//
//    double score = total == 0 ? 0.0 : (correct * 100.0 / total);
//    boolean passed = score >= passingPercent;
//
////    User student = (User) auth.getPrincipal();
//    User student = currentUser(auth);
//    var attempt = QuizAttempt.builder()
//        .student(student).lecture(lecture)
//        .totalQuestions(total).correctAnswers(correct)
//        .scorePercent(score).passed(passed).attemptedAt(Instant.now())
//        .build();
//    attemptRepo.save(attempt);
//
//    if (passed) {
//      var lp = progressRepo.findByStudentIdAndLectureId(student.getId(), lecture.getId())
//          .orElse(LectureProgress.builder().student(student).lecture(lecture).build());
//      lp.setCompleted(true);
//      lp.setScorePercent(score);
//      lp.setCompletedAt(Instant.now());
//      progressRepo.save(lp);
//    }
//    return new QuizSubmissionResult(total, correct, score, passed);
//  }
}
