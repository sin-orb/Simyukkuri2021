package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyHungerRuleTest {
	@Test
	void detectsTooFullAndOverEating() {
		StubBodyAttributes body = new StubBodyAttributes();
		int limit = body.getHungryLimit();

		body.setHungry(limit - 1);
		assertFalse(BodyHungerRule.isTooFull(body));
		assertFalse(BodyHungerRule.isOverEating(body));

		body.setHungry(limit);
		assertTrue(BodyHungerRule.isTooFull(body));
		assertFalse(BodyHungerRule.isOverEating(body));

		body.setHungry((int) (limit * 1.3f));
		assertTrue(BodyHungerRule.isTooFull(body));
		assertTrue(BodyHungerRule.isOverEating(body));
	}

	@Test
	void ignoresDeadBodies() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setHungry(body.getHungryLimit() * 2);
		body.setDead(true);

		assertFalse(BodyHungerRule.isTooFull(body));
		assertFalse(BodyHungerRule.isOverEating(body));
	}
}
