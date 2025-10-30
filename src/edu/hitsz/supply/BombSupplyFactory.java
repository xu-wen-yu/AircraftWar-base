package edu.hitsz.supply;

public class BombSupplyFactory implements SupplyFactory {
    @Override
    public BaseSupply createSupply(int locationX, int locationY, int speedX, int speedY, int value) {
        return new BombSupply(locationX, locationY, speedX, speedY, value);
    }
}
