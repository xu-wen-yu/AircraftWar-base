package edu.hitsz.ballistic;

import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public interface strategy {
    List<BaseBullet> executeStrategy(int x, int y, int speedX, int speedY, int shootNum, int power, int direction, int isHero);
}
