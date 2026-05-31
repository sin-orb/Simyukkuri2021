package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Happiness;
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

		int initialStress = thief.getStress();
		assertTrue(YukkuriStealRule.handleOkazariSteal(target, thief),
				"盗み成功で true を返すこと");
		// okazari の移転を確認
		assertTrue(thief.hasOkazari(),    "盗み成功で thief がおかざりを持つこと");
		assertFalse(target.hasOkazari(), "盗み成功で target からおかざりが無くなること");
		// 盗み成功時の副作用確認
		// giveOkazari が VERY_HAPPY を設定し、後続の setHappiness(HAPPY) はそれより低いため変化しない
		assertEquals(Happiness.VERY_HAPPY, thief.getHappiness(), "盗み成功で thief が VERY_HAPPY になること");
		assertTrue(thief.getStress() < initialStress,       "盗み成功で thief のストレスが減ること");
	}

	@Test
	void testHandleOkazariSteal_awakeWitnessBlocksSteal() {
		Yukkuri thief = WorldTestHelper.createBody();
		Yukkuri target = WorldTestHelper.createBody();
		final Yukkuri witness = WorldTestHelper.createBody();
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
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(thief.getUniqueId(), thief);
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(target.getUniqueId(), target);
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(witness.getUniqueId(), witness);

		Happiness initialHappiness = thief.getHappiness();
		assertFalse(YukkuriStealRule.handleOkazariSteal(target, thief),
				"目撃者がいると盗めず false を返すこと");
		// 盗み失敗 → okazari は移転していないこと
		assertFalse(thief.hasOkazari(),  "目撃者がいると thief はおかざりを得ないこと");
		assertTrue(target.hasOkazari(), "目撃者がいると target のおかざりは保持されること");
		// happiness は変化しないこと（盗み失敗では副作用なし）
		assertEquals(initialHappiness, thief.getHappiness(), "盗み失敗で thief の happiness は変化しないこと");
	}
}
