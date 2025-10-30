package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeroAircraftTest {

    private HeroAircraft heroAircraft;
    @BeforeEach
    void setUp() {
        System.out.println("**--- Executed before each test method in this class ---**");
        heroAircraft = HeroAircraft.getHeroAircraft();
    }

    @AfterEach
    void tearDown() {
        System.out.println("**--- Executed after each test method in this class ---**");
        heroAircraft = null;
    }

    @Test
    void getHeroAircraft() {
        System.out.println("**--- Test getHeroAircraft method executed ---**");
        HeroAircraft anotherInstance = HeroAircraft.getHeroAircraft();
        assertSame(heroAircraft, anotherInstance);
    }

    @Test
    void increaseHp() {
        System.out.println("**--- Test increaseHp method executed ---**");
        int initialHp = heroAircraft.getHp();
        heroAircraft.increaseHp(20);
        assertEquals(Math.min(heroAircraft.maxHp, initialHp + 20), heroAircraft.getHp());
        heroAircraft.increaseHp(1000); // Exceeding max HP
        assertEquals(heroAircraft.getHp(), heroAircraft.maxHp);
    }

    @Test
    void forward() {
        System.out.println("**--- Test forward method executed ---**");
        int initialX = heroAircraft.getLocationX();
        int initialY = heroAircraft.getLocationY();
        heroAircraft.forward();
        // Since forward does nothing, positions should remain the same
        assertEquals(initialX, heroAircraft.getLocationX());
        assertEquals(initialY, heroAircraft.getLocationY());
    }}
