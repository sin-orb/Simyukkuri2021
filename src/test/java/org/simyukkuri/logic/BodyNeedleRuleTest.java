package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.util.WorldTestHelper;

class BodyNeedleRuleTest {

	private Yukkuri target;
	private Yukkuri actor;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		target = WorldTestHelper.createBody();
		actor = WorldTestHelper.createBody();
		// isPartner/isChild 等の関係判定は YukkuriLookup.getYukkuriById を使うため registry 登録必須
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(actor.getUniqueId(), actor);
		SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(target.getUniqueId(), target);
	}

	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	void notNeedledReturnsFalse() {
		target.setNeedled(false);
		assertFalse(YukkuriNeedleRule.handleNeedledYukkuri(target, actor),
				"針が刺さっていない場合は false を返すこと");
	}

	@Test
	void needledChildWithMotherActorCallsGuriguri() {
		// actor=成体+母、target=子ゆ+針
		actor.setAgeState(AgeState.ADULT);
		target.setAgeState(AgeState.BABY);
		WorldTestHelper.setParents(target, -1, actor.getUniqueId()); // actor が target の母
		target.setNeedled(true);

		boolean result = YukkuriNeedleRule.handleNeedledYukkuri(target, actor);

		assertTrue(result, "処理を担当したため true を返すこと");
		assertEquals(Happiness.VERY_SAD, actor.getHappiness(),
				"母ぐーりぐーりで actor の happiness が VERY_SAD になること");
	}

	@Test
	void needledPartnerCallsGuriguri() {
		// target=つがい+針、actor=つがい
		actor.setPartner(target.getUniqueId());
		target.setPartner(actor.getUniqueId());
		target.setNeedled(true);

		boolean result = YukkuriNeedleRule.handleNeedledYukkuri(target, actor);

		assertTrue(result, "処理を担当したため true を返すこと");
		assertEquals(Happiness.VERY_SAD, actor.getHappiness(),
				"番ぐーりぐーりで actor の happiness が VERY_SAD になること");
	}

	@Test
	void needledUnrelatedBodyClearsActionsWithoutGuriguri() {
		// 無関係成体ゆっくり、針あり → clearActions のみ、doGuriguri なし
		// actor=ADULT のままにして sister 分岐(子ゆのみ)も回避する
		target.setAgeState(AgeState.ADULT);
		actor.setAgeState(AgeState.ADULT);
		target.setNeedled(true);
		int stressBefore = actor.getStress();

		boolean result = YukkuriNeedleRule.handleNeedledYukkuri(target, actor);

		assertTrue(result, "針あり処理を担当したため true を返すこと");
		assertEquals(stressBefore, actor.getStress(),
				"無関係ゆっくりに対しては doGuriguri(addStress(30)) が呼ばれないこと");
	}
}
