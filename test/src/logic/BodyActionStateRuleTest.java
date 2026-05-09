package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;
import src.enums.CriticalDamegeType;

class BodyActionStateRuleTest {
	@Test
	void detectsActionAndSenseStates() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setDead(false);
		body.setBegging(false);
		body.setStrike(false);
		body.setBirth(false);
		body.setEating(false);
		body.setEatingShit(false);
		body.setSukkiri(false);
		body.setNeedled(false);

		assertFalse(BodyActionStateRule.isBeggingForLife(body));
		assertFalse(BodyActionStateRule.isStrike(body));
		assertFalse(BodyActionStateRule.isBirth(body));
		assertFalse(BodyActionStateRule.isEating(body));
		assertFalse(BodyActionStateRule.isEatingShit(body));
		assertFalse(BodyActionStateRule.isSukkiri(body));
		assertFalse(BodyActionStateRule.isNeedled(body));
		assertFalse(BodyActionStateRule.isFeelPain(body));
		assertFalse(BodyActionStateRule.isFeelHardPain(body));

		body.setBegging(true);
		body.setStrike(true);
		body.setBirth(true);
		body.setEating(true);
		body.setEatingShit(true);
		body.setSukkiri(true);
		body.setNeedled(true);
		body.setAge(1000);
		body.setBirthAge(900);

		assertTrue(BodyActionStateRule.isBeggingForLife(body));
		assertTrue(BodyActionStateRule.isStrike(body));
		assertTrue(BodyActionStateRule.isBirth(body));
		assertTrue(BodyActionStateRule.isEating(body));
		assertTrue(BodyActionStateRule.isEatingShit(body));
		assertTrue(BodyActionStateRule.isSukkiri(body));
		assertTrue(BodyActionStateRule.isNeedled(body));
		assertTrue(BodyActionStateRule.isNewborn(body));

		body.setBirthAge(100);
		assertFalse(BodyActionStateRule.isNewborn(body));

		body.setDead(true);
		assertFalse(BodyActionStateRule.isBeggingForLife(body));
		assertFalse(BodyActionStateRule.isStrike(body));
		assertFalse(BodyActionStateRule.isBirth(body));
		assertFalse(BodyActionStateRule.isEating(body));
		assertFalse(BodyActionStateRule.isEatingShit(body));
		assertFalse(BodyActionStateRule.isSukkiri(body));
		assertFalse(BodyActionStateRule.isNeedled(body));

		body.setDead(false);
		body.setNeedled(true);
		assertTrue(BodyActionStateRule.isFeelPain(body));
		assertTrue(BodyActionStateRule.isFeelHardPain(body));

		body.setNeedled(false);
		body.setCriticalDamege(CriticalDamegeType.CUT);
		assertTrue(BodyActionStateRule.isFeelPain(body));
		assertFalse(BodyActionStateRule.isFeelHardPain(body));
	}
}
