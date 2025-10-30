package edu.hitsz.ballistic;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.ArrayList;
import java.util.List;

public class Circle implements strategy {
    private final int angleCount;
    private final int bulletSpeed;

    public Circle(int angleCount, int bulletSpeed) {
        this.angleCount = angleCount;
        this.bulletSpeed = bulletSpeed;
    }

    @Override
    public List<BaseBullet> executeStrategy(int x, int y, int speedX, int speedY, int shootNum, int power, int direction, int isHero) {
        List<BaseBullet> res = new ArrayList<>(shootNum);
        for (int i = 0; i < angleCount; i++) {
            double angle = 2 * Math.PI * i / angleCount;
            int vx = (int) Math.round(Math.cos(angle) * bulletSpeed);
            int vy = (int) Math.round(Math.sin(angle) * bulletSpeed);
            if(isHero == 1)
                res.add(new HeroBullet(x, y, vx, vy, power));
            else
                res.add(new EnemyBullet(x, y, vx, vy, power));
        }
        return res;
    }
}