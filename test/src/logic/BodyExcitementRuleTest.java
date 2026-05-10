package src.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.ConstState;
import src.SimYukkuri;
import src.base.Yukkuri;
import src.base.StubBodyAttributes;
import src.enums.AgeState;
import src.enums.PublicRank;
import src.system.Sprite;
import src.util.WorldTestHelper;

/**
 * Tests for BodyExcitementRule.
 */
class BodyExcitementRuleTest {

	@Test
	void testHandleExcitingContact_raperBranchConsumesAction() {
		WorldTestHelper.initializeMinimalWorld();
		SimYukkuri.world.getCurrentMap().getEvent().clear();
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
		you.setAgeState(AgeState.ADULT);
		me.setExciting(true);
		me.setRaper(true);
		you.setRaper(false);
		SimYukkuri.RND = new ConstState(0);

		assertDoesNotThrow(() -> assertTrue(BodyExcitementRule.handleExcitingContact(you, me)));
		assertFalse(me.isToBody(), "rape branch should consume the contact action without switching targets");
	}

	@Test
	void testHandleExcitingContact_adultPartnerFallsBackToSukkiri() {
		WorldTestHelper.initializeMinimalWorld();
		SimYukkuri.world.getCurrentMap().getEvent().clear();
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
		you.setAgeState(AgeState.ADULT);
		me.setExciting(true);
		me.setRaper(false);
		me.setStress(150);
		you.setStress(90);
		me.setPartner(you.getUniqueID());
		you.setPartner(me.getUniqueID());

		assertDoesNotThrow(() -> assertTrue(BodyExcitementRule.handleExcitingContact(you, me)));
	}

	@Test
	void testHandleExcitingContact_forceExcitingContinues() {
		WorldTestHelper.initializeMinimalWorld();
		SimYukkuri.world.getCurrentMap().getEvent().clear();
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
		you.setAgeState(AgeState.BABY);
		me.setExciting(true);
		me.setForceExciting(true);
		me.setRaper(false);

		assertDoesNotThrow(() -> assertFalse(BodyExcitementRule.handleExcitingContact(you, me)));
		assertTrue(me.isSukkiri(), "force exciting should still trigger sukkiri side effect");
	}

	private static Sprite[] makeSprites(int w, int h) {
		Sprite[] spr = new Sprite[3];
		for (int i = 0; i < 3; i++) {
			spr[i] = new Sprite(w, h, Sprite.PIVOT_CENTER_BOTTOM);
		}
		return spr;
	}
}
