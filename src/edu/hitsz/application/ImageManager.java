package edu.hitsz.application;


import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.aircraft.SuperEliteEnemy;
import edu.hitsz.aircraft.Boss;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.supply.BloodSupply;
import edu.hitsz.supply.BombSupply;
import edu.hitsz.supply.BulletSupply;
import edu.hitsz.supply.SuperBulletSupply;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 综合管理图片的加载，访问
 * 提供图片的静态访问方法
 *
 * @author hitsz
 */
public class ImageManager {

    /**
     * 类名-图片 映射，存储各基类的图片 <br>
     * 可使用 CLASSNAME_IMAGE_MAP.get( obj.getClass().getName() ) 获得 obj 所属基类对应的图片
     */
    private static final Map<String, BufferedImage> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static BufferedImage BACKGROUND_IMAGE;
    public static BufferedImage HERO_IMAGE;
    public static BufferedImage HERO_BULLET_IMAGE;
    public static BufferedImage ENEMY_BULLET_IMAGE;
    public static BufferedImage MOB_ENEMY_IMAGE;
    public static BufferedImage ELITE_ENEMY_IMAGE;
    public static BufferedImage BLOOD_SUPPLY_IMAGE;
    public static BufferedImage BOMB_SUPPLY_IMAGE;
    public static BufferedImage BULLET_SUPPLY_IMAGE;
    public static BufferedImage SUPER_ELITE_ENEMY_IMAGE;
    public static BufferedImage BOSS_IMAGE;
    public static BufferedImage SUPER_BULLET_SUPPLY_IMAGE;

    static {
        try {
            // 默认背景（若加载失败，会在运行时通过 loadBackground 设置）
            try {
                BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg.jpg"));
            } catch (IOException ignored) {
                BACKGROUND_IMAGE = null;
            }

            HERO_IMAGE = ImageIO.read(new FileInputStream("src/images/hero.png"));
            MOB_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/mob.png"));
            ELITE_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elite.png"));
            HERO_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_hero.png"));
            ENEMY_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_enemy.png"));
            BLOOD_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_blood.png"));
            BOMB_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bomb.png"));
            BULLET_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bullet.png"));
            SUPER_ELITE_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePlus.png"));
            BOSS_IMAGE = ImageIO.read(new FileInputStream("src/images/boss.png"));
            SUPER_BULLET_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bulletPlus.png"));

            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BloodSupply.class.getName(), BLOOD_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BombSupply.class.getName(), BOMB_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BulletSupply.class.getName(), BULLET_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(SuperEliteEnemy.class.getName(), SUPER_ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(Boss.class.getName(), BOSS_IMAGE);
            CLASSNAME_IMAGE_MAP.put(SuperBulletSupply.class.getName(), SUPER_BULLET_SUPPLY_IMAGE);

        } catch (IOException e) {
            // 打印错误但不退出，这样 UI 可以继续运行并在运行时切换背景
            e.printStackTrace();
        }
    }

    /**
     * 在运行时加载并设置背景图片（传入相对或绝对路径）
     */
    public static void loadBackground(String path) {
        try {
            BACKGROUND_IMAGE = ImageIO.read(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            // 不抛出异常，调用方可选择回退
        }
    }

    public static BufferedImage get(String className){
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static BufferedImage get(Object obj){
        if (obj == null){
            return null;
        }
        return get(obj.getClass().getName());
    }

}
