package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyBurnRuleTest {
	@Test
	void detectsBurnedAndHeavilyBurnedStates() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setFootBakePeriod(0);
		body.setBodyBakePeriod(0);

		assertFalse(BodyBurnRule.isGotBurned(body));
		assertFalse(BodyBurnRule.isGotBurnedHeavily(body));

		body.setFootBakePeriod(body.getDamageLimitBase()[body.getBodyAgeState().ordinal()] / 2 + 1);
		assertTrue(BodyBurnRule.isGotBurned(body));
		assertTrue(BodyBurnRule.isGotBurnedHeavily(body));

		body.setFootBakePeriod(0);
		body.setBodyBakePeriod(body.getDamageLimitBase()[body.getBodyAgeState().ordinal()] * 3 / 4 + 1);
		assertTrue(BodyBurnRule.isGotBurned(body));
		assertTrue(BodyBurnRule.isGotBurnedHeavily(body));
	}
}
