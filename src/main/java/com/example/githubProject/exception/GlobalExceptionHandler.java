package com.example.githubProject.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GithubException.class)
    public ResponseEntity<String> handleGithubException(GithubException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ex.getMessage());
    }
}
