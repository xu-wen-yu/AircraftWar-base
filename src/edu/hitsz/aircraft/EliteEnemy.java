package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.supply.BaseSupply;
import edu.hitsz.supply.SupplyFactory;
import edu.hitsz.ballistic.Direct;

import java.util.LinkedList;
import java.util.List;

/**
 * 精英敌机
 * 可射击
 */
public class EliteEnemy extends AbstractAircraft implements ObserverAircraft {

    private final SupplyFactory bloodSupplyFactory;
    private final SupplyFactory bombSupplyFactory;
    private final SupplyFactory bulletSupplyFactory;
    private final SupplyFactory superBulletSupplyFactory;

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp,
                      SupplyFactory bloodSupplyFactory,
                      SupplyFactory bombSupplyFactory,
                      SupplyFactory bulletSupplyFactory,
                      SupplyFactory superBulletSupplyFactory) {
        super(locationX, locationY, speedX, speedY, hp);
        this.bloodSupplyFactory = bloodSupplyFactory;
        this.bombSupplyFactory = bombSupplyFactory;
        this.bulletSupplyFactory = bulletSupplyFactory;
        this.superBulletSupplyFactory = superBulletSupplyFactory;
        // 注入直射策略
        this.setShootStrategy(new Direct());
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        final int shootNum = 1;
        final int power = 10;
        final int direction = 1;
        int isHero = 0;
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction * 5;
        return shootStrategy.executeStrategy(x, y, speedX, speedY, shootNum, power, direction, isHero);
    }

    public List<BaseSupply> dropSupply() {
        List<BaseSupply> res = new LinkedList<>();
        double rand = Math.random();
        int x = this.getLocationX();
        int y = this.getLocationY();

        if(rand < 0.2 && bloodSupplyFactory != null) {
            res.add(bloodSupplyFactory.createSupply(x, y, 0, 5, 20));
        } else if(rand < 0.4 && bulletSupplyFactory != null) {
            res.add(bulletSupplyFactory.createSupply(x, y, 0, 5, 0));
        } else if(rand < 0.6 && bombSupplyFactory != null){
            res.add(bombSupplyFactory.createSupply(x, y, 0, 5, 0));
        } else if(rand >0.8 && superBulletSupplyFactory != null){
            res.add(superBulletSupplyFactory.createSupply(x, y, 0, 5, 0));
        }
        return res;
    }

    @Override
    public void update() {
        // Bomb effect: decrease hp by 30 for elite enemy
        decreaseHp(60);
    }
}