package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyDisplayRuleTest {
	@Test
	void classifiesDisplayFlags() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setImageNagasiMode(false);
		body.setShadowVisible(false);
		body.setPinned(false);
		assertFalse(BodyDisplayRule.isImageNagasiMode(body));
		assertFalse(BodyDisplayRule.isShadowVisible(body));
		assertFalse(BodyDisplayRule.isPinned(body));

		body.setImageNagasiMode(true);
		body.setShadowVisible(true);
		body.setPinned(true);
		assertTrue(BodyDisplayRule.isImageNagasiMode(body));
		assertTrue(BodyDisplayRule.isShadowVisible(body));
		assertTrue(BodyDisplayRule.isPinned(body));
	}
}
