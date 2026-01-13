package com.osc.leaderboard.pullrequest.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record PullRequestDTO(@NotNull String id, @NotNull Instant mergedAt, @NotNull String developerId,
        @NotNull String repoId) {
}
