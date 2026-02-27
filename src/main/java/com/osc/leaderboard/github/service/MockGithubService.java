package com.osc.leaderboard.github.service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.osc.leaderboard.developer.service.DeveloperService;
import com.osc.leaderboard.fetch.model.Fetch;
import com.osc.leaderboard.github.dtos.PullRequestSearchDTO;
import com.osc.leaderboard.pullrequest.service.PullRequestService;
import com.osc.leaderboard.repo.service.RepoService;

@Service
@Profile("test")
public class MockGithubService extends GithubService {

    private static final List<String> PULL_REQUEST_SEARCH_PATH = new ArrayList<>(
            Arrays.asList("GithubSearchPullRequests1.json", "GithubSearchPullRequests2.json",
                    "GithubSearchPullRequests3.json", "GithubSearchPullRequests4.json"));

    public MockGithubService(ObjectMapper objectMapper, Environment env, RepoService repoService,
            DeveloperService developerService, PullRequestService pullRequestService, WebClient apiClient) {
        super(objectMapper, env, repoService, developerService, pullRequestService, apiClient);
    }

    private JsonNode mockPullRequestSearchRequest(Integer page, Instant earliestDate, Optional<Fetch> laterThan)
            throws IOException {
        // Precondition
        if (page == 0 || page > PULL_REQUEST_SEARCH_PATH.size()) {
            throw new RuntimeException("Page index is invalid");
        }

        // Precondition
        if (laterThan.isPresent()) {
            // Return empty response for testing purposes and adjust count accordingly
            ClassPathResource resource = new ClassPathResource(PULL_REQUEST_SEARCH_PATH.get(3));
            JsonNode json = this.objectMapper.readTree(resource.getInputStream());
            ObjectNode objectNode = ((ObjectNode) json).put("total_count", 0);
            return (JsonNode) objectNode;
        }

        ClassPathResource resource = new ClassPathResource(PULL_REQUEST_SEARCH_PATH.get(page - 1));
        JsonNode json = this.objectMapper.readTree(resource.getInputStream());
        return json;
    }

    // This should be called within the fetch service only
    @Override
    public Integer fetchPullRequests(Optional<Fetch> laterThan) throws IOException {
        JsonNode currJson;
        Integer currPage = 1;
        Instant earliestDate = Instant.now().plus(1, ChronoUnit.DAYS);

        currJson = mockPullRequestSearchRequest(currPage, earliestDate, laterThan);
        PullRequestSearchDTO pullRequestSearchDTO = processPullRequestSearchJson(currJson);
        processPullRequestSearch(pullRequestSearchDTO);

        Integer totalCount = pullRequestSearchDTO.totalCount();
        for (int i = 1; i < PULL_REQUEST_SEARCH_PATH.size(); ++i) {
            earliestDate = pullRequestSearchDTO.earliestDate();

            currJson = mockPullRequestSearchRequest(i, earliestDate, laterThan);
            pullRequestSearchDTO = processPullRequestSearchJson(currJson);
            processPullRequestSearch(pullRequestSearchDTO);

            if (pullRequestSearchDTO.pullRequestResults().size() == 0)
                break;
        }

        return totalCount;
    }

}
