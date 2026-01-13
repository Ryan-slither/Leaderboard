package com.osc.leaderboard.developer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.osc.leaderboard.developer.dto.DeveloperDTO;
import com.osc.leaderboard.developer.model.Developer;
import com.osc.leaderboard.developer.repository.DeveloperRepository;

@Service
public class DeveloperService {

    private final DeveloperRepository developerRepository;

    public DeveloperService(DeveloperRepository developerRepository) {
        this.developerRepository = developerRepository;
    }

    public DeveloperDTO createDeveloper(String username, String avatarUrl) {
        Developer developerSaved = developerRepository.findByUsername(username).orElseGet(() -> {
            Developer developer = new Developer(null, username, avatarUrl);
            return developerRepository.save(developer);
        });

        return developerToDeveloperDTO(developerSaved);
    }

    public List<DeveloperDTO> findAllDevelopers() {
        return developerRepository.findAll().stream().map(DeveloperService::developerToDeveloperDTO).toList();
    }

    public static DeveloperDTO developerToDeveloperDTO(Developer developer) {
        return new DeveloperDTO(developer.getId(), developer.getUsername(), developer.getavatarUrl());
    }

}
