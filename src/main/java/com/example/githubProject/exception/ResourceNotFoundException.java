package com.example.githubProject.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends GithubException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
