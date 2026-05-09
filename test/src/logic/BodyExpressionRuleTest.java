package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyExpressionRuleTest {
	@Test
	void classifiesExpressionFlags() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setSilent(false);
		body.setShutmouth(false);
		body.setPikopiko(false);
		body.setPurupuru(false);
		assertFalse(BodyExpressionRule.isSilent(body));
		assertFalse(BodyExpressionRule.isShutmouth(body));
		assertFalse(BodyExpressionRule.isPikopiko(body));
		assertFalse(BodyExpressionRule.isPurupuru(body));

		body.setSilent(true);
		body.setShutmouth(true);
		body.setPikopiko(true);
		body.setPurupuru(true);
		assertTrue(BodyExpressionRule.isSilent(body));
		assertTrue(BodyExpressionRule.isShutmouth(body));
		assertTrue(BodyExpressionRule.isPikopiko(body));
		assertTrue(BodyExpressionRule.isPurupuru(body));
	}
}
