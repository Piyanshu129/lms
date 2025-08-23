package com.example.demo.util;


import com.example.demo.dto.course.*;
import com.example.demo.domain.*;

public class Mapper {
  public static CourseListItem toListItem(Course c) {
    return new CourseListItem(c.getId(), c.getTitle(), c.getDescription(), c.getInstructor().getName());
  }

  public static CourseResponse toCourseResponse(Course c, int lectureCount) {
    return new CourseResponse(c.getId(), c.getTitle(), c.getDescription(), c.getInstructor().getName(), lectureCount);
  }
}
