package edu.hitsz.score;

import java.util.List;

public interface ScoreDao {
    void doAdd(Score s);
    List<Score> getAllScores();
    void removeScores(List<Score> scores);
}