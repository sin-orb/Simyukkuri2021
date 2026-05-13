package src.logic;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import src.entity.core.living.yukkuri.StubBodyAttributes;
import src.enums.BodyRank;
import src.util.GameRandom;
import src.util.RandomSource;

class BodyExcretionRuleTest {
	@AfterEach
	void tearDown() {
		GameRandom.clearOverride();
	}

	@Test
	void kaiyuAlwaysCausesDiarrhea() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setBodyRank(BodyRank.KAIYU);

		assertTrue(BodyExcretionRule.getDiarrhea(body));
	}

	@Test
	void sicknessAndDamageHalveTheProbabilityBound() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setBodyRank(BodyRank.NORAYU);
		body.setDiarrheaProb(10);

		CapturingRandom random = new CapturingRandom();
		GameRandom.setOverride(random);

		assertTrue(BodyExcretionRule.getDiarrhea(body));
		assertEquals(10, random.lastBound);

		body.setSickPeriod(body.getIncubationPeriodBase() + 1);
		body.setDamage(1);

		assertTrue(BodyExcretionRule.getDiarrhea(body));
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
