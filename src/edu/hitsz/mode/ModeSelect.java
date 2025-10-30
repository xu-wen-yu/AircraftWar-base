package edu.hitsz.mode;

import edu.hitsz.application.Game;

public abstract class ModeSelect {
    // 模板方法：应用难度到游戏，固定流程不可覆写
    @SuppressWarnings("unused")
    public final void apply(Game game) {
        configureEnemyMaxNumber(game);
        configureCycleDuration(game);
        configureBossThreshold(game);
        configureSpawnRates(game);
    }

    // 必须实现：最大敌机数量
    protected abstract void configureEnemyMaxNumber(Game game);

    // 可选步骤：刷新周期（ms），默认不改动
    protected abstract void configureCycleDuration(Game game);

    // 可选步骤：初始Boss分数阈值，默认不改动
    protected abstract void configureBossThreshold(Game game);

    // 可选步骤：刷怪概率（普通/精英；超级精英=1-两者之和），默认不改动
    protected abstract void configureSpawnRates(Game game);
}
