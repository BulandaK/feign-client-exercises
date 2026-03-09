package com.example.githubProject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GithubException extends RuntimeException {
    private final HttpStatus httpStatus;
    public GithubException(String message,HttpStatus status) {
        super(message);
        this.httpStatus = status;
    }
}