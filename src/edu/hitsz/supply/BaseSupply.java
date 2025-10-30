package edu.hitsz.supply;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.application.Main;

public abstract class BaseSupply extends AbstractFlyingObject {
    private int value;

    public BaseSupply(int locationX, int locationY, int speedX, int speedY, int value) {
        super(locationX, locationY, speedX, speedY);
        this.value = value;
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            // 向下飞行出界
            vanish();
        }
    }


    public int getValue() {
        return value;
    }
}