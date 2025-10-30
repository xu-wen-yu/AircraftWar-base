package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.ballistic.Direct;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {
    private volatile static HeroAircraft heroAircraft;
    private int shootNum = 1;
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp){
        super(locationX, locationY, speedX, speedY, hp);
        this.setShootStrategy(new Direct());
    }
    public static HeroAircraft getHeroAircraft(){
        if(heroAircraft == null){
            synchronized (HeroAircraft.class){
                if(heroAircraft == null){
                    heroAircraft = new HeroAircraft(Main.WINDOW_WIDTH / 2,
                            Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight() ,
                            0, 0, 100);
                }
            }
        }
        return heroAircraft;
    }
    /*攻击方式 */


    public void increaseHp(int hp) {
        this.hp = Math.min(this.maxHp, this.hp + hp);
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /*
      通过射击产生子弹
      @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        int power = 30;
        int isHero = 1;
        int x = this.getLocationX();
        int direction = -1;
        int y = this.getLocationY() + direction *10;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction *5;
        return shootStrategy.executeStrategy(x, y, speedX, speedY, shootNum, power, direction, isHero);
    }
}
