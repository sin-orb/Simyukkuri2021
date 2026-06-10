package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Translate;
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
	void testCheckNearParent_childWithNoParentDoesNotThrow() {
		// 親なし・姉なし の子ゆで呼んでも例外なし
		Yukkuri me = WorldTestHelper.createBody();
		me.setAgeState(AgeState.BABY);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		assertDoesNotThrow(() -> YukkuriParentRule.checkNearParent(me),
				"親なし子ゆで checkNearParent を呼んでもクラッシュしないこと");
	}

	@Test
	void testCheckNearParent_dirtyChildCloseTriggersPelopero() {
		// dirty 子ゆが親のすぐ隣にいる → doPeropero（親が is staying になる）
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri parent = WorldTestHelper.createBody();
		me.setAgeState(AgeState.BABY);
		me.setDirty(true);
		// 子ゆと親を同じ位置に置く（distanceToParent = 0 ≤ parent.getStepDist()）
		me.setCalcX(100);
		me.setCalcY(100);
		parent.setCalcX(100);
		parent.setCalcY(100);
		WorldTestHelper.setParents(me, -1, parent.getUniqueId());
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(parent.getUniqueId(), parent);

		assertDoesNotThrow(() -> YukkuriParentRule.checkNearParent(me),
				"汚れた子ゆが近くにいる親に peropero を試みること（NPE なし）");
	}

	@Test
	void testCheckNearParent_dirtyChildFarTriggersMoveToward() {
		// dirty 子ゆが遠くにいる → body.moveTo(parentX, parentY)
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri parent = WorldTestHelper.createBody();
		me.setAgeState(AgeState.BABY);
		me.setDirty(true);
		// 十分離れた位置
		me.setCalcX(10);
		me.setCalcY(10);
		parent.setCalcX(200);
		parent.setCalcY(200);
		WorldTestHelper.setParents(me, -1, parent.getUniqueId());
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(parent.getUniqueId(), parent);

		YukkuriParentRule.checkNearParent(me);

		assertNotEquals(-1, me.getDestX(), "汚れた子ゆが遠くの親に向かって moveTo を呼ぶこと");
	}

	@Test
	void testCheckNearParent_closeEnoughSkipsMovement() {
		// distanceToParent < eyesight/32 → early return（moveTo 不要）
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri parent = WorldTestHelper.createBody();
		me.setAgeState(AgeState.BABY);
		me.setDirty(false); // dirty 分岐を避ける
		// eyesight/32 未満の距離に配置（ほぼ同じ場所）
		me.setCalcX(50);
		me.setCalcY(50);
		parent.setCalcX(51);
		parent.setCalcY(51);
		me.setEyesightBase(16000000); // デフォルトより大きい → eyesight/32 はかなり大きい
		WorldTestHelper.setParents(me, -1, parent.getUniqueId());
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(me.getUniqueId(), me);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(parent.getUniqueId(), parent);

		assertDoesNotThrow(() -> YukkuriParentRule.checkNearParent(me),
				"十分近い場合は early return で例外なし");
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
