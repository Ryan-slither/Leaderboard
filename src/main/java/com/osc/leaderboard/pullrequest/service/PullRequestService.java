package com.osc.leaderboard.pullrequest.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.osc.leaderboard.pullrequest.dto.PullRequestDTO;
import com.osc.leaderboard.pullrequest.model.PullRequest;
import com.osc.leaderboard.pullrequest.repository.PullRequestRepository;

@Service
public class PullRequestService {

    private final PullRequestRepository pullRequestRepository;

    public PullRequestService(PullRequestRepository pullRequestRepository) {
        this.pullRequestRepository = pullRequestRepository;
    }

    public PullRequestDTO createPullRequest(Instant mergedAt, String developerId, String repoId) {
        PullRequest pullRequest = new PullRequest(null, mergedAt, developerId, repoId);
        PullRequest pullRequestSaved = pullRequestRepository.save(pullRequest);
        return pullRequestToPullRequestDTO(pullRequestSaved);
    }

    public List<PullRequestDTO> findAllPullRequests() {
        return pullRequestRepository.findAll().stream().map(PullRequestService::pullRequestToPullRequestDTO).toList();
    }

    public static PullRequestDTO pullRequestToPullRequestDTO(PullRequest pullRequest) {
        return new PullRequestDTO(pullRequest.getId(), pullRequest.getMergedAt(), pullRequest.getDeveloperId(),
                pullRequest.getRepoId());
    }

}
