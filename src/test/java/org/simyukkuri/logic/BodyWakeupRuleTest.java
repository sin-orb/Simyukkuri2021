package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.PublicRank;
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
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueId(), you);

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
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueId(), you);

		assertFalse(YukkuriWakeupRule.checkWakeupOtherYukkuri(me));
	}

	@Test
	void testCheckWakeupOtherYukkuri_returnsFalseWhenOtherIsDead() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setEyesightBase(1);
		me.setX(10);
		me.setY(10);
		you.setX(200);
		you.setY(200);
		you.setDead(true);
		you.setSleeping(false);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueId(), you);

		assertFalse(YukkuriWakeupRule.checkWakeupOtherYukkuri(me),
				"死亡ゆっくりは起床判定に含まれないこと");
	}

	@Test
	void testCheckWakeupOtherYukkuri_returnsFalseWhenOtherIsNyd() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setEyesightBase(1);
		me.setX(10);
		me.setY(10);
		you.setX(200);
		you.setY(200);
		you.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
		you.setSleeping(false);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueId(), you);

		assertFalse(YukkuriWakeupRule.checkWakeupOtherYukkuri(me),
				"NYD ゆっくりは起床判定に含まれないこと");
	}

	@Test
	void testCheckWakeupOtherYukkuri_returnsFalseWhenNoneRankActorAndUnunSlaveOther() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setEyesightBase(1);
		me.setX(10);
		me.setY(10);
		you.setX(200);
		you.setY(200);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.UNUN_SLAVE);
		you.setSleeping(false);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueId(), you);

		assertFalse(YukkuriWakeupRule.checkWakeupOtherYukkuri(me),
				"NONE ランクは UNUN_SLAVE の目覚めを検知しないこと");
	}

	@Test
	void testCheckWakeupOtherYukkuri_returnsFalseWhenOtherIsBuried() {
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setEyesightBase(1);
		me.setX(10);
		me.setY(10);
		you.setX(200);
		you.setY(200);
		you.setBurialState(BurialState.ALL);
		you.setSleeping(false);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(you.getUniqueId(), you);

		assertFalse(YukkuriWakeupRule.checkWakeupOtherYukkuri(me),
				"埋没中のゆっくりは起床判定に含まれないこと");
	}
}
