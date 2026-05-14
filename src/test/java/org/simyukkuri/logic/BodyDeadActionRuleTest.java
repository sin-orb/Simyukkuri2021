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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriDeadActionRule.
 */
public class BodyDeadActionRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@Test
	void testHandleDeadBodyInteraction_excitingBodyHandlesDeadTarget() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri dead = WorldTestHelper.createBody();
		me.setExciting(true);
		me.setRaper(true);
		dead.setDead(true);

		assertTrue(YukkuriDeadActionRule.handleDeadYukkuriInteraction(dead, me));
	}

	@Test
	void testHandleDeadBodyInteraction_parentDeathMakesAdultVerySad() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri parent = WorldTestHelper.createBody();
		me.setAgeState(AgeState.ADULT);
		parent.setDead(true);
		WorldTestHelper.setParents(me, -1, parent.getUniqueID());

		assertTrue(YukkuriDeadActionRule.handleDeadYukkuriInteraction(parent, me));
		assertEquals(Happiness.VERY_SAD, me.getHappiness());
	}
}
