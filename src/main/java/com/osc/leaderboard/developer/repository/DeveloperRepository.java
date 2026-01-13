package com.osc.leaderboard.developer.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.osc.leaderboard.developer.model.Developer;

public interface DeveloperRepository extends MongoRepository<Developer, String> {

    Optional<Developer> findByUsername(String username);

}
