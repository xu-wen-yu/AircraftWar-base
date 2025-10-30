package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.supply.BaseSupply;
import edu.hitsz.supply.SupplyFactory;
import edu.hitsz.ballistic.Circle;

import java.util.LinkedList;
import java.util.List;

/**
 * Boss敌机
 * 可射击（环射）
 */
public class Boss extends AbstractAircraft {

    private int angleCount = 12; // 环射角度数量
    private int power = 30;
    private int bulletSpeed = 5;

    private final SupplyFactory bloodSupplyFactory;
    private final SupplyFactory bombSupplyFactory;
    private final SupplyFactory bulletSupplyFactory;
    private final SupplyFactory superBulletSupplyFactory;

    public Boss(int locationX, int locationY, int speedX, int speedY, int hp,
                SupplyFactory bloodSupplyFactory,
                SupplyFactory bombSupplyFactory,
                SupplyFactory bulletSupplyFactory,
                SupplyFactory superBulletSupplyFactory) {
        super(locationX, locationY, speedX, speedY, hp);
        this.bloodSupplyFactory = bloodSupplyFactory;
        this.bombSupplyFactory = bombSupplyFactory;
        this.bulletSupplyFactory = bulletSupplyFactory;
        this.superBulletSupplyFactory = superBulletSupplyFactory;
        // 注入环射策略（可配置角度与速度）
        this.setShootStrategy(new Circle(angleCount, bulletSpeed));
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
        int isHero = 0;
        int x = this.getLocationX();
        int y = this.getLocationY();
        int speedX = 0;
        int speedY = 0;
        int direction = 1; // 环射无方向意义
        return shootStrategy.executeStrategy(x, y, speedX, speedY, angleCount, power, direction, isHero);
    }

    public List<BaseSupply> dropSupply() {
        List<BaseSupply> res = new LinkedList<>();
        double rand = Math.random();
        int x = this.getLocationX();
        int y = this.getLocationY();

        int supplynum = 0;
        while(supplynum < 3) {
            if (rand < 0.2 && bloodSupplyFactory != null ) {
                res.add(bloodSupplyFactory.createSupply(x, y, -2, 5, 20));
            }
            if (rand < 0.4 && bulletSupplyFactory != null) {
                res.add(bulletSupplyFactory.createSupply(x, y, -1, 5, 0));
            }
            if (rand < 0.6 && bombSupplyFactory != null ) {
                res.add(bombSupplyFactory.createSupply(x, y, 1, 5, 0));
            }
            if (rand >= 0.8 && bloodSupplyFactory != null ) {
                res.add(superBulletSupplyFactory.createSupply(x, y, 2, 5, 0));
            }
            supplynum ++;
        }
        return res;
    }
}