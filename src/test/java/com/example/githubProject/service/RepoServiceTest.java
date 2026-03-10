package com.example.githubProject.service;

import com.example.githubProject.client.RepoClient;
import com.example.githubProject.dto.GithubClientResponse;
import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.dto.GithubRepoUpdateRequestDto;
import com.example.githubProject.exception.GithubException;
import com.example.githubProject.exception.RepositoryAlreadyExistsException;
import com.example.githubProject.exception.ResourceNotFoundException;
import com.example.githubProject.mapper.RepoMapper;
import com.example.githubProject.model.GithubRepo;
import com.example.githubProject.repository.RepoRepository;
import com.example.githubProject.utils.RepoValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        this.repoService = new RepoService(repoClient, repoRepository, repoMapper, repoValidator);
    }

    @Test
    void getRepositoryInfo_CorrectData_ReturnsGithubRepoDto() {
        GithubClientResponse response = new GithubClientResponse("BulandaK/hospital", "my hospital app", "github.com/BulandaK/hospital", 3, null);

        when(repoClient.getRepoByOwnerAndName(any(), any()))
                .thenReturn(response);

        GithubRepoDto result = repoService.getRepositoryInfo("BulandaK", "hospital");

        assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals("BulandaK/hospital", result.fullName()),
                () -> Assertions.assertEquals("github.com/BulandaK/hospital", result.cloneUrl())
        );

        verify(repoClient).getRepoByOwnerAndName(any(), any());
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

    @Test
    void saveRepositoryInfo_CorrectData_ReturnsGithubRepoDto() {
        String owner = "BulandaK";
        String repo = "hospital";
        String fullName = owner + "/" + repo;

        GithubClientResponse clientResponse = new GithubClientResponse(fullName, "my hospital app", "url", 10, null);

        doNothing().when(repoValidator).validateNotExists(fullName);
        when(repoClient.getRepoByOwnerAndName(owner, repo)).thenReturn(clientResponse);

        when(repoRepository.save(any(GithubRepo.class))).thenAnswer(invocation -> {
            GithubRepo entityToSave = invocation.getArgument(0);
            entityToSave.setInternalId(1L);
            return entityToSave;
        });

        GithubRepoDto result = repoService.saveRepositoryInfo(owner, repo);

        assertAll(
                () -> Assertions.assertNotNull(result, "Result should not be null"),
                () -> Assertions.assertEquals(1L, result.internalId(), "ID should be assigned by mock repository"),
                () -> Assertions.assertEquals("BulandaK/hospital", result.fullName(), "Full name should be mapped from response"),
                () -> Assertions.assertEquals("url", result.cloneUrl(), "URL should be mapped correctly"),
                () -> Assertions.assertEquals(10, result.stars(), "Stars should be mapped correctly")
        );

        verify(repoValidator).validateNotExists(fullName);
        verify(repoClient).getRepoByOwnerAndName(owner, repo);
        verify(repoRepository).save(any(GithubRepo.class));
    }

    @Test
    void saveRepositoryInfo_RepoAlreadyInDatabase_ThrowsGithubException() {
        String owner = "BulandaK";
        String repo = "test-repo";
        String fullName = "BulandaK/test-repo";

        doThrow(new RepositoryAlreadyExistsException("repository already exists"))
                .when(repoValidator).validateNotExists(fullName);

        assertThrows(RepositoryAlreadyExistsException.class,
                () -> repoService.saveRepositoryInfo(owner, repo));

        verifyNoInteractions(repoClient);
        verifyNoInteractions(repoRepository);
    }

    @Test
    void getRepositoryFromDatabase_CorrectData_ReturnsGithubRepoDto() {
        String owner = "BulandaK";
        String repo = "hospital";
        String fullName = "BulandaK/hospital";

        GithubRepo githubRepo = new GithubRepo(1L, fullName, "my description", "url", 5, null);

        when(repoValidator.validateAndGet(anyString())).thenReturn(githubRepo);

        GithubRepoDto result = repoService.getRepositoryFromDatabase(owner, repo);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(result, "result can't be null"),
                () -> Assertions.assertEquals("BulandaK/hospital", result.fullName()),
                () -> Assertions.assertEquals(1L, result.internalId()),
                () -> Assertions.assertEquals("url", result.cloneUrl()),
                () -> Assertions.assertEquals(5, result.stars())
        );

        verify(repoValidator).validateAndGet(fullName);
    }

    @Test
    void getRepositoryFromDatabase_IncorrectData_ThrowsResourceNotFoundException() {
        String owner = "BulandaK";
        String repo = "hospital";
        String fullName = owner + "/" + repo;
        String expectedMessage = "repository not found: " + fullName;

        when(repoValidator.validateAndGet(fullName))
                .thenThrow(new ResourceNotFoundException(expectedMessage));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> repoService.getRepositoryFromDatabase(owner, repo)
        );

        assertAll(
                () -> assertEquals(expectedMessage, exception.getMessage())
        );

        verify(repoValidator).validateAndGet(fullName);
    }
    @Test
    void updateInDatabase_CorrectData_ReturnsGithubRepoDto() {
        String owner = "BulandaK";
        String repo = "hospital";
        String fullName = owner + "/" + repo;
        GithubRepo entity = new GithubRepo(1L,fullName,"description","url",5,null);
        GithubRepoUpdateRequestDto requestDto = new GithubRepoUpdateRequestDto("BulandaK/githubRepo","repo to exercise feign","url",10,null);

        when(repoValidator.validateAndGet(anyString())).thenReturn(entity);

        GithubRepoDto result = repoService.updateInDatabase(owner,repo,requestDto);

        assertAll(
                () -> assertEquals("BulandaK/githubRepo",result.fullName()),
                () -> assertEquals("repo to exercise feign",result.description()),
                () -> assertEquals(10,result.stars())
        );

        verify(repoRepository).save(any(GithubRepo.class));
        verify(repoValidator).validateAndGet(fullName);
    }
    @Test
    void updateInDatabase_IncorrectData_ThrowsResourceNotFoundException() {
        String owner = "BulandaK";
        String repo = "non-existing";
        String fullName = owner + "/" + repo;
        GithubRepoUpdateRequestDto requestDto = new GithubRepoUpdateRequestDto("BulandaK/githubRepo","repo to exercise feign","url",10,null);
        String message = "repository not found: ";

        when(repoValidator.validateAndGet(anyString())).thenThrow(new ResourceNotFoundException(message));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> repoService.updateInDatabase(owner,repo,requestDto));

        Assertions.assertAll(
                () -> assertEquals(message,exception.getMessage())
        );

        verify(repoValidator).validateAndGet(fullName);
    }
    @Test
    void deleteInDatabase_DataCorrect_ReturnsVoid() {
        String owner = "BulandaK";
        String repo = "hospital";
        String fullName = owner + "/" + repo;
        GithubRepo entity = new GithubRepo(1L,fullName,"description","url",5,null);

        when(repoValidator.validateAndGet(anyString())).thenReturn(entity);

        repoService.deleteInDatabase(owner,repo);
        verify(repoRepository).delete(entity);
    }
    @Test
    void deleteInDatabase_DataIncorrect_ThrowsRepositoryNotFound() {
        String owner = "BulandaK";
        String repo = "hospital";
        String fullName = owner + "/" + repo;
        String message = "repository not found: ";

        when(repoValidator.validateAndGet(anyString()))
                .thenThrow(new ResourceNotFoundException(message));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                ()-> repoService.deleteInDatabase(owner,repo));

        assertEquals(message,exception.getMessage());
        verify(repoValidator).validateAndGet(fullName);
    }
}
