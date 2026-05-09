package src.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Yukkuri;
import src.enums.AgeState;
import src.enums.PublicRank;
import src.system.Sprite;
import src.util.WorldTestHelper;

/**
 * Tests for BodyDeadSearchRule.
 */
class BodyDeadSearchRuleTest {

	@Test
	void testHandleDeadFound_excitingBranchMovesToSukkiri() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setBodySpr(makeSprites(1, 1));
		you.setBodySpr(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(120);
		you.setY(100);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setAgeState(AgeState.ADULT);
		you.setDead(true);
		me.setExciting(true);

		assertDoesNotThrow(() -> assertTrue(BodyDeadSearchRule.handleDeadFound(me, you, 0, 0)));
	}

	@Test
	void testHandleDeadFound_randomSkipReturnsFalse() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setBodySpr(makeSprites(1, 1));
		you.setBodySpr(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(120);
		you.setY(100);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setAgeState(AgeState.ADULT);
		you.setDead(true);

		assertDoesNotThrow(() -> assertFalse(BodyDeadSearchRule.handleDeadFound(me, you, 0, 0)));
	}

	private static Sprite[] makeSprites(int w, int h) {
		Sprite[] spr = new Sprite[3];
		for (int i = 0; i < 3; i++) {
			spr[i] = new Sprite(w, h, Sprite.PIVOT_CENTER_BOTTOM);
		}
		return spr;
	}
}
