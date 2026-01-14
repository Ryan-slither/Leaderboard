package com.osc.leaderboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.osc.leaderboard.github.service.GithubService;
import com.osc.leaderboard.pullrequest.dto.PullRequestDTO;
import com.osc.leaderboard.pullrequest.service.PullRequestService;

class GithubServiceTests extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GithubServiceTests.class);

    // Amount stated in mock data
    public static final Integer TOTAL_STATED_COUNT = 1330;

    // Actual amount of prs in mock data
    public static final Integer TOTAL_ACTUAL_COUNT = 300;

    // Counts for prs in mock data
    public static final Integer TERMINAL_MONOPOLY_COUNT = 51;
    public static final Integer ECHO_CHAT_COUNT = 15;
    public static final Integer DRONE_COUNT = 2;
    public static final Integer JUKEBOX_FRONTEND_COUNT = 59;
    public static final Integer STUDY_GATCHI_COUNT = 21;
    public static final Integer WEBSITE_TWO_COUNT = 21;
    public static final Integer HACKATHON_COUNT = 2;
    public static final Integer JUKEBOX_SERVER_COUNT = 42;
    public static final Integer TERMINAL_CASINO_COUNT = 41;
    public static final Integer HIDE_SEEK_COUNT = 20;
    public static final Integer WORKOUT_COUNT = 11;
    public static final Integer NOVEL_COUNT = 15;

    @Autowired
    private GithubService githubService;

    @Autowired
    private PullRequestService pullRequestService;

    @Test
    void serviceNotNull() {
        assertNotNull(githubService);
    }

    @Test
    // Fetch pull requests runs without error for mocked api call and returns
    // correct total count
    void fetchPullRequestsTest() {
        long startTime = System.nanoTime();

        githubService.fetchPullRequests(Optional.empty());
        List<PullRequestDTO> pullRequestDTOs = pullRequestService.findAllPullRequests();
        assertEquals(TOTAL_ACTUAL_COUNT, pullRequestDTOs.size());

        // TODO: TEST ALL COUNTS
        assertTrue(true);

        long endTime = System.nanoTime();
        logger.info("Time taken for JSON parsing: " + ((endTime - startTime) / 1_000_000_000.0));
    }

}
