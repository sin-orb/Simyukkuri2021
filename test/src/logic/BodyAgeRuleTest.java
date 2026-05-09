package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyAgeRuleTest {
	@Test
	void detectsOldBodies() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setAge((body.getLifeLimitBase() * 9 / 10) - 1);
		assertFalse(BodyAgeRule.isOld(body));

		body.setAge((body.getLifeLimitBase() * 9 / 10) + 1);
		assertTrue(BodyAgeRule.isOld(body));
	}
}
