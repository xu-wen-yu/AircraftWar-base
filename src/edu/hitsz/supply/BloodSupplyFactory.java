package edu.hitsz.supply;

public class BloodSupplyFactory implements SupplyFactory {
    @Override
    public BaseSupply createSupply(int locationX, int locationY, int speedX, int speedY, int value) {
        return new BloodSupply(locationX, locationY, speedX, speedY, value);
    }
}
