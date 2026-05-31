package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriExcitementRule.
 */
class BodyExcitementRuleTest {

	@Test
	void testHandleExcitingContact_raperBranchConsumesAction() {
		WorldTestHelper.initializeMinimalWorld();
		SimYukkuri.world.getCurrentWorldState().getEvents().clear();
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
		you.setAgeState(AgeState.ADULT);
		me.setExciting(true);
		me.setRaper(true);
		you.setRaper(false);
		SimYukkuri.RND = new ConstState(0);

		assertTrue(YukkuriExcitementRule.handleExcitingContact(you, me),
				"raper ブランチは true を返すこと");
		assertFalse(me.isToYukkuri(), "rape ブランチはターゲット切替なしでアクションを消費すること");
		assertTrue(me.isSukkiri(), "doRape により me が sukkiri 状態になること");
	}

	@Test
	void testHandleExcitingContact_adultPartnerFallsBackToSukkiri() {
		WorldTestHelper.initializeMinimalWorld();
		SimYukkuri.world.getCurrentWorldState().getEvents().clear();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		// isPartner() は yukkuriRegistry 経由で解決するため登録が必要
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueId(), you);
		me.setSpriteSet(makeSprites(1, 1));
		you.setSpriteSet(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(120);
		you.setY(100);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setAgeState(AgeState.ADULT);
		you.setAgeState(AgeState.ADULT);
		me.setExciting(true);
		me.setRaper(false);
		me.setStress(150);
		you.setStress(90);
		me.setPartner(you.getUniqueId());
		you.setPartner(me.getUniqueId());

		assertTrue(YukkuriExcitementRule.handleExcitingContact(you, me),
				"パートナーとの接触で true を返すこと");
		assertTrue(me.isSukkiri(), "doSukkiri により me が sukkiri 状態になること");
	}

	@Test
	void testHandleExcitingContact_forceExcitingContinues() {
		WorldTestHelper.initializeMinimalWorld();
		SimYukkuri.world.getCurrentWorldState().getEvents().clear();
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
		you.setAgeState(AgeState.BABY);
		me.setExciting(true);
		me.setForceExciting(true);
		me.setRaper(false);

		assertFalse(YukkuriExcitementRule.handleExcitingContact(you, me),
				"BABY 相手で forceExciting ブランチは false を返すこと");
		assertTrue(me.isSukkiri(), "forceExciting ブランチでも doSukkiri によりすっきり状態になること");
	}

	private static Sprite[] makeSprites(int w, int h) {
		Sprite[] spr = new Sprite[3];
		for (int i = 0; i < 3; i++) {
			spr[i] = new Sprite(w, h, Sprite.PIVOT_CENTER_BOTTOM);
		}
		return spr;
	}
}
