package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.StubBodyAttributes;

class BodyStressRuleTest {
	private static final class StressBody extends StubBodyAttributes {
		@Override
		public int getNonYukkuriDiseaseTolerance() {
			return 100;
		}
	}

	@Test
	void detectsStressThresholds() {
		StressBody body = new StressBody();
		int limit = body.getStressLimit();

		// ストレス閾値の完全な境界確認
		body.setStress(limit * 2 / 5);
		assertFalse(YukkuriStressRule.isStressful(body),    "limit*2/5 では isStressful=false");
		assertFalse(YukkuriStressRule.isVeryStressful(body), "limit*2/5 では isVeryStressful=false");

		body.setStress(limit * 2 / 5 + 1);
		assertTrue(YukkuriStressRule.isStressful(body),     "limit*2/5+1 では isStressful=true");
		assertFalse(YukkuriStressRule.isVeryStressful(body), "limit*2/5+1 では isVeryStressful=false");

		// Very ストレス手前の境界
		body.setStress(limit * 3 / 5);
		assertTrue(YukkuriStressRule.isStressful(body),     "limit*3/5 では isStressful=true");
		assertFalse(YukkuriStressRule.isVeryStressful(body), "limit*3/5 では isVeryStressful=false");

		body.setStress(limit * 3 / 5 + 1);
		assertTrue(YukkuriStressRule.isStressful(body),     "limit*3/5+1 では isStressful=true");
		assertTrue(YukkuriStressRule.isVeryStressful(body),  "limit*3/5+1 では isVeryStressful=true");
	}

	@Test
	void ignoresDeadBodiesByDelegatingToStressValueOnly() {
		StressBody body = new StressBody();
		body.setStress(body.getStressLimit());

		// isStressful/isVeryStressful はストレス値のみで判定し dead フラグを無視すること
		body.setDead(false);
		assertTrue(YukkuriStressRule.isStressful(body),    "alive でも stress=limit なら isStressful=true");
		assertTrue(YukkuriStressRule.isVeryStressful(body), "alive でも stress=limit なら isVeryStressful=true");

		body.setDead(true);
		assertTrue(YukkuriStressRule.isStressful(body),    "dead でも stress=limit なら isStressful=true");
		assertTrue(YukkuriStressRule.isVeryStressful(body), "dead でも stress=limit なら isVeryStressful=true");
	}
}
