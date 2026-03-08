package com.example.githubProject;

import com.example.githubProject.client.RepoClient;
import com.example.githubProject.model.GithubRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWireMock(port = 8888)
public class RepoClientTest {
    @Autowired
    RepoClient repoClient;
    @Autowired
    WireMockServer wireMockServer;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void test() throws JsonProcessingException {
        //given
        GithubRepo response = new GithubRepo("hospital", "my hospital app", "github.com/BulandaK/hospital", 3, null);

        wireMockServer.stubFor(WireMock.get("/repositories/owner/name").willReturn(
                WireMock.aResponse()
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(response))
                        .withStatus(200)
        ));
        //when
        GithubRepo result = repoClient.getRepoByOwnerAndName("owner", "name");

        //then
        assertAll(
                () -> assertEquals("hospital", result.getFullName()),
                () -> assertEquals("my hostpiatl app", result.getDescription()),
                () -> assertEquals(3,result.getStars())
        );
    }
}
