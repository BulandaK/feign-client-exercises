package com.example.githubProject.model;

import com.example.githubProject.dto.GithubRepoUpdateRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "github_repos")
public class GithubRepo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;
    @JsonProperty("full_name")
    private String fullName;
    private String description;
    @JsonProperty("clone_url")
    private String cloneUrl;
    @JsonProperty("stargazers_count")
    private Integer stars;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public GithubRepo update(GithubRepoUpdateRequestDto request) {
        this.fullName = request.fullName();
        this.description = request.description();
        this.cloneUrl = request.cloneUrl();
        this.stars = request.stars();
        this.createdAt = request.createdAt();
        return this;
    }
}