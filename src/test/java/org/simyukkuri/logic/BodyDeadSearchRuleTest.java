package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriDeadSearchRule.
 */
class BodyDeadSearchRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		GameEnvironment.clearOverride();
	}

	@AfterEach
	void tearDown() {
		SimYukkuri.RND = new java.util.Random();
		WorldTestHelper.resetWorld();
	}

	@Test
	void testHandleDeadFound_excitingBranchMovesToSukkiri() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setSpriteSet(makeSprites(1, 1));
		you.setSpriteSet(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(120);
		you.setY(100);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setAgeState(AgeState.ADULT);
		you.setDead(true);
		me.setExciting(true);
		me.setTargetBind(true);  // exciting ブランチで false に変わることを確認するため事前に true

		assertTrue(YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0),
				"exciting ブランチは true を返すこと");
		assertFalse(me.isTargetBind(),
				"exciting ブランチで setTargetBind(false) が呼ばれること");
	}

	@Test
	void testHandleDeadFound_randomSkipReturnsFalse() {
		WorldTestHelper.initializeMinimalWorld();
		Yukkuri me = WorldTestHelper.createBody();
		Yukkuri you = WorldTestHelper.createBody();
		me.setSpriteSet(makeSprites(1, 1));
		you.setSpriteSet(makeSprites(1, 1));
		me.setX(100);
		me.setY(100);
		you.setX(120);
		you.setY(100);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		me.setAgeState(AgeState.ADULT);
		you.setDead(true);

		assertFalse(YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0));
	}

	@Test
	void testHandleDeadFound_rankMismatch_returnsFalse() {
		// ランクが違う → 家族コードをスキップ → handled=false
		Yukkuri me = createRegisteredBody(AgeState.ADULT);
		Yukkuri you = createRegisteredBody(AgeState.ADULT);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.UNUN_SLAVE);
		you.setDead(true);
		SimYukkuri.RND = new ConstState(0); // nextInt(10)=0 → proceed

		assertFalse(YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0),
				"ランクが違う死体には反応しないこと（return false）");
	}

	@Test
	void testHandleDeadFound_adultActorWithFamilyDeadMovesToYukkuri() {
		// 成体 + 家族死体 → moveToYukkuri → return true
		Yukkuri me = createRegisteredBody(AgeState.ADULT);
		Yukkuri you = createRegisteredBody(AgeState.BABY);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		WorldTestHelper.setParents(you, -1, me.getUniqueId()); // me は you の母
		you.setDead(true);
		SimYukkuri.RND = new ConstState(0);

		assertTrue(YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0),
				"成体が家族（子）の死体に向かうこと（return true）");
	}

	@Test
	void testHandleDeadFound_childActorWithSisterDeadMovesToYukkuri() {
		// 子ゆ + 姉妹死体 → moveToYukkuri → return true
		Yukkuri commonParent = createRegisteredBody(AgeState.ADULT);
		Yukkuri me = createRegisteredBody(AgeState.CHILD);
		Yukkuri you = createRegisteredBody(AgeState.CHILD);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		WorldTestHelper.setParents(me, -1, commonParent.getUniqueId());
		WorldTestHelper.setParents(you, -1, commonParent.getUniqueId()); // 同じ母
		you.setDead(true);
		SimYukkuri.RND = new ConstState(0);

		assertTrue(YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0),
				"子ゆが姉妹の死体に向かうこと（return true）");
	}

	@Test
	void testHandleDeadFound_adultActorWithUnrelatedDeadReturnsFalse() {
		// 成体 + 無関係死体 → lookTo は呼ぶが handled=false
		Yukkuri me = createRegisteredBody(AgeState.ADULT);
		Yukkuri you = createRegisteredBody(AgeState.ADULT);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		you.setDead(true);
		SimYukkuri.RND = new ConstState(0);

		assertFalse(YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0),
				"成体が無関係の死体に向かわないこと（return false）");
	}

	@Test
	void testHandleDeadFound_childActorWithUnrelatedDeadRunsAway() {
		// 子ゆ + 無関係死体 → runAway → scare=true
		Yukkuri me = createRegisteredBody(AgeState.CHILD);
		Yukkuri you = createRegisteredBody(AgeState.ADULT);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		you.setDead(true);
		SimYukkuri.RND = new ConstState(0);

		YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0);

		assertTrue(me.isScare(), "子ゆが無関係の死体を見て逃げること（scare=true）");
	}

	@Test
	void testHandleDeadFound_talkingActorSkipsScareAndMemories() {
		// isTalking=true → scare メッセージと addMemories(-1) がスキップされる
		Yukkuri me = createRegisteredBody(AgeState.ADULT);
		Yukkuri you = createRegisteredBody(AgeState.ADULT);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		you.setDead(true);
		me.setMessageTicks(1); // isTalking=true
		int memoriesBefore = me.getMemories();
		SimYukkuri.RND = new ConstState(0);

		YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0);

		assertEquals(memoriesBefore, me.getMemories(),
				"しゃべり中は addMemories(-1) が呼ばれないこと");
	}

	@Test
	void testHandleDeadFound_normalActorDecrementsMemories() {
		// 通常ゆっくり + 非レイパー + 非捕食 → addMemories(-1)
		Yukkuri me = createRegisteredBody(AgeState.ADULT);
		Yukkuri you = createRegisteredBody(AgeState.ADULT);
		me.setPublicRank(PublicRank.NONE);
		you.setPublicRank(PublicRank.NONE);
		you.setDead(true);
		me.setIntelligence(org.simyukkuri.enums.Intelligence.AVERAGE); // 確定的に AVERAGE（FOOL/WISE は -1/2=0 になる）
		me.setMemories(100);
		SimYukkuri.RND = new ConstState(0);

		YukkuriDeadSearchRule.handleDeadFound(me, you, 0, 0);

		assertEquals(99, me.getMemories(),
				"通常ゆっくりは死体を見て memories が -1 されること");
	}

	private Yukkuri createRegisteredBody(AgeState age) {
		Yukkuri b = WorldTestHelper.createBody();
		b.setSpriteSet(makeSprites(1, 1));
		b.setAgeState(age);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueId(), b);
		return b;
	}

	private static Sprite[] makeSprites(int w, int h) {
		Sprite[] spr = new Sprite[3];
		for (int i = 0; i < 3; i++) {
			spr[i] = new Sprite(w, h, Sprite.PIVOT_CENTER_BOTTOM);
		}
		return spr;
	}
}
