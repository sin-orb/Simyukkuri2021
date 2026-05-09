package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;
import src.enums.AgeState;
import src.enums.Burst;
import src.system.Sprite;

class BodyBurstRuleTest {
	@Test
	void classifiesCantDieAndBurstStates() {
		StubBodyAttributes body = createBodyWithOriginWidth(100);

		body.setCantDiePeriod(1);
		assertTrue(BodyBurstRule.isCantDie(body));
		body.setCantDiePeriod(0);
		assertFalse(BodyBurstRule.isCantDie(body));

		body.setExpandSizeW(0);
		assertFalse(BodyBurstRule.isBurst(body));
		assertFalse(BodyBurstRule.isAboutToBurst(body));
		assertFalse(BodyBurstRule.isInfration(body));

		body.setExpandSizeW(75);
		assertTrue(BodyBurstRule.isAboutToBurst(body));
		assertTrue(BodyBurstRule.isInfration(body));
		assertFalse(BodyBurstRule.isBurst(body));

		body.setExpandSizeW(100);
		assertTrue(BodyBurstRule.isBurst(body));
		assertTrue(BodyBurstRule.isInfration(body));
	}

	private static StubBodyAttributes createBodyWithOriginWidth(int width) {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setAgeState(AgeState.ADULT);
		body.setBodySpr(new Sprite[3]);
		body.getBodySpr()[AgeState.ADULT.ordinal()] = new Sprite(width, 50, Sprite.PIVOT_CENTER_CENTER);
		return body;
	}
}
