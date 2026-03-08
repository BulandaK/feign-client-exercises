package com.example.githubProject;

import com.example.githubProject.client.RepoClient;
import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.exception.GithubException;
import com.example.githubProject.model.GithubRepo;
import com.example.githubProject.service.RepoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepoServiceTest {
    RepoClient repoClient;
    RepoService repoService;

    @BeforeEach
    void setup() {
        this.repoClient = Mockito.mock(RepoClient.class);
        this.repoService = new RepoService(repoClient);
    }

    @Test
    void getRepositoryInfo_CorrectData_ReturnsGithubRepoDto() {
        GithubRepo githubRepo = new GithubRepo("hospital", "my hospital app", "github.com/BulandaK/hospital", 3, null);

        when(repoClient.getRepoByOwnerAndName(any(), any()))
                .thenReturn(githubRepo);

        GithubRepoDto result = repoService.getRepositoryInfo("BulandaK", "hospital");

        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(result.fullName(), githubRepo.getFullName()),
                () -> Assertions.assertEquals(result.cloneUrl(), githubRepo.getCloneUrl())
        );

        verify(repoClient).getRepoByOwnerAndName(any(),any());
    }

    @Test
    void getRepositoryInfo_GithubReturns500_ThrowsGithubException() {
        when(repoClient.getRepoByOwnerAndName(any(), any()))
                .thenThrow(new GithubException("GitHub returned Internal Server Error (500)"));

        GithubException exception = assertThrows(GithubException.class, () ->
                repoService.getRepositoryInfo("BulandaK", "hospital")
        );

        Assertions.assertEquals("GitHub returned Internal Server Error (500)", exception.getMessage());

        verify(repoClient).getRepoByOwnerAndName(any(), any());
    }
}
