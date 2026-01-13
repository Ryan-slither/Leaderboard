package com.osc.leaderboard;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.osc.leaderboard.developer.dto.DeveloperDTO;
import com.osc.leaderboard.developer.service.DeveloperService;
import com.osc.leaderboard.fetch.service.FetchService;

public class DeveloperServiceTests extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DeveloperServiceTests.class);

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private FetchService fetchService;

    @Test
    void servicesNotNull() {
        assertNotNull(developerService);
        assertNotNull(fetchService);
    }

    @Test
    void createDevelopersOnFetchTest() {
        fetchService.createFetch();

        HashSet<String> developerNames = new HashSet<>();

        List<DeveloperDTO> developerDTOs = developerService.findAllDevelopers();
        assertTrue(developerDTOs.size() > 0);
        developerDTOs.forEach(developer -> {
            assertFalse(developerNames.contains(developer.username()));
            developerNames.add(developer.username());
            logger.info(developer.username());
        });
    }
}
