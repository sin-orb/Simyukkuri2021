package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriWakeupRule.
 */
public class BodyWakeupRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@Test
	void testCheckWakeupOtherYukkuri_returnsTrueWhenAwakeBodyVisible() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setEyesightBase(1);
		me.setX(10);
		me.setY(10);
		you.setX(200);
		you.setY(200);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueID(), you);

		assertTrue(YukkuriWakeupRule.checkWakeupOtherYukkuri(me));
	}

	@Test
	void testCheckWakeupOtherYukkuri_returnsFalseWhenVisibleBodySleeping() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setEyesightBase(1);
		me.setX(10);
		me.setY(10);
		you.setX(200);
		you.setY(200);
		you.setSleeping(true);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueID(), you);

		assertFalse(YukkuriWakeupRule.checkWakeupOtherYukkuri(me));
	}
}
