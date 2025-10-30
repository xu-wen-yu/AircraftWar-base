package music;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class SoundManager {

    public static final String BGM_PATH = "src/videos/bgm.wav";
    public static final String BOSS_BGM_PATH = "src/videos/bgm_boss.wav";
    public static final String BULLET_HIT_PATH = "src/videos/bullet_hit.wav";
    public static final String BOMB_EXPLOSION_PATH = "src/videos/bomb_explosion.wav";
    public static final String SUPPLY_EFFECT_PATH = "src/videos/get_supply.wav";
    public static final String GAME_OVER_PATH = "src/videos/game_over.wav";

    private static final SoundManager INSTANCE = new SoundManager();

    private final ExecutorService effectPool;
    private volatile boolean soundEnabled = true;
    private MusicThread backgroundThread;
    private MusicThread bossThread;
    private final Object backgroundLock = new Object();
    private final Object bossLock = new Object();

    private SoundManager() {
        ThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("sound-effect-%d")
                .daemon(true)
                .build();
        effectPool = Executors.newCachedThreadPool(factory);
    }

    public static SoundManager getInstance() {
        return INSTANCE;
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (!enabled) {
            stopBossMusic();
            stopBackground();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void playBackground(String path) {
        if (!soundEnabled) {
            return;
        }
        synchronized (backgroundLock) {
            if (backgroundThread != null) {
                backgroundThread.stopMusic();
            }
            backgroundThread = new MusicThread(path, true);
            backgroundThread.start();
        }
    }

    public void stopBackground() {
        synchronized (backgroundLock) {
            if (backgroundThread != null) {
                backgroundThread.stopMusic();
                backgroundThread = null;
            }
        }
    }

    public void playBossMusic(String path) {
        if (!soundEnabled) {
            return;
        }
        synchronized (bossLock) {
            stopBackground();
            if (bossThread != null) {
                bossThread.stopMusic();
            }
            bossThread = new MusicThread(path, true);
            bossThread.start();
        }
    }

    public void stopBossMusic() {
        synchronized (bossLock) {
            if (bossThread != null) {
                bossThread.stopMusic();
                bossThread = null;
            }
        }
    }

    public void playEffect(String path) {
        if (!soundEnabled) {
            return;
        }
        effectPool.execute(() -> {
            MusicThread effectThread = new MusicThread(path, false);
            effectThread.playOnce();
        });
    }

    public void onGameOver() {
        if (!soundEnabled) {
            return;
        }
        stopBossMusic();
        stopBackground();
        playEffect(GAME_OVER_PATH);
    }
}
