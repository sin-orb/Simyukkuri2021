package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriParentRule.
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
		assertDoesNotThrow(() -> YukkuriParentRule.checkNearParent(me));
	}

	@Test
	void testCheckNearParent_callingParentsWakesSleepingParent() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri parent = WorldTestHelper.createBody();
		me.setAgeState(AgeState.CHILD);
		me.setCallingParents(true);
		parent.setSleeping(true);
		WorldTestHelper.setParents(me, -1, parent.getUniqueId());
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(parent.getUniqueId(), parent);

		YukkuriParentRule.checkNearParent(me);

		assertFalse(parent.isSleeping());
	}
}
