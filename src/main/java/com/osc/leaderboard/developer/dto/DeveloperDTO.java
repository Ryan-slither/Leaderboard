package com.osc.leaderboard.developer.dto;

import jakarta.validation.constraints.NotNull;

public record DeveloperDTO(@NotNull String id, @NotNull String username, @NotNull String avatarUrl) {
}
