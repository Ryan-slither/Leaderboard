package com.osc.leaderboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.osc.leaderboard.fetch.controller.FetchController;
import com.osc.leaderboard.fetch.dtos.FetchDTO;
import com.osc.leaderboard.fetch.service.FetchService;

public class FetchControllerTests extends BaseTest {

    @Autowired
    private FetchController fetchController;

    @Autowired
    private FetchService fetchService;

    @Test
    void controllerNotNull() {
        assertNotNull(fetchController);
    }

    @Test
    void fetchesByDateTest() {
        FetchDTO fetchDTO1 = fetchService.createFetch();
        FetchDTO fetchDTO2 = fetchService.createFetch();

        assertEquals(GithubServiceTests.TOTAL_STATED_COUNT, fetchDTO1.pullRequestCount());
        assertEquals(0, fetchDTO2.pullRequestCount());

        Instant behindOneMinute = Instant.now().minus(1, ChronoUnit.MINUTES);
        Instant aheadOneMinute = Instant.now().plus(1, ChronoUnit.MINUTES);

        try {
            fetchController.getFetchesByDate(null, null);
            assertTrue(false);
        } catch (BadRequestException e) {
            assertTrue(e instanceof BadRequestException);
        }

        try {
            List<FetchDTO> fetchesByDate1 = fetchController.getFetchesByDate(aheadOneMinute, null).getBody();
            assertTrue(fetchesByDate1.isEmpty());

            List<FetchDTO> fetchesByDate2 = fetchController.getFetchesByDate(null, behindOneMinute).getBody();
            assertTrue(fetchesByDate2.isEmpty());

            List<FetchDTO> fetchesByDate3 = fetchController.getFetchesByDate(behindOneMinute, null).getBody();
            assertEquals(fetchesByDate3.size(), 2);

            List<FetchDTO> fetchesByDate4 = fetchController.getFetchesByDate(null, aheadOneMinute).getBody();
            assertEquals(fetchesByDate4.size(), 2);

            List<FetchDTO> fetchesByDate5 = fetchController.getFetchesByDate(behindOneMinute, aheadOneMinute).getBody();
            assertEquals(fetchesByDate5.size(), 2);
        } catch (BadRequestException e) {
            assertTrue(false);
        }
    }

}
