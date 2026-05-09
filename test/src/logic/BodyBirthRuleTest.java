package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyBirthRuleTest {
	@Test
	void detectsUnBirthFlag() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setUnBirth(false);
		assertFalse(BodyBirthRule.isUnBirth(body));

		body.setUnBirth(true);
		assertTrue(BodyBirthRule.isUnBirth(body));
	}

	@Test
	void detectsForceBirthMessageFlag() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setBirthMessageForced(false);
		assertFalse(BodyBirthRule.isBirthMessageForced(body));

		body.setBirthMessageForced(true);
		assertTrue(BodyBirthRule.isBirthMessageForced(body));
	}

	@Test
	void detectsFirstEatStalkAndFirstGroundFlags() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setFirstEatStalk(false);
		body.setFirstGround(false);
		assertFalse(BodyBirthRule.isFirstEatStalk(body));
		assertFalse(BodyBirthRule.isFirstGround(body));

		body.setFirstEatStalk(true);
		body.setFirstGround(true);
		assertTrue(BodyBirthRule.isFirstEatStalk(body));
		assertTrue(BodyBirthRule.isFirstGround(body));
	}
}
