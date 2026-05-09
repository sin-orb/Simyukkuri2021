package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodySpecialTypeRuleTest {
	@Test
	void returnsFalseForDefaultStubBody() {
		StubBodyAttributes body = new StubBodyAttributes();

		assertFalse(BodySpecialTypeRule.isIdiot(body));
		assertFalse(BodySpecialTypeRule.isHybrid(body));
	}
}
