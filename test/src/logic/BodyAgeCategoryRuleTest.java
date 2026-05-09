package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;
import src.enums.AgeState;

class BodyAgeCategoryRuleTest {
	@Test
	void classifiesAgeStates() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setAgeState(AgeState.ADULT);
		assertTrue(BodyAgeCategoryRule.isAdult(body));
		assertFalse(BodyAgeCategoryRule.isChild(body));
		assertFalse(BodyAgeCategoryRule.isBaby(body));

		body.setAgeState(AgeState.CHILD);
		assertFalse(BodyAgeCategoryRule.isAdult(body));
		assertTrue(BodyAgeCategoryRule.isChild(body));
		assertFalse(BodyAgeCategoryRule.isBaby(body));

		body.setAgeState(AgeState.BABY);
		assertFalse(BodyAgeCategoryRule.isAdult(body));
		assertFalse(BodyAgeCategoryRule.isChild(body));
		assertTrue(BodyAgeCategoryRule.isBaby(body));
	}
}
