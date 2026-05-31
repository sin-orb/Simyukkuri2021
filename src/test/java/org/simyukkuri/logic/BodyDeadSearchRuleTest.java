package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriDeadSearchRule.
 */
class BodyDeadSearchRuleTest {

	@Test
	void testHandleDeadFound_excitingBranchMovesToSukkiri() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setSpriteSet(makeSprites(1, 1));
		you.setSpriteSet(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(120);
		you.setY(100);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setAgeState(AgeState.ADULT);
		you.setDead(true);
		me.setExciting(true);
		me.setTargetBind(true);  // exciting ブランチで false に変わることを確認するため事前に true

		assertTrue(YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0),
				"exciting ブランチは true を返すこと");
		assertFalse(me.isTargetBind(),
				"exciting ブランチで setTargetBind(false) が呼ばれること");
	}

	@Test
	void testHandleDeadFound_randomSkipReturnsFalse() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setSpriteSet(makeSprites(1, 1));
		you.setSpriteSet(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(120);
		you.setY(100);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setAgeState(AgeState.ADULT);
		you.setDead(true);

		assertFalse(YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0));
	}

	private static Sprite[] makeSprites(int w, int h) {
		Sprite[] spr = new Sprite[3];
		for (int i = 0; i < 3; i++) {
			spr[i] = new Sprite(w, h, Sprite.PIVOT_CENTER_BOTTOM);
		}
		return spr;
	}
}
