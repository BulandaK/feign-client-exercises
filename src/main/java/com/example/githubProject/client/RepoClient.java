package com.example.githubProject.client;

import com.example.githubProject.dto.GithubClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "repoClient", url = "${github.api.url}", configuration = FeignConfig.class,fallbackFactory = GithubRepoFallbackFactory.class)
public interface RepoClient {

    @GetMapping("/repositories/{owner}/{repo-name}")
    GithubClientResponse getRepoByOwnerAndName(@PathVariable("owner") String owner, @PathVariable("repo-name") String repoName);
}
