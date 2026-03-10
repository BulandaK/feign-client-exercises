package com.example.githubProject.controller;

import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.dto.GithubRepoUpdateRequestDto;
import com.example.githubProject.service.RepoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class RepoController {
    private final RepoService repoService;

    @GetMapping("/repositories/{owner}/{repository-name}")
    GithubRepoDto getRepo(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        return repoService.getRepositoryInfo(owner, repositoryName);
    }
    @GetMapping("/local/repositories/{owner}/{repository-name}")
    public GithubRepoDto getRepoFromDatabase(
            @PathVariable("owner") String owner,
            @PathVariable("repository-name") String repositoryName) {
        return repoService.getRepositoryFromDatabase(owner, repositoryName);
    }

    @PostMapping("/repositories/{owner}/{repository-name}")
    @ResponseStatus(HttpStatus.CREATED)
    GithubRepoDto saveRepoToDatabase(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        return repoService.saveRepositoryInfo(owner, repositoryName);
    }

    @PutMapping("/repositories/{owner}/{repository-name}")
    GithubRepoDto updateInDatabase(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName, @RequestBody GithubRepoUpdateRequestDto request) {
        return repoService.updateInDatabase(owner, repositoryName, request);
    }

    @DeleteMapping("/repositories/{owner}/{repository-name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteInDatabase(@PathVariable("owner") String owner, @PathVariable("repository-name") String repositoryName) {
        repoService.deleteInDatabase(owner, repositoryName);
    }
}
