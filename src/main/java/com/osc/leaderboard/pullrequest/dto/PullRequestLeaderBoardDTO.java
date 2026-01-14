package com.osc.leaderboard.pullrequest.dto;

import jakarta.validation.constraints.NotNull;

public record PullRequestLeaderBoardDTO(@NotNull String repoId, @NotNull String repoName,
        @NotNull Integer pullRequestCount) {
}
