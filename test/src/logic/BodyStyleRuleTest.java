package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyStyleRuleTest {
	@Test
	void detectsFurifuriAndIgnoresDeadBodies() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setFurifuri(true);
		assertTrue(BodyStyleRule.isFurifuri(body));

		body.setDead(true);
		assertFalse(BodyStyleRule.isFurifuri(body));
	}

	@Test
	void detectsNobinobiAndIgnoresDeadBodies() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setNobinobi(true);
		assertTrue(BodyStyleRule.isNobinobi(body));

		body.setDead(true);
		assertFalse(BodyStyleRule.isNobinobi(body));
	}

	@Test
	void detectsVainAndIgnoresDeadBodies() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setBeVain(true);
		assertTrue(BodyStyleRule.isVain(body));

		body.setDead(true);
		assertFalse(BodyStyleRule.isVain(body));
	}

	@Test
	void detectsBeVainAndIgnoresDeadBodies() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setBeVain(true);
		assertTrue(BodyStyleRule.isBeVain(body));

		body.setDead(true);
		assertFalse(BodyStyleRule.isBeVain(body));
	}
}
