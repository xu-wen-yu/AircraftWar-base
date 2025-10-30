package edu.hitsz.supply;

public interface SupplyFactory {
    BaseSupply createSupply(int locationX, int locationY, int speedX, int speedY, int value);
}
