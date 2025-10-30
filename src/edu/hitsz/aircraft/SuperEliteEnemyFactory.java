package edu.hitsz.aircraft;

import edu.hitsz.supply.SupplyFactory;

public class SuperEliteEnemyFactory implements EnemyFactory{
    @Override
    public AbstractAircraft createEnemyWithSupplies(int locationX, int locationY, int speedX, int speedY, int hp,
                                                    SupplyFactory bloodSupplyFactory,
                                                    SupplyFactory bombSupplyFactory,
                                                    SupplyFactory bulletSupplyFactory,
                                                    SupplyFactory superBulletSupplyFactory) {
        return new SuperEliteEnemy(locationX, locationY, speedX, speedY, hp,
                bloodSupplyFactory, bombSupplyFactory, bulletSupplyFactory, superBulletSupplyFactory);
    }
}
