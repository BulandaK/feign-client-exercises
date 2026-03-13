package com.example.githubProject.controller;

import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.dto.GithubRepoUpdateRequestDto;
import com.example.githubProject.service.RepoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RepoController {
    private final RepoService repoService;

    @GetMapping("/repositories/{owner}/{repository-name}")
    GithubRepoDto getGithubRepository(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        log.info("getting repo from github api");
        return repoService.getRepositoryInfo(owner, repositoryName);
    }

    @GetMapping("/local/repositories/{owner}/{repository-name}")
    public GithubRepoDto getLocalRepository(
            @PathVariable("owner") String owner,
            @PathVariable("repository-name") String repositoryName) {
        log.info("getting repo from local database");
        return repoService.getLocalRepository(owner, repositoryName);
    }

    @PostMapping("/repositories/{owner}/{repository-name}")
    @ResponseStatus(HttpStatus.CREATED)
    GithubRepoDto saveLocalRepository(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        log.info("saving repo in local database");
        return repoService.saveRepositoryInfo(owner, repositoryName);
    }

    @PutMapping("/repositories/{owner}/{repository-name}")
    GithubRepoDto updateLocalRepository(
            @PathVariable("owner") String owner,
            @PathVariable("repository-name") String repositoryName,
            @Valid @RequestBody GithubRepoUpdateRequestDto request
    ) {
        log.info("updating repo in local database");
        return repoService.updateLocalRepository(owner, repositoryName, request);
    }

    @DeleteMapping("/repositories/{owner}/{repository-name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteLocalRepository(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        log.info("deleting repo in local database");
        repoService.deleteLocalRepository(owner, repositoryName);
    }
}
