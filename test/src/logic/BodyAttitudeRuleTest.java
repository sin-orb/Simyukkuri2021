package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;
import src.enums.Attitude;

class BodyAttitudeRuleTest {
	@Test
	void classifiesAttitudeValues() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setAttitude(Attitude.SUPER_SHITHEAD);
		assertTrue(BodyAttitudeRule.isVeryRude(body));
		assertTrue(BodyAttitudeRule.isRude(body));
		assertFalse(BodyAttitudeRule.isNormal(body));
		assertFalse(BodyAttitudeRule.isSmart(body));

		body.setAttitude(Attitude.SHITHEAD);
		assertFalse(BodyAttitudeRule.isVeryRude(body));
		assertTrue(BodyAttitudeRule.isRude(body));

		body.setAttitude(Attitude.AVERAGE);
		assertFalse(BodyAttitudeRule.isRude(body));
		assertTrue(BodyAttitudeRule.isNormal(body));
		assertFalse(BodyAttitudeRule.isSmart(body));

		body.setAttitude(Attitude.NICE);
		assertTrue(BodyAttitudeRule.isSmart(body));
		assertFalse(BodyAttitudeRule.isRude(body));
	}
}
