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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
		StubBodyAttributes body = new StubBodyAttributes();
		body.setRank(YukkuriRank.KAIYU);

		assertTrue(YukkuriExcretionRule.getDiarrhea(body));
	}

	@Test
	void sicknessAndDamageHalveTheProbabilityBound() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setRank(YukkuriRank.NORAYU);
		body.setDiarrheaProb(10);

		CapturingRandom random = new CapturingRandom();
		GameRandom.setOverride(random);

		assertTrue(YukkuriExcretionRule.getDiarrhea(body));
		assertEquals(10, random.lastBound);

		body.setSickPeriod(body.getIncubationPeriodBase() + 1);
		body.setDamage(1);

		assertTrue(YukkuriExcretionRule.getDiarrhea(body));
		assertEquals(5, random.lastBound);
	}

	private static final class CapturingRandom implements RandomSource {
		private int lastBound;

		@Override
		public int nextInt(int bound) {
			lastBound = bound;
			return 0;
		}

		@Override
		public boolean nextBoolean() {
			return false;
		}
	}
}
