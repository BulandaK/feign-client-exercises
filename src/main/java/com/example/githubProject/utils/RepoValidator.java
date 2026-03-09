package com.example.githubProject.utils;

import com.example.githubProject.exception.ResourceNotFoundException;
import com.example.githubProject.exception.RepositoryAlreadyExistsException;
import com.example.githubProject.model.GithubRepo;
import com.example.githubProject.repository.RepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepoValidator {

    private final RepoRepository repoRepository;

    public void validateNotExists(String fullName) {
        if (repoRepository.existsByFullName(fullName)) {
            throw new RepositoryAlreadyExistsException(fullName);
        }
    }

    public GithubRepo validateAndGet(String fullName) {
        GithubRepo entity = repoRepository.findByFullName(fullName);
        if (entity == null) {
            throw new ResourceNotFoundException("repository not found: " + fullName);
        }
        return entity;
    }
}
