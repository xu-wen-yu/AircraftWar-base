package edu.hitsz.supply;

public class BulletSupplyFactory implements SupplyFactory {
    @Override
    public BaseSupply createSupply(int locationX, int locationY, int speedX, int speedY, int value) {
        return new BulletSupply(locationX, locationY, speedX, speedY, value);
    }
}
