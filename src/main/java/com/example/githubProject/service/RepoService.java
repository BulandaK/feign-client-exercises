package com.example.githubProject.service;

import com.example.githubProject.model.GithubRepo;
import com.example.githubProject.client.RepoClient;
import com.example.githubProject.dto.GithubRepoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RepoService {
    private final RepoClient githubClient;

    public GithubRepoDto getRepositoryInfo(String owner, String repo) {
        GithubRepo entity = githubClient.getRepoByOwnerAndName(owner, repo);
        return new GithubRepoDto(
                entity.getFullName(),
                entity.getDescription(),
                entity.getCloneUrl(),
                entity.getStars(),
                entity.getCreatedAt()
        );
    }
}
