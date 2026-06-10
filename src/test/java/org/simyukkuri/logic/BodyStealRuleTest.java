package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaReimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.WorldTestHelper;

/**
 * Tests for YukkuriStealRule.
 */
public class BodyStealRuleTest {

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
	}

	@Test
	void testHandleOkazariSteal_successfulStealTransfersOkazari() {
		Yukkuri thief = WorldTestHelper.createBody();
		Yukkuri target = WorldTestHelper.createBody();
		thief.setAgeState(AgeState.ADULT);
		target.setAgeState(AgeState.ADULT);
		thief.setAttitude(Attitude.SHITHEAD);
		thief.takeOkazari(false);
		target.setSleeping(true);
		thief.setToSteal(true);
		thief.setPublicRank(PublicRank.NONE);
		target.setPublicRank(PublicRank.NONE);
		target.setX(100);
		target.setY(100);
		thief.setX(100);
		thief.setY(100);

		int initialStress = thief.getStress();
		assertTrue(YukkuriStealRule.handleOkazariSteal(target, thief),
				"盗み成功で true を返すこと");
		// okazari の移転を確認
		assertTrue(thief.hasOkazari(),    "盗み成功で thief がおかざりを持つこと");
		assertFalse(target.hasOkazari(), "盗み成功で target からおかざりが無くなること");
		// 盗み成功時の副作用確認
		// giveOkazari が VERY_HAPPY を設定し、後続の setHappiness(HAPPY) はそれより低いため変化しない
		assertEquals(Happiness.VERY_HAPPY, thief.getHappiness(), "盗み成功で thief が VERY_HAPPY になること");
		assertTrue(thief.getStress() < initialStress,       "盗み成功で thief のストレスが減ること");
	}

	private Yukkuri makeRudeThief() {
		Yukkuri thief = WorldTestHelper.createBody();
		thief.setAgeState(AgeState.ADULT);
		thief.setAttitude(Attitude.SHITHEAD);
		thief.takeOkazari(false);
		thief.setToSteal(true);
		thief.setPublicRank(PublicRank.NONE);
		return thief;
	}

	private Yukkuri makeSleepingTarget() {
		Yukkuri target = WorldTestHelper.createBody();
		target.setAgeState(AgeState.ADULT);
		target.setSleeping(true);
		target.setPublicRank(PublicRank.NONE);
		return target;
	}

	@Test
	void actorWithOkazariCannotSteal() {
		Yukkuri thief = WorldTestHelper.createBody();
		thief.setAgeState(AgeState.ADULT);
		thief.setAttitude(Attitude.SHITHEAD);
		// hasOkazari() はデフォルト true → !hasOkazari()=false → ガードで弾かれる
		Yukkuri target = makeSleepingTarget();
		assertFalse(YukkuriStealRule.handleOkazariSteal(target, thief),
				"お飾りを持っている actor は盗みに失敗すること");
	}

	@Test
	void targetWithoutOkazariCannotBeStolen() {
		Yukkuri thief = makeRudeThief();
		Yukkuri target = makeSleepingTarget();
		target.setOkazaris(null); // hasOkazari=false
		assertFalse(YukkuriStealRule.handleOkazariSteal(target, thief),
				"お飾りのない target から盗めないこと");
	}

	@Test
	void ageStateMismatchBlocksSteal() {
		Yukkuri thief = makeRudeThief();
		thief.setAgeState(AgeState.ADULT);
		Yukkuri target = makeSleepingTarget();
		target.setAgeState(AgeState.CHILD);
		assertFalse(YukkuriStealRule.handleOkazariSteal(target, thief),
				"年齢が違うゆっくりから盗めないこと");
	}

	@Test
	void typeMismatchBlocksSteal() {
		Yukkuri thief = makeRudeThief(); // Marisa
		// Reimu でターゲットを作る（型が異なる）
		Reimu reimu = new Reimu();
		reimu.setObjId(org.simyukkuri.enums.Numbering.INSTANCE.numberingObjId());
		reimu.setUniqueId(org.simyukkuri.enums.Numbering.INSTANCE.numberingYukkuriId());
		reimu.setAgeState(AgeState.ADULT);
		reimu.setPublicRank(PublicRank.NONE);
		reimu.setSleeping(true);
		assertFalse(YukkuriStealRule.handleOkazariSteal(reimu, thief),
				"種族が違うゆっくりから盗めないこと");
	}

	@Test
	void hybridActorBlocksSteal() {
		// ハイブリッド種（MarisaReimu）はisHybrid()=true
		MarisaReimu hybrid = new MarisaReimu();
		hybrid.setObjId(org.simyukkuri.enums.Numbering.INSTANCE.numberingObjId());
		hybrid.setUniqueId(org.simyukkuri.enums.Numbering.INSTANCE.numberingYukkuriId());
		hybrid.setAgeState(AgeState.ADULT);
		hybrid.setAttitude(Attitude.SHITHEAD);
		hybrid.takeOkazari(false);
		hybrid.setToSteal(true);
		hybrid.setPublicRank(PublicRank.NONE);
		Yukkuri target = makeSleepingTarget();
		assertFalse(YukkuriStealRule.handleOkazariSteal(target, hybrid),
				"ハイブリッドは盗みに失敗すること");
	}

	@Test
	void rankConditionBlocksSteal() {
		// target.rank=UNUN_SLAVE かつ actor.rank=NONE → (UNUN_SLAVE==NONE || NONE==UNUN_SLAVE) = false
		Yukkuri thief = makeRudeThief();
		thief.setPublicRank(PublicRank.NONE);
		Yukkuri target = makeSleepingTarget();
		target.setPublicRank(PublicRank.UNUN_SLAVE);
		assertFalse(YukkuriStealRule.handleOkazariSteal(target, thief),
				"target が NONE 以外かつ actor が UNUN_SLAVE 以外の場合は盗めないこと");
	}

	@Test
	void lockmoveActorBlocksSteal() {
		Yukkuri thief = makeRudeThief();
		thief.setLockmove(true);
		Yukkuri target = makeSleepingTarget();
		assertFalse(YukkuriStealRule.handleOkazariSteal(target, thief),
				"移動ロック中は盗みに失敗すること");
	}

	@Test
	void niceActorCannotSteal() {
		Yukkuri thief = makeRudeThief();
		thief.setAttitude(Attitude.NICE); // isRude=false
		Yukkuri target = makeSleepingTarget();
		assertFalse(YukkuriStealRule.handleOkazariSteal(target, thief),
				"ゲスでない actor は盗みに失敗すること");
	}

	@Test
	void ununSlaveStealsFromNoneAndRanksSwap() {
		Yukkuri thief = makeRudeThief();
		thief.setPublicRank(PublicRank.UNUN_SLAVE);
		Yukkuri target = makeSleepingTarget();
		target.setPublicRank(PublicRank.NONE);

		assertTrue(YukkuriStealRule.handleOkazariSteal(target, thief),
				"UNUN_SLAVE が NONE から盗み成功すること");
		assertEquals(PublicRank.NONE, thief.getPublicRank(),
				"盗み成功で thief が NONE に昇格すること");
		assertEquals(PublicRank.UNUN_SLAVE, target.getPublicRank(),
				"盗み成功で target が UNUN_SLAVE に降格すること");
	}

	@Test
	void testHandleOkazariSteal_awakeWitnessBlocksSteal() {
		Yukkuri thief = WorldTestHelper.createBody();
		Yukkuri target = WorldTestHelper.createBody();
		final Yukkuri witness = WorldTestHelper.createBody();
		thief.setAgeState(AgeState.ADULT);
		target.setAgeState(AgeState.ADULT);
		thief.setAttitude(Attitude.SHITHEAD);
		thief.takeOkazari(false);
		thief.setToSteal(true);
		thief.setPublicRank(PublicRank.NONE);
		target.setPublicRank(PublicRank.NONE);
		target.setX(100);
		target.setY(100);
		thief.setX(100);
		thief.setY(100);
		witness.setX(200);
		witness.setY(200);
		WorldTestHelper.initializeMinimalWorld();
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(thief.getUniqueId(), thief);
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(target.getUniqueId(), target);
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(witness.getUniqueId(), witness);

		Happiness initialHappiness = thief.getHappiness();
		assertFalse(YukkuriStealRule.handleOkazariSteal(target, thief),
				"目撃者がいると盗めず false を返すこと");
		// 盗み失敗 → okazari は移転していないこと
		assertFalse(thief.hasOkazari(),  "目撃者がいると thief はおかざりを得ないこと");
		assertTrue(target.hasOkazari(), "目撃者がいると target のおかざりは保持されること");
		// happiness は変化しないこと（盗み失敗では副作用なし）
		assertEquals(initialHappiness, thief.getHappiness(), "盗み失敗で thief の happiness は変化しないこと");
	}
}
