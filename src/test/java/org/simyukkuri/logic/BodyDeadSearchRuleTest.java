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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
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

		assertDoesNotThrow(() -> assertTrue(YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0)));
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

		assertDoesNotThrow(() -> assertFalse(YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0)));
	}

	private static Sprite[] makeSprites(int w, int h) {
		Sprite[] spr = new Sprite[3];
		for (int i = 0; i < 3; i++) {
			spr[i] = new Sprite(w, h, Sprite.PIVOT_CENTER_BOTTOM);
		}
		return spr;
	}
}
