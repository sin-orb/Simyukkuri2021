package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyFallRuleTest {
	@Test
	void detectsNoDamageNextFallFlag() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setNoDamageNextFall(false);
		assertFalse(BodyFallRule.isNoDamageNextFall(body));

		body.setNoDamageNextFall(true);
		assertTrue(BodyFallRule.isNoDamageNextFall(body));
	}
}
