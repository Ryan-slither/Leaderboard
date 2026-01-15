package com.osc.leaderboard.pullrequest.service;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import com.osc.leaderboard.pullrequest.dto.PullRequestDTO;
import com.osc.leaderboard.pullrequest.dto.PullRequestLeaderBoardDTO;
import com.osc.leaderboard.pullrequest.model.PullRequest;
import com.osc.leaderboard.pullrequest.repository.PullRequestRepository;

@Service
public class PullRequestService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final PullRequestRepository pullRequestRepository;

    public PullRequestService(PullRequestRepository pullRequestRepository) {
        this.pullRequestRepository = pullRequestRepository;
    }

    public PullRequestDTO createPullRequest(Instant mergedAt, String developerId, String repoId) {
        PullRequest pullRequest = new PullRequest(null, mergedAt, new ObjectId(developerId), new ObjectId(repoId));
        PullRequest pullRequestSaved = pullRequestRepository.save(pullRequest);
        return pullRequestToPullRequestDTO(pullRequestSaved);
    }

    public List<PullRequestDTO> findAllPullRequests() {
        return pullRequestRepository.findAll().stream().map(PullRequestService::pullRequestToPullRequestDTO).toList();
    }

    public List<PullRequestLeaderBoardDTO> getPullRequestLeaderboard() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(
                        "repos", "repoId", "_id", "reposList"),
                Aggregation.unwind(
                        "$reposList",
                        false),
                Aggregation.group(
                        "$reposList").count().as("pullRequestCount"),
                Aggregation.project(
                        "pullRequestCount").and("$_id._id").as("repoId").and("$_id.name").as("repoName"));
        AggregationResults<PullRequestLeaderBoardDTO> results = mongoTemplate.aggregate(aggregation, "pullrequests",
                PullRequestLeaderBoardDTO.class);
        return results.getMappedResults();
    }

    public static PullRequestDTO pullRequestToPullRequestDTO(PullRequest pullRequest) {
        return new PullRequestDTO(pullRequest.getId(), pullRequest.getMergedAt(), pullRequest.getDeveloperId(),
                pullRequest.getRepoId());
    }

}
