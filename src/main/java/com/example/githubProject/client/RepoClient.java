package com.example.githubProject.client;

import com.example.githubProject.model.GithubRepo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "repoClient", url = "https://api.github.com/", configuration = FeignConfig.class)
public interface RepoClient {

    @GetMapping( "/repos/{owner}/{repo-name}")
    GithubRepo getRepoByOwnerAndName(@PathVariable("owner") String owner, @PathVariable("repo-name") String repoName);
}
