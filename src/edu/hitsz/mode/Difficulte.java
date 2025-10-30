package edu.hitsz.mode;

import edu.hitsz.application.Game;

public class Difficulte extends ModeSelect{
    @Override
    protected void configureEnemyMaxNumber(Game game) { Game.setEnemyMaxNumber(10); }

    @Override
    protected void configureCycleDuration(Game game) {
        game.setCycleDuration(500);
    }

    @Override
    protected void configureBossThreshold(Game game) {
        game.setBossInitialThreshold(400);
    }

    @Override
    protected void configureSpawnRates(Game game) {
        game.setSpawnProbabilities(0.45, 0.35); // super = 0.20
    }
}
