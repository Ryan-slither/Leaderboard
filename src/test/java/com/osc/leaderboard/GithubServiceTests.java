package com.osc.leaderboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.osc.leaderboard.github.service.MockGithubService;
import com.osc.leaderboard.pullrequest.dto.PullRequestDTO;
import com.osc.leaderboard.pullrequest.dto.PullRequestLeaderBoardDTO;
import com.osc.leaderboard.pullrequest.service.PullRequestService;
import com.osc.leaderboard.repo.dtos.RepoDTO;
import com.osc.leaderboard.repo.service.RepoService;

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

    public static final Map<String, Integer> repoCountMap = new HashMap<>(Map.ofEntries(
            Map.entry("Jukebox-Frontend", JUKEBOX_FRONTEND_COUNT),
            Map.entry("Jukebox-Server", JUKEBOX_SERVER_COUNT),
            Map.entry("TERMINALCASINO", TERMINAL_CASINO_COUNT),
            Map.entry("OSC_Workout_App", WORKOUT_COUNT),
            Map.entry("Studygatchi", STUDY_GATCHI_COUNT),
            Map.entry("Club_Website_2", WEBSITE_TWO_COUNT),
            Map.entry("TERMINALMONOPOLY", TERMINAL_MONOPOLY_COUNT),
            Map.entry("hackathon-website", HACKATHON_COUNT),
            Map.entry("VisualNovel", NOVEL_COUNT),
            Map.entry("hide-and-seek", HIDE_SEEK_COUNT),
            Map.entry("Echo-Chat", ECHO_CHAT_COUNT),
            Map.entry("osc-drone", DRONE_COUNT)));

    @Autowired
    private MockGithubService githubService;

    @Autowired
    private PullRequestService pullRequestService;

    @Autowired
    private RepoService repoService;

    @Test
    void serviceNotNull() {
        assertNotNull(githubService);
    }

    @Test
    // Fetch pull requests runs without error for mocked api call and returns
    // correct total count
    void fetchPullRequestsTest() throws IOException {
        long startTime = System.nanoTime();

        githubService.fetchPullRequests(Optional.empty());
        List<PullRequestDTO> pullRequestDTOs = pullRequestService.findAllPullRequests();
        assertEquals(TOTAL_ACTUAL_COUNT, pullRequestDTOs.size());

        List<RepoDTO> repoDTOs = repoService.findAllRepos();
        Map<ObjectId, String> repoIdToNameMap = new HashMap<>();
        for (RepoDTO repo : repoDTOs) {
            repoIdToNameMap.put(new ObjectId(repo.id()), repo.name());
        }
        assertEquals(repoIdToNameMap.size(), repoCountMap.size());

        List<PullRequestLeaderBoardDTO> pullRequestLeaderBoardDTOs = pullRequestService.getPullRequestLeaderboard();
        for (PullRequestLeaderBoardDTO pr : pullRequestLeaderBoardDTOs) {
            assertEquals(repoCountMap.get(pr.repoName()), pr.pullRequestCount());
        }

        for (PullRequestDTO pr : pullRequestDTOs) {
            String repoName = repoIdToNameMap.get(pr.repoId());
            Integer repoCount = repoCountMap.get(repoIdToNameMap.get(pr.repoId()));

            assertTrue(repoCount - 1 >= 0);

            repoCountMap.put(repoName, repoCount - 1);
        }

        repoCountMap.forEach((repoName, count) -> {
            assertEquals(0, count);
        });

        long endTime = System.nanoTime();
        logger.info("Time taken for JSON parsing: " + ((endTime - startTime) / 1_000_000_000.0));
    }

}
