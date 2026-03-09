package com.example.githubProject.exception;

import org.springframework.http.HttpStatus;

public class RepositoryAlreadyExistsException extends GithubException {
    public RepositoryAlreadyExistsException(String fullName) {
        super("repository " + fullName + " is already in database.", HttpStatus.CONFLICT);
    }
}