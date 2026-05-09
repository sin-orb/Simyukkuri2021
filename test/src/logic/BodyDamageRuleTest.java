package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;
import src.enums.AgeState;
import src.system.Sprite;

class BodyDamageRuleTest {
	@Test
	void classifiesDamageAndBurstStates() {
		StubBodyAttributes body = createBodyWithOriginWidth(100);
		body.setAgeState(AgeState.ADULT);

		body.setDamage(0);
		assertTrue(BodyDamageRule.isNoDamaged(body));
		assertFalse(BodyDamageRule.isDamaged(body));
		assertFalse(BodyDamageRule.isDamagedHeavily(body));

		body.setDamage(body.getDamageLimit() / 2);
		assertFalse(BodyDamageRule.isNoDamaged(body));
		assertTrue(BodyDamageRule.isDamaged(body));
		assertFalse(BodyDamageRule.isDamagedHeavily(body));

		body.setDamage(body.getDamageLimit() * 3 / 4);
		assertTrue(BodyDamageRule.isDamaged(body));
		assertTrue(BodyDamageRule.isDamagedHeavily(body));

		body.setCantDiePeriod(1);
		assertTrue(BodyDamageRule.isCantDie(body));
		body.setCantDiePeriod(0);
		assertFalse(BodyDamageRule.isCantDie(body));

		body.setExpandSizeW(0);
		assertFalse(BodyDamageRule.isBurst(body));
		assertFalse(BodyDamageRule.isAboutToBurst(body));
		assertFalse(BodyDamageRule.isInfration(body));

		body.setExpandSizeW(75);
		assertTrue(BodyDamageRule.isAboutToBurst(body));
		assertTrue(BodyDamageRule.isInfration(body));
		assertFalse(BodyDamageRule.isBurst(body));

		body.setExpandSizeW(100);
		assertTrue(BodyDamageRule.isBurst(body));
		assertTrue(BodyDamageRule.isInfration(body));
	}

	private static StubBodyAttributes createBodyWithOriginWidth(int width) {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setBodySpr(new Sprite[3]);
		body.getBodySpr()[AgeState.ADULT.ordinal()] = new Sprite(width, 50, Sprite.PIVOT_CENTER_CENTER);
		return body;
	}
}
