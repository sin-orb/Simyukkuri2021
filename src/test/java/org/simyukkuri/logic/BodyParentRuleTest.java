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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for BodyParentRule.
 */
public class BodyParentRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@Test
	void testCheckNearParent_returnsWhenAdult() {
		Yukkuri me = WorldTestHelper.createBody();
		me.setAge((long) me.getChildLimitBase());
		assertDoesNotThrow(() -> BodyParentRule.checkNearParent(me));
	}

	@Test
	void testCheckNearParent_callingParentsWakesSleepingParent() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri parent = WorldTestHelper.createBody();
		me.setAgeState(AgeState.CHILD);
		me.setCallingParents(true);
		parent.setSleeping(true);
		WorldTestHelper.setParents(me, -1, parent.getUniqueID());
		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);

		BodyParentRule.checkNearParent(me);

		assertFalse(parent.isSleeping());
	}
}
