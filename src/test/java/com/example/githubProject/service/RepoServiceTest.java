package com.example.githubProject.service;

import com.example.githubProject.client.RepoClient;
import com.example.githubProject.dto.GithubClientResponse;
import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.exception.GithubException;
import com.example.githubProject.mapper.RepoMapper;
import com.example.githubProject.repository.RepoRepository;
import com.example.githubProject.utils.RepoValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepoServiceTest {
    RepoClient repoClient;
    RepoMapper repoMapper;
    RepoRepository repoRepository;
    RepoService repoService;
    RepoValidator repoValidator;

    @BeforeEach
    void setup() {
        this.repoClient = Mockito.mock(RepoClient.class);
        this.repoRepository = Mockito.mock(RepoRepository.class);
        this.repoMapper = Mappers.getMapper(RepoMapper.class);
        this.repoValidator = Mockito.mock(RepoValidator.class);
        this.repoService = new RepoService(repoClient,repoRepository,repoMapper,repoValidator);
    }

    @Test
    void getRepositoryInfo_CorrectData_ReturnsGithubRepoDto() {
        GithubClientResponse response = new GithubClientResponse("BulandaK/hospital", "my hospital app", "github.com/BulandaK/hospital", 3, null);

        when(repoClient.getRepoByOwnerAndName(any(), any()))
                .thenReturn(response);

        GithubRepoDto result = repoService.getRepositoryInfo("BulandaK", "hospital");

        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals("BulandaK/hospital",result.fullName()),
                () -> Assertions.assertEquals("github.com/BulandaK/hospital", result.cloneUrl())
        );

        verify(repoClient).getRepoByOwnerAndName(any(),any());
    }

    @Test
    void getRepositoryInfo_GithubReturns500_ThrowsGithubException() {
        when(repoClient.getRepoByOwnerAndName(any(), any()))
                .thenThrow(new GithubException("GitHub returned Internal Server Error (500)", HttpStatus.INTERNAL_SERVER_ERROR));

        GithubException exception = assertThrows(GithubException.class, () ->
                repoService.getRepositoryInfo("BulandaK", "hospital")
        );

        Assertions.assertEquals("GitHub returned Internal Server Error (500)", exception.getMessage());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());

        verify(repoClient).getRepoByOwnerAndName(any(), any());
    }
}
