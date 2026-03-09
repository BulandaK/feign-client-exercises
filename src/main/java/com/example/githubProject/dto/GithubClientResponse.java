package com.example.githubProject.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDateTime;

public record GithubClientResponse(
        @JsonAlias("full_name") String fullName,
        @JsonAlias("description") String description,
        @JsonAlias("clone_url") String cloneUrl,
        @JsonAlias("stargazers_count") Integer stars,
        @JsonAlias("created_at") LocalDateTime createdAt
) {
}
