package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.ballistic.Circle;
import edu.hitsz.ballistic.Direct;
import edu.hitsz.ballistic.Scatter;
import edu.hitsz.ballistic.strategy;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.mode.ModeSelect;
import edu.hitsz.supply.*;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.score.ExportRecords;
import music.SoundManager;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.random;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public class Game extends JPanel {

    private int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private final int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<BaseSupply> bloodSupplies;
    private final List<BaseSupply> bombSupplies;
    private final List<BaseSupply> bulletSupplies;
    private final List<BaseSupply> superbulletSupplies;
    private final SoundManager soundManager;
    private final ScheduledExecutorService powerUpExecutor;
    private ScheduledFuture<?> heroPowerUpResetTask;
    private final Object heroPowerUpLock = new Object();
    private final AtomicInteger heroPowerUpVersion = new AtomicInteger();
    private static final long BULLET_SUPPLY_DURATION = 8000L;
    private static final long SUPER_BULLET_DURATION = 12000L;
    private final AtomicBoolean gameOverHandled = new AtomicBoolean(false);
    
    /**
     * 敌机工厂
     */
    private final EnemyFactory mobEnemyFactory;
    private final EnemyFactory eliteEnemyFactory;
    
    /**
     * 补给品工厂
     */
    private final SupplyFactory bloodSupplyFactory;
    private final SupplyFactory bombSupplyFactory;
    private final SupplyFactory bulletSupplyFactory;
    private final SupplyFactory superBulletSupplyFactory;

    /**
     * 炸弹主题（观察者模式）
     */
    private final BombActive bombActive = new BombActive();

    /**
     * 屏幕中出现的敌机最大数量
     */
    private static int enemyMaxNumber = 5;
    /**
     * 当前得分
     */
    private int score = 0;
    /**
     * 当前时刻
     */
    private int time = 0;

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;

    /**
     * Boss是否存在的标志
     */
    public static boolean bossExists = false;
    
    /**
     * 下一次Boss出现的分数阈值
     */
    private int nextBossScoreThreshold = 500;

    // ===== 新增：刷怪概率参数（默认值与原逻辑兼容） =====
    private double mobSpawnProb = 0.55;
    private double eliteSpawnProb = 0.25; // super = 1 - (mob+elite)

    public Game(boolean soundEnabled, ModeSelect mode) {
        heroAircraft = HeroAircraft.getHeroAircraft();
        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        bloodSupplies = new LinkedList<>();
        bombSupplies = new LinkedList<>();
        bulletSupplies = new LinkedList<>();
        superbulletSupplies = new LinkedList<>();
        soundManager = SoundManager.getInstance();
        soundManager.setSoundEnabled(soundEnabled);
        powerUpExecutor = Executors.newSingleThreadScheduledExecutor(
        new BasicThreadFactory.Builder().namingPattern("hero-powerup-%d").daemon(true).build());
        
        // 初始化敌机工厂
        mobEnemyFactory = new MobEnemyFactory();
        eliteEnemyFactory = new EliteEnemyFactory();
        
        // 初始化补给品工厂
        bloodSupplyFactory = new BloodSupplyFactory();
        bombSupplyFactory = new BombSupplyFactory();
        bulletSupplyFactory = new BulletSupplyFactory();
        superBulletSupplyFactory = new SuperBulletSupplyFactory();

        /*
          Scheduled 线程池，用于定时任务调度
          关于alibaba code guide：可命名的 ThreadFactory 一般需要第三方包
          apache 第三方库： org.apache.commons.lang3.concurrent.BasicThreadFactory
         */
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

        // 应用难度配置
        if (mode != null) {
            mode.apply(this);
        }
    }

    public static void setEnemyMaxNumber(int enemynumber) {
        enemyMaxNumber = enemynumber;
    }

    // ===== 新增：模板配置的 setter =====
    public void setCycleDuration(int cycleDuration) {
        this.cycleDuration = Math.max(200, cycleDuration);
    }

    public void setBossInitialThreshold(int threshold) {
        this.nextBossScoreThreshold = Math.max(0, threshold);
    }

    public void setSpawnProbabilities(double mob, double elite) {
        mob = Math.max(0.0, mob);
        elite = Math.max(0.0, elite);
        double sum = mob + elite;
        if (sum > 1.0) { // 归一化
            mob /= sum;
            elite /= sum;
        }
        this.mobSpawnProb = mob;
        this.eliteSpawnProb = elite;
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {
        if (soundManager.isSoundEnabled()) {
            soundManager.playBackground(SoundManager.BGM_PATH);
        }
        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;


            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
                // 新敌机产生
                if (enemyAircrafts.size() < enemyMaxNumber) {
                    // 敌机生成逻辑使用可配置概率
                    double r = random();
                    if (r < mobSpawnProb) {
                        // 普通敌机
                        AbstractAircraft mob = mobEnemyFactory.createEnemyWithSupplies(
                                (int) (random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                                (int) (random() * Main.WINDOW_HEIGHT * 0.05),
                                0,
                                10,
                                30,
                                bloodSupplyFactory,
                                bombSupplyFactory,
                                bulletSupplyFactory,
                                superBulletSupplyFactory
                        );
                        enemyAircrafts.add(mob);
                        if (mob instanceof ObserverAircraft) {
                            bombActive.addAircraft((ObserverAircraft) mob);
                        }
                    }
                    else if(r < mobSpawnProb + eliteSpawnProb){
                        // 精英敌机
                        AbstractAircraft elite = eliteEnemyFactory.createEnemyWithSupplies(
                                (int) (random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                                (int) (random() * Main.WINDOW_HEIGHT * 0.05),
                                0,
                                10,
                                60,
                                bloodSupplyFactory,
                                bombSupplyFactory,
                                bulletSupplyFactory,
                                superBulletSupplyFactory
                        );
                        enemyAircrafts.add(elite);
                        if (elite instanceof ObserverAircraft) {
                            bombActive.addAircraft((ObserverAircraft) elite);
                        }
                    }
                    else{
                        // 超级精英敌机
                        AbstractAircraft se = new SuperEliteEnemy(
                                (int) (random() * (Main.WINDOW_WIDTH - ImageManager.SUPER_ELITE_ENEMY_IMAGE.getWidth())),
                                (int) (random() * Main.WINDOW_HEIGHT * 0.05),
                                5 - (int)(random()*10),
                                8,
                                100,
                                bloodSupplyFactory,
                                bombSupplyFactory,
                                bulletSupplyFactory,
                                superBulletSupplyFactory
                        );
                        enemyAircrafts.add(se);
                        bombActive.addAircraft((ObserverAircraft) se);
                    }
                    CreateBoss();
                }

                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            // 游戏结束检查英雄机是否存活
            if (heroAircraft.getHp() <= 0) {
                if (gameOverHandled.compareAndSet(false, true)) {
                    // 游戏结束
                    executorService.shutdown();
                    soundManager.onGameOver();
                    cancelHeroPowerUpTask();
                    resetHeroStrategyImmediate();
                    if (!powerUpExecutor.isShutdown()) {
                        powerUpExecutor.shutdownNow();
                    }
                    int finalScore = score;
                    SwingUtilities.invokeLater(() -> ExportRecords.exportRecord(Game.this, finalScore));
                    System.out.println("Game Over!");
                }
            }

        };

        /*
          以固定延迟时间进行执行
          本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    }

    private void applyHeroPowerUp(strategy newStrategy, long durationMillis) {
        int version = heroPowerUpVersion.incrementAndGet();
        heroAircraft.setShootStrategy(newStrategy);
        synchronized (heroPowerUpLock) {
            if (heroPowerUpResetTask != null && !heroPowerUpResetTask.isDone()) {
                heroPowerUpResetTask.cancel(false);
            }
            heroPowerUpResetTask = powerUpExecutor.schedule(() -> resetHeroStrategy(version),
                    durationMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void resetHeroStrategy(int version) {
        if (heroPowerUpVersion.get() != version) {
            return;
        }
        heroAircraft.setShootStrategy(new Direct());
        synchronized (heroPowerUpLock) {
            heroPowerUpResetTask = null;
        }
    }

    private void resetHeroStrategyImmediate() {
        heroPowerUpVersion.incrementAndGet();
        heroAircraft.setShootStrategy(new Direct());
        synchronized (heroPowerUpLock) {
            heroPowerUpResetTask = null;
        }
    }

    private void cancelHeroPowerUpTask() {
        synchronized (heroPowerUpLock) {
            if (heroPowerUpResetTask != null && !heroPowerUpResetTask.isDone()) {
                heroPowerUpResetTask.cancel(false);
            }
            heroPowerUpResetTask = null;
        }
    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        // TODO 敌机射击
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }
    
    /**
     * 检查并创建Boss敌机
     * 当分数达到阈值且当前没有Boss存在时创建Boss
     */
    private void CreateBoss() {
        // 检查是否达到分数阈值且当前没有Boss存在
        if (score >= nextBossScoreThreshold && !bossExists) {
            // 创建Boss敌机
            Boss boss = new Boss(
                    (int) (random() * (Main.WINDOW_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                    (int) (random() * Main.WINDOW_HEIGHT * 0.05),
                    5 - (int) (random() * 10),
                    0,
                    300,
                    bloodSupplyFactory,
                    bombSupplyFactory,
                    bulletSupplyFactory,
                    superBulletSupplyFactory
            );
            enemyAircrafts.add(boss);
            bossExists = true;
            soundManager.playBossMusic(SoundManager.BOSS_BGM_PATH);
            
            // 增加下一次Boss出现的分数阈值（难度递增）
            nextBossScoreThreshold += 1000;
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
        // 补给道具移动
        for (BaseSupply supply : bloodSupplies) {
            supply.forward();
        }
        for (BaseSupply supply : bombSupplies) {
            supply.forward();
        }
        for (BaseSupply supply : bulletSupplies) {
            supply.forward();
        }
        for (BaseSupply supply : superbulletSupplies) {
            supply.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.notValid()) {
                // 英雄机已挂，不再检测
                break;
            }
            if (heroAircraft.crash(bullet) || bullet.crash(heroAircraft)) {
                // 英雄机撞击到敌机子弹
                // 英雄机损失一定生命值
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    soundManager.playEffect(SoundManager.BULLET_HIT_PATH);
                    if (enemyAircraft.notValid()) {
                        // 获得分数，产生道具补给
                        score += 10;
                        if (enemyAircraft instanceof EliteEnemy || enemyAircraft instanceof SuperEliteEnemy || enemyAircraft instanceof Boss) {
                            score += 10;
                            // 产生道具补给
                            List<BaseSupply> supplies;
                            if (enemyAircraft instanceof SuperEliteEnemy) {
                                supplies = ((SuperEliteEnemy) enemyAircraft).dropSupply();
                            } else if(enemyAircraft instanceof EliteEnemy) {
                                supplies = ((EliteEnemy) enemyAircraft).dropSupply();
                            } else{
                                supplies = ((Boss) enemyAircraft).dropSupply();
                                // Boss被击败，重置Boss存在标志
                                bossExists = false;
                                soundManager.stopBossMusic();
                                if (heroAircraft.getHp() > 0) {
                                    soundManager.playBackground(SoundManager.BGM_PATH);
                                }
                            }
                            // 从炸弹观察者注册表注销
                            if (enemyAircraft instanceof ObserverAircraft) {
                                bombActive.removeAircraft((ObserverAircraft) enemyAircraft);
                            }
                            for (BaseSupply supply : supplies) {
                                if (supply instanceof BloodSupply) {
                                    bloodSupplies.add(supply);
                                } else if (supply instanceof BombSupply) {
                                    bombSupplies.add(supply);
                                } else if (supply instanceof BulletSupply) {
                                    bulletSupplies.add(supply);
                                }
                                else if (supply instanceof SuperBulletSupply) {
                                    superbulletSupplies.add(supply);
                                }
                            }
                        }
                    }
                }


                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    // 从炸弹观察者注册表注销
                    if (enemyAircraft instanceof ObserverAircraft) {
                        bombActive.removeAircraft((ObserverAircraft) enemyAircraft);
                    }
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // Todo: 我方获得道具，道具生效
        // 在 crashCheckAction() 末尾补全英雄机获得道具的判定
        // 英雄机获得血量补给
        Iterator<BaseSupply> bloodIt = bloodSupplies.iterator();
        while (bloodIt.hasNext()) {
            BaseSupply supply = bloodIt.next();
            if (heroAircraft.crash(supply)) {
                heroAircraft.increaseHp(supply.getValue());
                soundManager.playEffect(SoundManager.SUPPLY_EFFECT_PATH);
                supply.vanish();
                bloodIt.remove();
            }
        }
        // 英雄机获得炸弹补给
        // 临时列表：避免在遍历 bombSupplies 时向其新增元素，导致并发修改异常
        List<BaseSupply> pendingBloodSupplies = new ArrayList<>();
        List<BaseSupply> pendingBombSupplies = new ArrayList<>();
        List<BaseSupply> pendingBulletSupplies = new ArrayList<>();
        List<BaseSupply> pendingSuperBulletSupplies = new ArrayList<>();
        Iterator<BaseSupply> bombIt = bombSupplies.iterator();
        while (bombIt.hasNext()) {
            BaseSupply supply = bombIt.next();
            if (heroAircraft.crash(supply)) {
                soundManager.playEffect(SoundManager.BOMB_EXPLOSION_PATH);
                // 记录通知前仍然有效的敌机
                List<AbstractAircraft> preValid = new ArrayList<>();
                for (AbstractAircraft e : enemyAircrafts) {
                    if (!e.notValid()) preValid.add(e);
                }
                // 触发炸弹效果：由主题统一通知所有观察者敌机更新状态
                bombActive.catchBombSupply();
                // 对被炸毁的敌机结算得分与掉落
                for (AbstractAircraft enemy : preValid) {
                    if (enemy.notValid()) {
                        score += 10;
                        if (enemy instanceof EliteEnemy || enemy instanceof SuperEliteEnemy || enemy instanceof Boss) {
                            score += 10;
                            List<BaseSupply> supplies;
                            if (enemy instanceof SuperEliteEnemy) {
                                supplies = ((SuperEliteEnemy) enemy).dropSupply();
                            } else if (enemy instanceof EliteEnemy) {
                                supplies = ((EliteEnemy) enemy).dropSupply();
                            } else {
                                supplies = Collections.emptyList();
                            }
                            // 从炸弹观察者注册表注销
                            if (enemy instanceof ObserverAircraft) {
                                bombActive.removeAircraft((ObserverAircraft) enemy);
                            }
                            for (BaseSupply s : supplies) {
                                if (s instanceof BloodSupply) {
                                    pendingBloodSupplies.add(s);
                                } else if (s instanceof BombSupply) {
                                    pendingBombSupplies.add(s);
                                } else if (s instanceof BulletSupply) {
                                    pendingBulletSupplies.add(s);
                                } else if (s instanceof SuperBulletSupply) {
                                    pendingSuperBulletSupplies.add(s);
                                }
                            }
                        }
                    }
                }
                // 敌机子弹全部清空
                for (BaseBullet bullet : enemyBullets) {
                    bullet.vanish();
                }
                supply.vanish();
                bombIt.remove();
            }
        }
        // 统一在遍历结束后再合并新增补给，避免遍历期间修改列表
        if (!pendingBloodSupplies.isEmpty()) {
            bloodSupplies.addAll(pendingBloodSupplies);
        }
        if (!pendingBombSupplies.isEmpty()) {
            bombSupplies.addAll(pendingBombSupplies);
        }
        if (!pendingBulletSupplies.isEmpty()) {
            bulletSupplies.addAll(pendingBulletSupplies);
        }
        if (!pendingSuperBulletSupplies.isEmpty()) {
            superbulletSupplies.addAll(pendingSuperBulletSupplies);
        }

        // 英雄机获得子弹补给
        Iterator<BaseSupply> bulletIt = bulletSupplies.iterator();
        while (bulletIt.hasNext()) {
            BaseSupply supply = bulletIt.next();
            if (heroAircraft.crash(supply)) {
                soundManager.playEffect(SoundManager.SUPPLY_EFFECT_PATH);
                applyHeroPowerUp(new Scatter(), BULLET_SUPPLY_DURATION);
                supply.vanish();
                bulletIt.remove();
            }
        }
        //英雄机获得超级子弹补给
        Iterator<BaseSupply> superBulletIt = superbulletSupplies.iterator();
        while (superBulletIt.hasNext()) {
            BaseSupply supply = superBulletIt.next();
            if (heroAircraft.crash(supply)) {
                soundManager.playEffect(SoundManager.SUPPLY_EFFECT_PATH);
                applyHeroPowerUp(new Circle(12, 5), SUPER_BULLET_DURATION);
                supply.vanish();
                superBulletIt.remove();
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        // 在移除无效敌机前，先从炸弹观察者注销
        for (AbstractAircraft a : enemyAircrafts) {
            if (a.notValid() && a instanceof ObserverAircraft) {
                bombActive.removeAircraft((ObserverAircraft) a);
            }
        }
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        if (bossExists) {
            boolean hasBoss = false;
            for (AbstractAircraft aircraft : enemyAircrafts) {
                if (aircraft instanceof Boss) {
                    hasBoss = true;
                    break;
                }
            }
            if (!hasBoss) {
                bossExists = false;
                soundManager.stopBossMusic();
                if (heroAircraft.getHp() > 0) {
                    soundManager.playBackground(SoundManager.BGM_PATH);
                }
            }
        }
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param g 画笔对象
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);

        paintImageWithPositionRevised(g, enemyAircrafts);
        paintImageWithPositionRevised(g, bloodSupplies);
        paintImageWithPositionRevised(g, bombSupplies);
        paintImageWithPositionRevised(g, bulletSupplies);
        paintImageWithPositionRevised(g, superbulletSupplies);
        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }
}
