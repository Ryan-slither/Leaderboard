package com.osc.leaderboard.pullrequest.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.osc.leaderboard.pullrequest.model.PullRequest;

public interface PullRequestRepository extends MongoRepository<PullRequest, String> {
}
