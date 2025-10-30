package edu.hitsz.aircraft;

    import edu.hitsz.bullet.BaseBullet;
    import edu.hitsz.basic.AbstractFlyingObject;
    import edu.hitsz.ballistic.strategy;

    import java.util.List;

    /**
     * 所有种类飞机的抽象父类：
     * 敌机（BOSS, ELITE, MOB），英雄飞机
     */
    public abstract class AbstractAircraft extends AbstractFlyingObject {
        protected int maxHp;
        protected int hp;

        // 弹道策略
        protected strategy shootStrategy;

        public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
            super(locationX, locationY, speedX, speedY);
            this.hp = hp;
            this.maxHp = hp;
        }

        public void setShootStrategy(strategy shootStrategy) {
            this.shootStrategy = shootStrategy;
        }

        public void decreaseHp(int decrease){
            hp -= decrease;
            if(hp <= 0){
                hp=0;
                vanish();
            }
        }

        public int getHp() {
            return hp;
        }

        /**
         * 飞机射击方法
         */
        public abstract List<BaseBullet> shoot();

        public void update() {
        }
    }