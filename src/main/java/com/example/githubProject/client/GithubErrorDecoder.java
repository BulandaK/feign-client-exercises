package com.example.githubProject.client;

import com.example.githubProject.exception.GithubException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class GithubErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 500 -> new GithubException("GitHub returned Internal Server Error (500)", HttpStatus.INTERNAL_SERVER_ERROR);

            case 503 -> new RetryableException(
                    response.status(),
                    "Service Unavailable (503) - retrying...",
                    response.request().httpMethod(),
                    50L,
                    response.request()
            );

            default -> new Default().decode(methodKey, response);
        };
    }
}