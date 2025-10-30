package edu.hitsz.mode;

import edu.hitsz.application.Game;

public class Medium extends ModeSelect{
    @Override
    protected void configureEnemyMaxNumber(Game game) { Game.setEnemyMaxNumber(5); }

    @Override
    protected void configureCycleDuration(Game game) {
        game.setCycleDuration(600);
    }

    @Override
    protected void configureBossThreshold(Game game) {
        game.setBossInitialThreshold(500);
    }

    @Override
    protected void configureSpawnRates(Game game) {
        game.setSpawnProbabilities(0.55, 0.25); // super = 0.20
    } }
