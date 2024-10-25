package net.dungeonhub.model.score;

import lombok.Getter;
import lombok.Setter;
import me.taubsie.dungeonhub.common.DungeonHubService;

import java.util.List;
import java.util.Optional;

@Getter
public class LeaderboardModel {
    private final int page;
    private final int totalPages;
    private final List<ScoreModel> scores;

    @Setter
    private Integer playerPosition;
    @Setter
    private ScoreModel playerScore;

    public LeaderboardModel(int page, int totalPages, List<ScoreModel> scores) {
        this.page = page;
        this.totalPages = totalPages;
        this.scores = scores;
    }

    public static LeaderboardModel fromJson(String json) {
        return DungeonHubService.getInstance()
                .getGson()
                .fromJson(json, LeaderboardModel.class);
    }

    public Optional<Integer> getPlayerPosition() {
        return Optional.ofNullable(playerPosition);
    }

    public Optional<ScoreModel> getPlayerScore() {
        return Optional.ofNullable(playerScore);
    }

    public boolean hasNextPage() {
        return page < totalPages - 1;
    }

    public boolean hasPrevPage() {
        return page > 0;
    }
}