package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.entity.core.living.yukkuri.Yukkuri;
import src.util.WorldTestHelper;

/**
 * Tests for BodyWakeupRule.
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
		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(you.getUniqueID(), you);

		assertTrue(BodyWakeupRule.checkWakeupOtherYukkuri(me));
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
		SimYukkuri.world.getCurrentMap().getBody().put(me.getUniqueID(), me);
		SimYukkuri.world.getCurrentMap().getBody().put(you.getUniqueID(), you);

		assertFalse(BodyWakeupRule.checkWakeupOtherYukkuri(me));
	}
}
