package edu.hitsz.score;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Score {
    private final int score;
    private final String scorename;
    private final LocalDateTime time;

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Score(int score, String scorename) {
        this(score, scorename, LocalDateTime.now());
    }

    public Score(int score, String scorename, LocalDateTime time) {
        this.score = score;
        this.scorename = scorename;
        this.time = time;
    }

    public int getScore() { return score; }
    public String getScorename() { return scorename; }
    public LocalDateTime getTime() { return time; }
    public String getTimeString() { return time.format(F); }
}