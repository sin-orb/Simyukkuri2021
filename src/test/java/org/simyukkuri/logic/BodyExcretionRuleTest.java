package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.StubBodyAttributes;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.RandomSource;

class BodyExcretionRuleTest {
	@AfterEach
	void tearDown() {
		GameRandom.clearOverride();
	}

	@Test
	void kaiyuAlwaysCausesDiarrhea() {
		// KAIYU は確率に関係なく常に true
		StubBodyAttributes kaiyu = new StubBodyAttributes();
		kaiyu.setRank(YukkuriRank.KAIYU);
		assertTrue(YukkuriExcretionRule.getDiarrhea(kaiyu), "KAIYU は常に下痢");

		// KAIYU 以外は diarrheaProb による確率判定（nextInt が 0 以外なら false）
		CapturingRandom random = new CapturingRandom(1);  // 常に 1 を返す（≠0 なので false）
		GameRandom.setOverride(random);
		StubBodyAttributes nora = new StubBodyAttributes();
		nora.setRank(YukkuriRank.NORAYU);
		nora.setDiarrheaProb(10);
		assertFalse(YukkuriExcretionRule.getDiarrhea(nora),
				"NORAYU は nextInt≠0 のとき下痢にならない");
	}

	@Test
	void sicknessAndDamageHalveTheProbabilityBound() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setRank(YukkuriRank.NORAYU);
		body.setDiarrheaProb(10);

		CapturingRandom random = new CapturingRandom(0);  // 常に 0 を返す（=0 なので true）
		GameRandom.setOverride(random);

		// 通常: diarrheaProb=10 がそのまま境界として使われる
		assertTrue(YukkuriExcretionRule.getDiarrhea(body));
		assertEquals(10, random.lastBound, "通常は diarrheaProb(10) がそのまま境界");

		// sick + damage → diarrheaProb が半分になる
		body.setSickPeriod(body.getIncubationPeriodBase() + 1);
		body.setDamage(1);
		assertTrue(YukkuriExcretionRule.getDiarrhea(body));
		assertEquals(5, random.lastBound, "sick + damage で確率境界が 10/2=5 に半減すること");
	}

	@Test
	void zeroDiarrhoeaProbClampsToOneAndAlwaysReturnsTrue() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setRank(YukkuriRank.NORAYU);
		body.setDiarrheaProb(0);

		CapturingRandom random = new CapturingRandom(0);
		GameRandom.setOverride(random);

		assertTrue(YukkuriExcretionRule.getDiarrhea(body),
				"diarrheaProb=0 は 1 にクランプされ nextInt(1)==0 で常に true になること");
		assertEquals(1, random.lastBound, "クランプ後の境界は 1 であること");
	}

	private static final class CapturingRandom implements RandomSource {
		private int lastBound;
		private final int returnValue;

		CapturingRandom(int returnValue) {
			this.returnValue = returnValue;
		}

		@Override
		public int nextInt(int bound) {
			lastBound = bound;
			return returnValue;
		}

		@Override
		public boolean nextBoolean() {
			return false;
		}
	}
}
