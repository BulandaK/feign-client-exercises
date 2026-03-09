package com.example.githubProject.mapper;

import com.example.githubProject.dto.GithubClientResponse;
import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.model.GithubRepo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RepoMapper {
    GithubRepoDto toDto(GithubRepo entity);

    GithubRepoDto toDto(GithubClientResponse response);

    GithubRepo toEntity(GithubRepoDto dto);

    GithubRepo toEntity(GithubClientResponse response);
}
