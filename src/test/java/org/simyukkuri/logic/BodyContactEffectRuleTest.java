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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.event.impl.AvoidMoldEvent;
import org.simyukkuri.event.impl.HateNoOkazariEvent;
import org.simyukkuri.ConstState;
import org.simyukkuri.util.WorldTestHelper;
import org.simyukkuri.system.Sprite;

/**
 * Tests for BodyContactEffectRule.
 */
class BodyContactEffectRuleTest {

	@Test
	void testHandleContactEffects_addsAvoidMoldEventForHealthyActor() {
		WorldTestHelper.initializeMinimalWorld();
		SimYukkuri.world.getCurrentMap().getEvent().clear();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setBodySpr(makeSprites(1, 1));
		you.setBodySpr(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(100);
		you.setY(100);
		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(you.getUniqueID(), you);
		me.setAgeState(AgeState.ADULT);
		you.setAgeState(AgeState.ADULT);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setIntelligence(org.simyukkuri.enums.Intelligence.AVERAGE);
		you.setSickPeriod(you.getIncubationPeriodBase() + 1);

		assertDoesNotThrow(() -> assertTrue(BodyContactEffectRule.handleContactEffects(you, me)));
		assertEquals(1, me.getEventList().size(), "actor should receive exactly one body event");
		assertTrue(me.getEventList().get(0) instanceof AvoidMoldEvent,
				"actor should receive an AvoidMoldEvent");
		assertNull(me.getCurrentEvent(), "mold avoidance should queue the event only");
	}

	@Test
	void testHandleContactEffects_addsHateNoOkazariWorldEvent() {
		WorldTestHelper.initializeMinimalWorld();
		SimYukkuri.world.getCurrentMap().getEvent().clear();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setBodySpr(makeSprites(1, 1));
		you.setBodySpr(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(100);
		you.setY(100);
		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(you.getUniqueID(), you);
		me.setAgeState(AgeState.ADULT);
		you.setAgeState(AgeState.BABY);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setIntelligence(org.simyukkuri.enums.Intelligence.FOOL);
		WorldTestHelper.setParents(you, -1, me.getUniqueID());
		you.takeOkazari(false);
		you.setCoreAnkoState(CoreAnkoState.NonYukkuriDiseaseNear);
		ConstState rnd = new ConstState(0);
		rnd.setFixedBoolean(true);
		SimYukkuri.RND = rnd;

		assertDoesNotThrow(() -> assertTrue(BodyContactEffectRule.handleContactEffects(you, me)));
		assertFalse(SimYukkuri.world.getCurrentMap().getEvent().isEmpty(),
				"world event queue should receive a HateNoOkazariEvent");
		assertTrue(SimYukkuri.world.getCurrentMap().getEvent().get(0) instanceof HateNoOkazariEvent,
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
