package com.osc.leaderboard.fetch.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.osc.leaderboard.fetch.dtos.FetchDTO;
import com.osc.leaderboard.fetch.model.Fetch;
import com.osc.leaderboard.fetch.repository.FetchRepository;
import com.osc.leaderboard.github.service.GithubService;

@Service
public class FetchService {

    private GithubService githubService;

    private FetchRepository fetchRepository;

    public FetchService(GithubService githubService, FetchRepository fetchRepository) {
        this.githubService = githubService;
        this.fetchRepository = fetchRepository;
    }

    public FetchDTO createFetch() {
        // Check if fetch has already been done to know if you only need to query Github
        // API by later date or not
        Optional<Fetch> existingFetch = fetchRepository.findTopByOrderByFetchedAtDesc();

        Instant fetchedAt = Instant.now();
        try {
            Integer totalCount = githubService.fetchPullRequests(existingFetch);
            Fetch fetch = new Fetch(null, fetchedAt, totalCount);
            Fetch fetchSaved = fetchRepository.save(fetch);
            return fetchToFetchDTO(fetchSaved);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<FetchDTO> getFetchesByDateBetween(Instant start, Instant end) {
        return fetchRepository
                .findByFetchedAtBetween(start == null ? Instant.MIN : start,
                        end == null ? Instant.MAX : end)
                .stream()
                .map(FetchService::fetchToFetchDTO).toList();
    }

    public List<FetchDTO> getFetchesByDateBefore(Instant end) {
        return fetchRepository
                .findByFetchedAtLessThanEqual(end)
                .stream()
                .map(FetchService::fetchToFetchDTO).toList();
    }

    public List<FetchDTO> getFetchesByDateAfter(Instant start) {
        return fetchRepository
                .findByFetchedAtGreaterThanEqual(start)
                .stream()
                .map(FetchService::fetchToFetchDTO).toList();
    }

    public static FetchDTO fetchToFetchDTO(Fetch fetch) {
        return new FetchDTO(fetch.getId(), fetch.getFetchedAt(), fetch.getPullRequestCount());
    }

}
