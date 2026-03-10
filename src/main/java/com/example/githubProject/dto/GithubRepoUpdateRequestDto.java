package com.example.githubProject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record GithubRepoUpdateRequestDto(
        @NotBlank
        String fullName,
        @NotBlank
        String description,
        @NotBlank
        String cloneUrl,
        @Min(value = 0, message = "stars can't be less than 0")
        Integer stars,
        LocalDateTime createdAt
) {
}
