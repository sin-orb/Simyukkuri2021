package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriStealRule.
 */
public class BodyStealRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@Test
	void testHandleOkazariSteal_successfulStealTransfersOkazari() {
		Yukkuri thief = WorldTestHelper.createBody();
		Yukkuri target = WorldTestHelper.createBody();
		thief.setAgeState(AgeState.ADULT);
		target.setAgeState(AgeState.ADULT);
		thief.setAttitude(Attitude.SHITHEAD);
		thief.takeOkazari(false);
		target.setSleeping(true);
		thief.setToSteal(true);
		thief.setPublicRank(PublicRank.NONE);
		target.setPublicRank(PublicRank.NONE);
		target.setX(100);
		target.setY(100);
		thief.setX(100);
		thief.setY(100);

		assertTrue(YukkuriStealRule.handleOkazariSteal(target, thief));
		assertTrue(thief.hasOkazari());
		assertFalse(target.hasOkazari());
	}

	@Test
	void testHandleOkazariSteal_awakeWitnessBlocksSteal() {
		Yukkuri thief = WorldTestHelper.createBody();
		Yukkuri target = WorldTestHelper.createBody();
		Yukkuri witness = WorldTestHelper.createBody();
		thief.setAgeState(AgeState.ADULT);
		target.setAgeState(AgeState.ADULT);
		thief.setAttitude(Attitude.SHITHEAD);
		thief.takeOkazari(false);
		thief.setToSteal(true);
		thief.setPublicRank(PublicRank.NONE);
		target.setPublicRank(PublicRank.NONE);
		target.setX(100);
		target.setY(100);
		thief.setX(100);
		thief.setY(100);
		witness.setX(200);
		witness.setY(200);
		WorldTestHelper.initializeMinimalWorld();
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(thief.getUniqueID(), thief);
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(target.getUniqueID(), target);
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(witness.getUniqueID(), witness);

		assertFalse(YukkuriStealRule.handleOkazariSteal(target, thief));
		assertFalse(thief.hasOkazari());
		assertTrue(target.hasOkazari());
	}
}
