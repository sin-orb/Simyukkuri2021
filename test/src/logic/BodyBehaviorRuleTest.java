package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyBehaviorRuleTest {
	@Test
	void classifiesBehaviorFlags() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setNotChangeCharacter(false);
		body.setUseRealPregnantLimit(false);
		assertFalse(BodyBehaviorRule.isNotChangeCharacter(body));
		assertFalse(BodyBehaviorRule.isUseRealPregnantLimit(body));

		body.setNotChangeCharacter(true);
		body.setUseRealPregnantLimit(true);
		assertTrue(BodyBehaviorRule.isNotChangeCharacter(body));
		assertTrue(BodyBehaviorRule.isUseRealPregnantLimit(body));
	}
}
