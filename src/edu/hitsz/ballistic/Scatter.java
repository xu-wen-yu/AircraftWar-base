package edu.hitsz.ballistic;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Scatter implements strategy {
    @Override
    public List<BaseBullet> executeStrategy(int x, int y, int speedX, int speedY, int shootNum, int power, int direction, int isHero) {
        List<BaseBullet> res = new ArrayList<>(shootNum);
        int bx = x;
        int by = y + direction * 2;
        int vx = -10;
        int vy = speedY + direction * 10;
        for (int i = 0; i < 3; i++) {
                if(isHero == 1)
                    res.add(new HeroBullet(bx, by, vx, vy, power));
                else
                    res.add(new EnemyBullet(bx, by, vx, vy, power));
                vx += 10;
        }
        return res;
    }
}