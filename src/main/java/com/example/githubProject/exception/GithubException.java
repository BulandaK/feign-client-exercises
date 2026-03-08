package com.example.githubProject.exception;

public class GithubException extends RuntimeException {
    public GithubException(String message) {
        super(message);
    }
}