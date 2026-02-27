package com.osc.leaderboard.github.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osc.leaderboard.developer.dto.DeveloperDTO;
import com.osc.leaderboard.developer.service.DeveloperService;
import com.osc.leaderboard.fetch.model.Fetch;
import com.osc.leaderboard.github.dtos.PullRequestResultDTO;
import com.osc.leaderboard.github.dtos.PullRequestSearchDTO;
import com.osc.leaderboard.pullrequest.service.PullRequestService;
import com.osc.leaderboard.repo.dtos.RepoDTO;
import com.osc.leaderboard.repo.service.RepoService;

public abstract class GithubService {

    protected final ObjectMapper objectMapper;

    protected final Environment env;

    protected final RepoService repoService;

    protected final DeveloperService developerService;

    protected final PullRequestService pullRequestService;

    protected final WebClient apiClient;

    protected GithubService(ObjectMapper objectMapper, Environment env, RepoService repoService,
            DeveloperService developerService, PullRequestService pullRequestService, WebClient apiClient) {
        this.objectMapper = objectMapper;
        this.env = env;
        this.repoService = repoService;
        this.developerService = developerService;
        this.pullRequestService = pullRequestService;
        this.apiClient = apiClient;
    }

    public abstract Integer fetchPullRequests(Optional<Fetch> laterThan) throws IOException;

    protected PullRequestSearchDTO processPullRequestSearchJson(JsonNode json) {
        Integer totalCount = json.get("total_count").asInt();
        Instant earliestDate = Instant.MAX;

        JsonNode itemsNode = json.get("items");
        List<PullRequestResultDTO> pullRequestResults = new ArrayList<>();
        for (JsonNode item : itemsNode) {
            JsonNode user = item.get("user");
            String username = user.get("login").asText();
            String avatarUrl = user.get("avatar_url").asText();

            String repoUrl = item.get("repository_url").asText();
            String repoName = repoUrl.substring(repoUrl.lastIndexOf("/") + 1);

            Instant mergedAt = Instant.parse(item.get("pull_request").get("merged_at").asText());
            earliestDate = earliestDate.compareTo(mergedAt) > 0 ? mergedAt : earliestDate;

            String nodeId = item.get("node_id").asText();

            pullRequestResults.add(new PullRequestResultDTO(username, avatarUrl, repoName, mergedAt, nodeId));
        }

        return new PullRequestSearchDTO(totalCount, pullRequestResults, earliestDate);
    }

    protected void processPullRequestSearch(PullRequestSearchDTO pullRequestSearchDTO) {
        pullRequestSearchDTO.pullRequestResults().forEach(result -> {
            RepoDTO repoDTO = repoService.createRepo(result.repoName());
            DeveloperDTO developerDTO = developerService.createDeveloper(result.username(), result.avatarUrl());
            pullRequestService.createPullRequest(result.mergedAt(), developerDTO.id(), repoDTO.id(), result.nodeId());
        });
    }

}
