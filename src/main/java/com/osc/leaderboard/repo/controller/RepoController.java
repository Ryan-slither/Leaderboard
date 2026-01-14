package com.osc.leaderboard.repo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.osc.leaderboard.repo.service.RepoService;

@RestController
@RequestMapping("/repo")
public class RepoController {

    private final RepoService repoService;

    public RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

}
