package com.example.githubProject.controller;

import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.dto.GithubRepoUpdateRequestDto;
import com.example.githubProject.exception.GithubException;
import com.example.githubProject.exception.RepositoryAlreadyExistsException;
import com.example.githubProject.exception.ResourceNotFoundException;
import com.example.githubProject.service.RepoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class RepoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    RepoService repoService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getRepo_DataCorrect_ReturnGithubRepoDto() throws Exception {
        when(repoService.getRepositoryInfo(any(), any()))
                .thenReturn(new GithubRepoDto(1L, "hospital", "my hospital app", "github.com/BulandaK/hospital", 3, null));
        mockMvc.perform(get("/repositories/BulandaK/hospital")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value("hospital"))
                .andExpect(jsonPath("$.description").value("my hospital app"))
                .andExpect(jsonPath("$.cloneUrl").value("github.com/BulandaK/hospital"));
        verify(repoService).getRepositoryInfo(any(), any());
    }
    @Test
    void getRepo_GithubReturns500_ReturnGithubExceptionMessage() throws Exception {
        String errorMessage = "GitHub returned Internal Server Error (500)";
        when(repoService.getRepositoryInfo(any(), any()))
                .thenThrow(new GithubException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR));

        mockMvc.perform(get("/repositories/BulandaK/hospital"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(errorMessage));
    }
    @Test
    void getRepoFromDatabase_DataCorrect_Returns200andJson() throws Exception {
        String owner = "BulandaK";
        String repo = "hospital";
        GithubRepoDto expectedDto = new GithubRepoDto(1L, "BulandaK/hospital", "desc", "url", 10, null);

        when(repoService.getRepositoryFromDatabase(owner, repo)).thenReturn(expectedDto);

        mockMvc.perform(get("/local/repositories/{owner}/{repository-name}", owner, repo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("BulandaK/hospital"))
                .andExpect(jsonPath("$.stars").value(10))
                .andExpect(jsonPath("$.internalId").value(1));
    }
    @Test
    void getRepoFromDatabase_RepoNotFound_Returns404() throws Exception {
        String owner = "BulandaK";
        String repo = "not-found";

        when(repoService.getRepositoryFromDatabase(owner, repo))
                .thenThrow(new ResourceNotFoundException("repository not found: " + owner + "/" + repo));

        mockMvc.perform(MockMvcRequestBuilders.get("/local/repositories/{owner}/{repository-name}", owner, repo))
                .andExpect(status().isNotFound());
    }
    @Test
    void saveRepoToDatabase_CorrectData_Returns201AndJson() throws Exception {
        String owner = "BulandaK";
        String repo = "hospital";
        GithubRepoDto savedDto = new GithubRepoDto(1L, "BulandaK/hospital", "desc", "url", 10, null);

        when(repoService.saveRepositoryInfo(owner, repo)).thenReturn(savedDto);

        mockMvc.perform(post("/repositories/{owner}/{repository-name}", owner, repo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("BulandaK/hospital"))
                .andExpect(jsonPath("$.internalId").value(1))
                .andExpect(jsonPath("$.stars").value(10));

        verify(repoService).saveRepositoryInfo(owner, repo);
    }

    @Test
    void saveRepoToDatabase_AlreadyExists_Returns409Conflict() throws Exception {
        String owner = "BulandaK";
        String repo = "hospital";

        when(repoService.saveRepositoryInfo(owner, repo))
                .thenThrow(new RepositoryAlreadyExistsException("repository already exists: " + owner + "/" + repo));

        mockMvc.perform(post("/repositories/{owner}/{repository-name}", owner, repo))
                .andExpect(status().isConflict());
    }
    @Test
    void updateInDatabase_CorrectData_Returns200AndJson() throws Exception {
        String owner = "BulandaK";
        String repo = "hospital";
        GithubRepoUpdateRequestDto requestDto = new GithubRepoUpdateRequestDto("BulandaK/new-name", "updated desc", "url", 10, null);
        GithubRepoDto responseDto = new GithubRepoDto(1L, "BulandaK/new-name", "updated desc", "url", 10, null);

        when(repoService.updateInDatabase(anyString(), anyString(), any(GithubRepoUpdateRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/repositories/{owner}/{repository-name}", owner, repo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("BulandaK/new-name"))
                .andExpect(jsonPath("$.description").value("updated desc"))
                .andExpect(jsonPath("$.stars").value(10));

        verify(repoService).updateInDatabase(eq(owner), eq(repo), any(GithubRepoUpdateRequestDto.class));
    }
    @Test
    void updateInDatabase_BlankFields_Returns400() throws Exception {
        GithubRepoUpdateRequestDto invalidRequest = new GithubRepoUpdateRequestDto(
                "", "", "", -5, LocalDateTime.now()
        );

        mockMvc.perform(put("/repositories/BulandaK/hospital")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(repoService);
    }
    @Test
    void updateInDatabase_RepoNotFound_Returns404() throws Exception {
        String owner = "BulandaK";
        String repo = "non-existent";
        GithubRepoUpdateRequestDto validRequest = new GithubRepoUpdateRequestDto(
                "BulandaK/new", "desc", "url", 10, LocalDateTime.now()
        );

        when(repoService.updateInDatabase(eq(owner), eq(repo), any()))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(put("/repositories/{owner}/{repo}", owner, repo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound());
    }
}
