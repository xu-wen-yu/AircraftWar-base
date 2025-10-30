package edu.hitsz.supply;

public class SuperBulletSupplyFactory implements SupplyFactory {
    @Override
    public BaseSupply createSupply(int locationX, int locationY, int speedX, int speedY, int value) {
        return new SuperBulletSupply(locationX, locationY, speedX, speedY, value);
    }
}
