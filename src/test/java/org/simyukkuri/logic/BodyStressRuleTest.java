package org.simyukkuri.logic;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

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

		body.setStress(limit * 2 / 5);
		assertFalse(YukkuriStressRule.isStressful(body));
		assertFalse(YukkuriStressRule.isVeryStressful(body));

		body.setStress(limit * 2 / 5 + 1);
		assertTrue(YukkuriStressRule.isStressful(body));
		assertFalse(YukkuriStressRule.isVeryStressful(body));

		body.setStress(limit * 3 / 5 + 1);
		assertTrue(YukkuriStressRule.isStressful(body));
		assertTrue(YukkuriStressRule.isVeryStressful(body));
	}

	@Test
	void ignoresDeadBodiesByDelegatingToStressValueOnly() {
		StressBody body = new StressBody();
		body.setStress(body.getStressLimit());
		body.setDead(true);

		assertTrue(YukkuriStressRule.isStressful(body));
		assertTrue(YukkuriStressRule.isVeryStressful(body));
	}
}
