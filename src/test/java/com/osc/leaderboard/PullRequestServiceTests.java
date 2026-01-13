package com.osc.leaderboard;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.osc.leaderboard.fetch.service.FetchService;
import com.osc.leaderboard.pullrequest.dto.PullRequestDTO;
import com.osc.leaderboard.pullrequest.service.PullRequestService;

public class PullRequestServiceTests extends BaseTest {

    @Autowired
    private PullRequestService pullRequestService;

    @Autowired
    private FetchService fetchService;

    @Test
    void servicesNotNull() {
        assertNotNull(pullRequestService);
        assertNotNull(fetchService);
    }

    @Test
    void createPullRequestsOnFetchTest() {
        fetchService.createFetch();

        List<PullRequestDTO> pullRequestDTOs = pullRequestService.findAllPullRequests();
        assertTrue(pullRequestDTOs.size() > 0);
    }
}
