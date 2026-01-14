package com.osc.leaderboard.pullrequest.dto;

import java.time.Instant;

import org.bson.types.ObjectId;

import jakarta.validation.constraints.NotNull;

public record PullRequestDTO(@NotNull String id, @NotNull Instant mergedAt, @NotNull ObjectId developerId,
                @NotNull ObjectId repoId) {
}
