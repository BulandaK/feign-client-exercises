package com.example.githubProject.controller;

import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.service.RepoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class RepoController {
    private final RepoService repoService;

    @GetMapping("/repositories/{owner}/{repository-name}")
    GithubRepoDto getRepo(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        return repoService.getRepositoryInfo(owner, repositoryName);
    }
}
