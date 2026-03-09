package com.example.githubProject.client;

import com.example.githubProject.dto.GithubClientResponse;
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
import org.springframework.test.context.ActiveProfiles;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
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
        GithubRepo response = new GithubRepo(1L,"hospital", "my hospital app", "github.com/BulandaK/hospital", 3, null);

        wireMockServer.stubFor(WireMock.get("/repositories/owner/name").willReturn(
                WireMock.aResponse()
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(response))
                        .withStatus(200)
        ));
        //when
        GithubClientResponse result = repoClient.getRepoByOwnerAndName("owner", "name");

        //then
        assertAll(
                () -> assertEquals("hospital", result.fullName()),
                () -> assertEquals("my hospital app", result.description()),
                () -> assertEquals(3,result.stars())
        );
    }
}
