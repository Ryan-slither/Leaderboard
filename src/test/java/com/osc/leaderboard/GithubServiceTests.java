package com.osc.leaderboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.osc.leaderboard.github.service.GithubService;

class GithubServiceTests extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GithubServiceTests.class);

    public static final Integer TOTAL_COUNT = 1330;

    @Autowired
    private GithubService githubService;

    @Test
    void serviceNotNull() {
        assertNotNull(githubService);
    }

    @Test
    // Fetch pull requests runs without error for mocked api call and returns
    // correct total count
    void fetchPullRequestsTest() {
        long startTime = System.nanoTime();
        Integer result = githubService.fetchPullRequests(Optional.empty());
        assertEquals(result, TOTAL_COUNT);
        long endTime = System.nanoTime();
        logger.info("Time taken for JSON parsing: " + ((endTime - startTime) / 1_000_000_000.0));
    }

}
