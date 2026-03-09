package com.example.githubProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GithubProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubProjectApplication.class, args);
    }

}
