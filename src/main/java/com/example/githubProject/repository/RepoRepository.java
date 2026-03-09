package com.example.githubProject.repository;

import com.example.githubProject.model.GithubRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepoRepository extends JpaRepository<GithubRepo, Long> {

    GithubRepo findByFullName(String fullName);

    boolean existsByFullName(String fullName);
}
