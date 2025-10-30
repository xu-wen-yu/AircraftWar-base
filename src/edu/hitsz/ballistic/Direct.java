package edu.hitsz.ballistic;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Direct implements strategy {
    @Override
    public List<BaseBullet> executeStrategy(int x, int y, int speedX, int speedY, int shootNum, int power, int direction,int isHero) {
        List<BaseBullet> res = new ArrayList<>(shootNum);
        int bx = x;
        int by = y + direction * 2;
        int vx = 0;
        int vy = speedY + direction * 5;
        for (int i = 0; i < shootNum; i++) {
            if(isHero == 1)
                res.add(new HeroBullet(bx, by, vx, vy, power));
            else
                res.add(new EnemyBullet(bx, by, vx, vy, power));
        }
        return res;
    }
}