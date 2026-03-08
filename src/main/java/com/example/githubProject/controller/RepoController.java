package com.example.githubProject.controller;

import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.service.RepoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/repositories")
public class RepoController {
    private final RepoService repoService;

    @GetMapping("/{owner}/{repo}")
    GithubRepoDto getRepo(@PathVariable("owner") String owner, @PathVariable("repo") String repositoryName){
        return repoService.getRepositoryInfo(owner,repositoryName);
    }
}
