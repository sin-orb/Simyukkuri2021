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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.entity.core.living.yukkuri.StubBodyAttributes;

class BodyStressRuleTest {
	private static final class StressBody extends StubBodyAttributes {
		@Override
		public int checkNonYukkuriDiseaseTolerance() {
			return 100;
		}
	}

	@Test
	void detectsStressThresholds() {
		StressBody body = new StressBody();
		int limit = body.getStressLimit();

		body.setStress(limit * 2 / 5);
		assertFalse(BodyStressRule.isStressful(body));
		assertFalse(BodyStressRule.isVeryStressful(body));

		body.setStress(limit * 2 / 5 + 1);
		assertTrue(BodyStressRule.isStressful(body));
		assertFalse(BodyStressRule.isVeryStressful(body));

		body.setStress(limit * 3 / 5 + 1);
		assertTrue(BodyStressRule.isStressful(body));
		assertTrue(BodyStressRule.isVeryStressful(body));
	}

	@Test
	void ignoresDeadBodiesByDelegatingToStressValueOnly() {
		StressBody body = new StressBody();
		body.setStress(body.getStressLimit());
		body.setDead(true);

		assertTrue(BodyStressRule.isStressful(body));
		assertTrue(BodyStressRule.isVeryStressful(body));
	}
}
