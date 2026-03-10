package com.example.githubProject.service;

import com.example.githubProject.dto.GithubClientResponse;
import com.example.githubProject.dto.GithubRepoUpdateRequestDto;
import com.example.githubProject.mapper.RepoMapper;
import com.example.githubProject.model.GithubRepo;
import com.example.githubProject.client.RepoClient;
import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.repository.RepoRepository;
import com.example.githubProject.utils.RepoValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RepoService {
    private final RepoClient githubClient;
    private final RepoRepository repoRepository;
    private final RepoMapper repoMapper;
    private final RepoValidator repoValidator;

    public GithubRepoDto getRepositoryInfo(String owner, String repo) {
        GithubClientResponse response = githubClient.getRepoByOwnerAndName(owner, repo);
        return repoMapper.toDto(response);
    }

    @Transactional
    public GithubRepoDto saveRepositoryInfo(String owner, String repo) {
        String fullName = owner + "/" + repo;
        repoValidator.validateNotExists(fullName);
        GithubClientResponse clientResponse = githubClient.getRepoByOwnerAndName(owner, repo);
        GithubRepo entity = repoMapper.toEntity(clientResponse);
        GithubRepo saved = repoRepository.save(entity);
        return repoMapper.toDto(saved);
    }

    public GithubRepoDto getRepositoryFromDatabase(String owner, String repositoryName) {
        GithubRepo entity = repoValidator.validateAndGet(owner + "/" + repositoryName);
        return repoMapper.toDto(entity);
    }

    @Transactional
    public GithubRepoDto updateInDatabase(String owner, String repositoryName, GithubRepoUpdateRequestDto request) {
        GithubRepo entity = repoValidator.validateAndGet(owner + "/" + repositoryName);
        GithubRepo updated = entity.update(request);
        repoRepository.save(updated);
        return repoMapper.toDto(updated);
    }

    @Transactional
    public void deleteInDatabase(String owner, String repositoryName) {
        GithubRepo entity = repoValidator.validateAndGet(owner + "/" + repositoryName);
        repoRepository.delete(entity);
    }
}
