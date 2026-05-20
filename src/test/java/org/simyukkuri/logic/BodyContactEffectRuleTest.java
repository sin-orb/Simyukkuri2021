package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.event.impl.AvoidMoldEvent;
import org.simyukkuri.event.impl.HateNoOkazariEvent;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriContactEffectRule.
 */
class BodyContactEffectRuleTest {

	@Test
	void testHandleContactEffects_addsAvoidMoldEventForHealthyActor() {
		WorldTestHelper.initializeMinimalWorld();
		SimYukkuri.world.getCurrentWorldState().getEvents().clear();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setSpriteSet(makeSprites(1, 1));
		you.setSpriteSet(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(100);
		you.setY(100);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueID(), you);
		me.setAgeState(AgeState.ADULT);
		you.setAgeState(AgeState.ADULT);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setIntelligence(org.simyukkuri.enums.Intelligence.AVERAGE);
		you.setSickPeriod(you.getIncubationPeriodBase() + 1);

		assertDoesNotThrow(() -> assertTrue(YukkuriContactEffectRule.handleContactEffects(you, me)));
		assertEquals(1, me.getEvents().size(), "actor should receive exactly one body event");
		assertTrue(me.getEvents().get(0) instanceof AvoidMoldEvent,
				"actor should receive an AvoidMoldEvent");
		assertNull(me.getCurrentEvent(), "mold avoidance should queue the event only");
	}

	@Test
	void testHandleContactEffects_addsHateNoOkazariWorldEvent() {
		WorldTestHelper.initializeMinimalWorld();
		SimYukkuri.world.getCurrentWorldState().getEvents().clear();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setSpriteSet(makeSprites(1, 1));
		you.setSpriteSet(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(100);
		you.setY(100);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueID(), you);
		me.setAgeState(AgeState.ADULT);
		you.setAgeState(AgeState.BABY);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setIntelligence(org.simyukkuri.enums.Intelligence.FOOL);
		WorldTestHelper.setParents(you, -1, me.getUniqueID());
		you.takeOkazari(false);
		you.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE_NEAR);
		ConstState rnd = new ConstState(0);
		rnd.setFixedBoolean(true);
		SimYukkuri.RND = rnd;

		assertDoesNotThrow(() -> assertTrue(YukkuriContactEffectRule.handleContactEffects(you, me)));
		assertFalse(SimYukkuri.world.getCurrentWorldState().getEvents().isEmpty(),
				"world event queue should receive a HateNoOkazariEvent");
		assertTrue(SimYukkuri.world.getCurrentWorldState().getEvents().get(0) instanceof HateNoOkazariEvent,
				"world event queue should receive a HateNoOkazariEvent");
	}

	private static Sprite[] makeSprites(int w, int h) {
		Sprite[] spr = new Sprite[3];
		for (int i = 0; i < 3; i++) {
			spr[i] = new Sprite(w, h, Sprite.PIVOT_CENTER_BOTTOM);
		}
		return spr;
	}
}
