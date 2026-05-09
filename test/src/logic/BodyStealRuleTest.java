package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.base.Yukkuri;
import src.enums.AgeState;
import src.enums.Attitude;
import src.enums.PublicRank;
import src.util.WorldTestHelper;

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
		src.SimYukkuri.world.getCurrentMap().getBody().put(thief.getUniqueID(), thief);
		src.SimYukkuri.world.getCurrentMap().getBody().put(target.getUniqueID(), target);
		src.SimYukkuri.world.getCurrentMap().getBody().put(witness.getUniqueID(), witness);

		assertFalse(BodyStealRule.handleOkazariSteal(target, thief));
		assertFalse(thief.hasOkazari());
		assertTrue(target.hasOkazari());
	}
}
