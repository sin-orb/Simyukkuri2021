package org.simyukkuri.logic;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

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
 * Tests for BodyStealRule.
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

		assertTrue(BodyStealRule.handleOkazariSteal(target, thief));
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
		org.simyukkuri.SimYukkuri.world.getCurrentMap().getBody().put(thief.getUniqueID(), thief);
		org.simyukkuri.SimYukkuri.world.getCurrentMap().getBody().put(target.getUniqueID(), target);
		org.simyukkuri.SimYukkuri.world.getCurrentMap().getBody().put(witness.getUniqueID(), witness);

		assertFalse(BodyStealRule.handleOkazariSteal(target, thief));
		assertFalse(thief.hasOkazari());
		assertTrue(target.hasOkazari());
	}
}
