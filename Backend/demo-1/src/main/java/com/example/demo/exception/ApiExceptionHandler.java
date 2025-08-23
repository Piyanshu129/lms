package com.example.demo.exception;


import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {


  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String,Object>> notFound(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<Map<String,Object>> forbidden(ForbiddenException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,Object>> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,Object>> badRequest(MethodArgumentNotValidException ex) {
    var errs = new ArrayList<>();
    ex.getBindingResult().getFieldErrors().forEach(e -> errs.add(e.getField()+": "+e.getDefaultMessage()));
    return ResponseEntity.badRequest().body(Map.of("errors", errs));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String,Object>> server(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error"));
  }
}

