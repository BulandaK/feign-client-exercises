package com.example.githubProject.client;

import com.example.githubProject.dto.GithubClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GithubRepoFallback implements RepoClient{
    @Override
    public GithubClientResponse getRepoByOwnerAndName(String owner, String repoName) {
        log.info("[Fallback] repo from fallback");
        return new GithubClientResponse("BulandaK/defaultRepo","default repo for fallback", "url",5,null);
    }
}
