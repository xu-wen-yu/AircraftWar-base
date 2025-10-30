package edu.hitsz.mode;

import edu.hitsz.application.Game;

public class Easy extends ModeSelect{
    @Override
    protected void configureEnemyMaxNumber(Game game) { Game.setEnemyMaxNumber(3); }

    @Override
    protected void configureCycleDuration(Game game) {
        game.setCycleDuration(700);
    }

    @Override
    protected void configureBossThreshold(Game game) {
        game.setBossInitialThreshold(800);
    }

    @Override
    protected void configureSpawnRates(Game game) {
        game.setSpawnProbabilities(0.70, 0.25); // super = 0.05
    }
}
