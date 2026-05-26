package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Test class for Cash.
 * Cash requires World/Player setup.
 */
public class CashTest {

    @BeforeEach
    public void setUp() {
        assertDoesNotThrow(WorldTestHelper::initializeMinimalWorld);
    }

    @Test
    public void testAddCashAdjustsPlayerBalanceExactly() {
        long initialCash = SimYukkuri.world.getPlayer().getCash();

        Cash.addCash(1000);

        assertEquals(initialCash + 1000, SimYukkuri.world.getPlayer().getCash());

        Cash.addCash(-500);

        assertEquals(initialCash + 500, SimYukkuri.world.getPlayer().getCash());
    }

    @Test
    public void testBuyItemSubtractsItemValueExactly() {
        Entity item = new Entity() {
            @Override
            public int getValue() {
                return 100;
            }
        };
        long initialCash = SimYukkuri.world.getPlayer().getCash();

        long cost = Cash.buyItem(item);

        assertEquals(100, cost);
        assertEquals(initialCash - 100, SimYukkuri.world.getPlayer().getCash());
    }

    @Test
    public void testBuyYukkuriBabyChargesExactlyOneThirdOfBaseCost() {
        Reimu baby = new Reimu();
        baby.setAgeState(AgeState.BABY);
        long initialCash = SimYukkuri.world.getPlayer().getCash();
        long expectedCost = baby.getCost() / 3;

        long cost = Cash.buyYukkuri(baby);

        assertEquals(expectedCost, cost);
        assertEquals(initialCash - expectedCost, SimYukkuri.world.getPlayer().getCash());
    }

    @Test
    public void testBuyYukkuriChildChargesExactlyOneHalfOfBaseCost() {
        Reimu child = new Reimu();
        child.setAgeState(AgeState.CHILD);
        long initialCash = SimYukkuri.world.getPlayer().getCash();
        long expectedCost = child.getCost() / 2;

        long cost = Cash.buyYukkuri(child);

        assertEquals(expectedCost, cost);
        assertEquals(initialCash - expectedCost, SimYukkuri.world.getPlayer().getCash());
    }

    @Test
    public void testBuyYukkuriAdultChargesFullCost() {
        Reimu adult = new Reimu();
        adult.setAgeState(AgeState.ADULT);
        long initialCash = SimYukkuri.world.getPlayer().getCash();
        long expectedCost = adult.getCost();

        long cost = Cash.buyYukkuri(adult);

        assertEquals(expectedCost, cost);
        assertEquals(initialCash - expectedCost, SimYukkuri.world.getPlayer().getCash());
    }

    @Test
    public void testSellWorthlessYukkuriReturnsZeroAndLeavesCashUnchanged() {
        Reimu damaged = new Reimu();
        damaged.setAge(20000);
        damaged.setDamage(10000);
        long initialCash = SimYukkuri.world.getPlayer().getCash();

        long value = Cash.sellYukkuri(damaged);

        assertEquals(0, value);
        assertEquals(initialCash, SimYukkuri.world.getPlayer().getCash());
    }

    @Test
    public void testSellHealthyChildPetAddsComputedPetValueToCash() {
        Marisa pet = new Marisa();
        pet.setAge(20000);
        pet.setAttitude(Attitude.AVERAGE);
        pet.setIntelligence(Intelligence.AVERAGE);
        long initialCash = SimYukkuri.world.getPlayer().getCash();
        long expectedValue = pet.getSellingPrice(0);

        long value = Cash.sellYukkuri(pet);

        assertEquals(expectedValue, value);
        assertEquals(initialCash + expectedValue, SimYukkuri.world.getPlayer().getCash());
    }

}
