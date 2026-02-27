package com.osc.leaderboard.github.service;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osc.leaderboard.developer.service.DeveloperService;
import com.osc.leaderboard.fetch.model.Fetch;
import com.osc.leaderboard.github.dtos.PullRequestSearchDTO;
import com.osc.leaderboard.pullrequest.service.PullRequestService;
import com.osc.leaderboard.repo.service.RepoService;

@Service
@Profile("dev")
public class RealGithubService extends GithubService {

    private static final String GITHUB_BASE_URL = "https://api.github.com";

    public RealGithubService(ObjectMapper objectMapper, Environment env, RepoService repoService,
            DeveloperService developerService, PullRequestService pullRequestService, WebClient apiClient) {
        super(objectMapper, env, repoService, developerService, pullRequestService, apiClient);
    }

    // TODO: FIX TOTAL COUNT ONLY USING LAST FETCH AND DEDUPLICATE RESULTS WITH
    // NODE_ID
    private JsonNode pullRequestSearchRequest(Integer page, Instant earliestDate, Optional<Fetch> laterThan)
            throws RuntimeException {
        String dateString = earliestDate.toString();
        String dateBoundary = "+created:";
        if (laterThan.isPresent()) {
            dateString = laterThan.get().getFetchedAt().toString();
            dateBoundary += ">=";
        } else {
            dateBoundary += "<=";
        }
        String dateQuery = dateBoundary + dateString;

        // Create custom GitHub QueryBuilder in future refactor
        URI uri = UriComponentsBuilder.fromUriString(GITHUB_BASE_URL).path("/search/issues")
                .queryParam("q", "org:ufosc+is:pr+is:merged+sort:created" + dateQuery)
                .queryParam("per_page", 100)
                .queryParam("page", page)
                .queryParam("order", "desc")
                .build().toUri();
        System.out.println(uri.toString());

        String result = this.apiClient.get()
                .uri(uri)
                .headers(h -> {
                    h.setBearerAuth(env.getProperty("GITHUB_API_KEY"));
                })
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();

        try {
            return this.objectMapper.readTree(result);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // This should be called within the fetch service only
    @Override
    public Integer fetchPullRequests(Optional<Fetch> laterThan) {
        JsonNode currJson;
        Integer currPage = 1;
        Instant earliestDate = Instant.now().plus(1, ChronoUnit.DAYS);

        currJson = pullRequestSearchRequest(currPage, earliestDate, laterThan);
        PullRequestSearchDTO pullRequestSearchDTO = processPullRequestSearchJson(currJson);
        processPullRequestSearch(pullRequestSearchDTO);
        Integer totalCount = pullRequestSearchDTO.totalCount();

        if (totalCount > 100) {
            Integer totalPages = (int) Math.ceil(totalCount / 100);
            for (int i = 1; i < totalPages + 1; ++i) {
                earliestDate = pullRequestSearchDTO.earliestDate();

                currJson = pullRequestSearchRequest(i, earliestDate, laterThan);
                pullRequestSearchDTO = processPullRequestSearchJson(currJson);
                processPullRequestSearch(pullRequestSearchDTO);

                if (pullRequestSearchDTO.pullRequestResults().size() == 0)
                    break;
            }
        }

        return totalCount;
    }

}
