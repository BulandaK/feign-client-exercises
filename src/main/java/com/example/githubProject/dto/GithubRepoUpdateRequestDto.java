package com.example.githubProject.dto;

import java.time.LocalDateTime;

public record GithubRepoUpdateRequestDto(
        String fullName,
        String description,
        String cloneUrl,
        Integer stars,
        LocalDateTime createdAt
) {
}
