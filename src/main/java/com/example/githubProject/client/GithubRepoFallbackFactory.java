package com.example.githubProject.client;

import com.example.githubProject.dto.GithubClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GithubRepoFallbackFactory implements FallbackFactory<RepoClient> {
    @Override
    public RepoClient create(Throwable cause) {
        return new RepoClient() {
            @Override
            public GithubClientResponse getRepoByOwnerAndName(String owner, String repoName) {
                log.info("[Fallback] fallback from GtihubRepoFactory");
                return new GithubClientResponse("BulandaK/defaultRepo","default repo for fallback", "url",5,null);
            }
        };
    }
}
