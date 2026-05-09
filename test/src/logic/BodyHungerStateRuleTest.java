package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;
class BodyHungerStateRuleTest {
	@Test
	void detectsHungerBands() {
		StubBodyAttributes body = new StubBodyAttributes();
		int limit = body.getHungryLimit();

		body.setHungry((int) (limit * 0.8f));
		assertTrue(BodyHungerStateRule.isFull(body));
		assertFalse(BodyHungerStateRule.isHungry(body));

		body.setHungry(limit / 2);
		assertTrue(BodyHungerStateRule.isHungry(body));
		assertFalse(BodyHungerStateRule.isSoHungry(body));

		body.setHungry((int) (limit * 0.2f));
		assertTrue(BodyHungerStateRule.isSoHungry(body));

		body.setHungry(0);
		assertTrue(BodyHungerStateRule.isVeryHungry(body));

		body.setDamage(body.getDamageLimitBase()[body.getBodyAgeState().ordinal()] / 2 + 1);
		assertTrue(BodyHungerStateRule.isTooHungry(body));

		body.setDamage(body.getDamageLimitBase()[body.getBodyAgeState().ordinal()] * 3 / 4 + 1);
		assertTrue(BodyHungerStateRule.isStarving(body));
	}

	@Test
	void ignoresDeadBodies() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setHungry(0);
		body.setDamage(body.getDamageLimitBase()[body.getBodyAgeState().ordinal()]);
		body.setDead(true);

		assertFalse(BodyHungerStateRule.isFull(body));
		assertFalse(BodyHungerStateRule.isHungry(body));
		assertFalse(BodyHungerStateRule.isSoHungry(body));
		assertFalse(BodyHungerStateRule.isVeryHungry(body));
		assertFalse(BodyHungerStateRule.isTooHungry(body));
		assertFalse(BodyHungerStateRule.isStarving(body));
	}
}
