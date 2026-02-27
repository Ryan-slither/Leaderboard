package com.osc.leaderboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.osc.leaderboard.fetch.dtos.FetchDTO;
import com.osc.leaderboard.fetch.service.FetchService;

public class FetchServiceTests extends BaseTest {

    @Autowired
    private FetchService fetchService;

    @Test
    void serviceNotNull() {
        assertNotNull(fetchService);
    }

    @Test
    void createFetchTest() {
        FetchDTO fetchDTO = fetchService.createFetch();

        assertEquals(GithubServiceTests.TOTAL_STATED_COUNT, fetchDTO.pullRequestCount());

        Instant behindOneMinute = Instant.now().minus(1, ChronoUnit.MINUTES);
        Instant afterOneMinute = Instant.now().plus(1, ChronoUnit.MINUTES);
        assertTrue(fetchDTO.fetchedAt().compareTo(afterOneMinute) < 0
                && fetchDTO.fetchedAt().compareTo(behindOneMinute) > 0);
    }

}
