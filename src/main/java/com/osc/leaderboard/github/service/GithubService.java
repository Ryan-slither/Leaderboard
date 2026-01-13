package com.osc.leaderboard.github.service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.osc.leaderboard.developer.dto.DeveloperDTO;
import com.osc.leaderboard.developer.service.DeveloperService;
import com.osc.leaderboard.fetch.model.Fetch;
import com.osc.leaderboard.github.dtos.PullRequestResultDTO;
import com.osc.leaderboard.github.dtos.PullRequestSearchDTO;
import com.osc.leaderboard.pullrequest.service.PullRequestService;
import com.osc.leaderboard.repo.dtos.RepoDTO;
import com.osc.leaderboard.repo.service.RepoService;

@Service
public class GithubService {

    private static final List<String> PULL_REQUEST_SEARCH_PATH = new ArrayList<>(
            Arrays.asList("GithubSearchPullRequests1.json", "GithubSearchPullRequests2.json",
                    "GithubSearchPullRequests3.json", "GithubSearchPullRequests4.json"));

    private final ObjectMapper objectMapper;

    private final Environment env;

    private final RepoService repoService;

    private final DeveloperService developerService;

    private final PullRequestService pullRequestService;

    public GithubService(ObjectMapper objectMapper, Environment env, RepoService repoService,
            DeveloperService developerService, PullRequestService pullRequestService) {
        this.objectMapper = objectMapper;
        this.env = env;
        this.repoService = repoService;
        this.developerService = developerService;
        this.pullRequestService = pullRequestService;
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
            File file = resource.getFile();
            JsonNode json = objectMapper.readTree(file);
            ObjectNode objectNode = ((ObjectNode) json).put("total_count", 0);
            return (JsonNode) objectNode;
        }

        ClassPathResource resource = new ClassPathResource(PULL_REQUEST_SEARCH_PATH.get(page - 1));
        File file = resource.getFile();
        JsonNode json = objectMapper.readTree(file);
        return json;
    }

    private JsonNode pullRequestSearchRequest(Integer page, Instant earliestDate, Optional<Fetch> laterThan) {
        throw new NotImplementedException();
    }

    private PullRequestSearchDTO processPullRequestSearchJson(JsonNode json) {
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

            pullRequestResults.add(new PullRequestResultDTO(username, avatarUrl, repoName, mergedAt));
        }

        return new PullRequestSearchDTO(totalCount, pullRequestResults, earliestDate);
    }

    private void processPullRequestSearch(PullRequestSearchDTO pullRequestSearchDTO) {
        pullRequestSearchDTO.pullRequestResults().forEach(result -> {
            RepoDTO repoDTO = repoService.createRepo(result.repoName());
            DeveloperDTO developerDTO = developerService.createDeveloper(result.username(), result.avatarUrl());
            pullRequestService.createPullRequest(result.mergedAt(), developerDTO.id(), repoDTO.id());
        });
    }

    private JsonNode callPullRequestSearchRequest(Integer currPage, Instant earliestDate,
            Optional<Fetch> laterThan) {
        String target = env.getProperty("TARGET");
        JsonNode currJson;
        if (target == "prod") {
            currJson = pullRequestSearchRequest(currPage, earliestDate, laterThan);
        } else {
            try {
                currJson = mockPullRequestSearchRequest(currPage, earliestDate, laterThan);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return currJson;
    }

    // This should be called within the fetch service only
    public Integer fetchPullRequests(Optional<Fetch> laterThan) {
        JsonNode currJson;
        Integer currPage = 1;
        Instant earliestDate = Instant.now().plus(1, ChronoUnit.DAYS);

        currJson = callPullRequestSearchRequest(currPage, earliestDate, laterThan);

        PullRequestSearchDTO pullRequestSearchDTO = processPullRequestSearchJson(currJson);
        Integer totalCount = pullRequestSearchDTO.totalCount();

        if (totalCount > 100) {
            Integer totalPages = (int) Math.ceil(totalCount / 100);
            for (int i = 2; i < totalPages + 1; ++i) {
                earliestDate = pullRequestSearchDTO.earliestDate();

                currJson = callPullRequestSearchRequest(currPage, earliestDate, laterThan);

                pullRequestSearchDTO = processPullRequestSearchJson(currJson);
                processPullRequestSearch(pullRequestSearchDTO);
                totalCount = pullRequestSearchDTO.totalCount();

                if (i == 4 && env.getProperty("TARGET") != "prod")
                    break;

                // // So we don't spam the Github API and get clapped
                // // We may not need this yet:
                // https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api?apiVersion=2022-11-28#about-secondary-rate-limits
                // try {
                // TimeUnit.SECONDS.sleep(.5);
                // } catch (InterruptedException ie) {
                // Thread.currentThread().interrupt();
                // }
            }
        }

        return totalCount;
    }

}
