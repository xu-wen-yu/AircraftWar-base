package edu.hitsz.aircraft;

import edu.hitsz.supply.SupplyFactory;

public class MobEnemyFactory implements EnemyFactory{
    @Override
    public AbstractAircraft createEnemyWithSupplies(int locationX, int locationY, int speedX, int speedY, int hp,
                                                  SupplyFactory bloodSupplyFactory,
                                                  SupplyFactory bombSupplyFactory,
                                                  SupplyFactory bulletSupplyFactory,
                                                    SupplyFactory superBulletSupplyFactory) {
        // 普通敌机不需要使用补给品工厂，直接返回普通敌机实例
        return new MobEnemy(locationX, locationY, speedX, speedY, hp);
    }
}
