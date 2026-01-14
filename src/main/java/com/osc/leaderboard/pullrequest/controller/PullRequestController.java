package com.osc.leaderboard.pullrequest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.osc.leaderboard.pullrequest.dto.PullRequestLeaderBoardDTO;
import com.osc.leaderboard.pullrequest.service.PullRequestService;

@RestController
@RequestMapping("/pullrequest")
public class PullRequestController {

    private final PullRequestService pullRequestService;

    public PullRequestController(PullRequestService pullRequestService) {
        this.pullRequestService = pullRequestService;
    }

    @GetMapping
    public ResponseEntity<List<PullRequestLeaderBoardDTO>> getPullRequestLeaderboard() {
        return new ResponseEntity<>(pullRequestService.getPullRequestLeaderboard(), HttpStatus.OK);
    }

}
