package edu.hitsz.aircraft;

import edu.hitsz.supply.SupplyFactory;

public interface EnemyFactory {
    /**
     * 创建带有供应品工厂的敌机
     * @param locationX x坐标
     * @param locationY y坐标
     * @param speedX x方向速度
     * @param speedY y方向速度
     * @param hp 生命值
     * @param bloodSupplyFactory 血包补给工厂
     * @param bombSupplyFactory 炸弹补给工厂
     * @param bulletSupplyFactory 子弹补给工厂
     * @return 创建的敌机
     */
    AbstractAircraft createEnemyWithSupplies(int locationX, int locationY, int speedX, int speedY, int hp,
                                            SupplyFactory bloodSupplyFactory,
                                            SupplyFactory bombSupplyFactory,
                                            SupplyFactory bulletSupplyFactory,
                                             SupplyFactory superBulletSupplyFactory);
}
