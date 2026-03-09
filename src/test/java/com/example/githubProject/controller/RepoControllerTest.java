package com.example.githubProject.controller;

import com.example.githubProject.dto.GithubRepoDto;
import com.example.githubProject.exception.GithubException;
import com.example.githubProject.service.RepoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class RepoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    RepoService repoService;

    @Test
    void getRepo_DataCorrect_ReturnGithubRepoDto() throws Exception{
        when(repoService.getRepositoryInfo(any(),any()))
                .thenReturn(new GithubRepoDto(  1L,"hospital", "my hospital app", "github.com/BulandaK/hospital", 3, null));
        mockMvc.perform(MockMvcRequestBuilders.get("/repositories/BulandaK/hospital"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("hospital"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("my hospital app"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cloneUrl").value("github.com/BulandaK/hospital"));
        verify(repoService).getRepositoryInfo(any(),any());
    }
    @Test
    void getRepo_GithubReturns500_ReturnGithubExceptionMessage() throws Exception {
        String errorMessage = "GitHub returned Internal Server Error (500)";
        when(repoService.getRepositoryInfo(any(), any()))
                .thenThrow(new GithubException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/repositories/BulandaK/hospital"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(errorMessage));
    }
}
