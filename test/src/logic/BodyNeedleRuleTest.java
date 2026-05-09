package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyNeedleRuleTest {
	@Test
	void detectsNeedledFlag() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setNeedled(false);
		assertFalse(BodyNeedleRule.isNeedled(body));

		body.setNeedled(true);
		assertTrue(BodyNeedleRule.isNeedled(body));
	}
}
