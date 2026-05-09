package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyConditionRuleTest {
	@Test
	void detectsBasicConditionFlags() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setDead(false);
		body.setFirstGround(false);
		body.setCrushed(false);
		body.setBurned(false);
		body.setRelax(false);
		body.setNightmare(false);
		body.setRapist(false);
		body.setSuperRapist(false);
		body.setWet(false);
		body.setMelt(false);
		body.setPealed(false);
		body.setPacked(false);
		body.setBlind(false);

		assertFalse(BodyConditionRule.isDead(body));
		assertFalse(BodyConditionRule.isFirstGround(body));
		assertFalse(BodyConditionRule.isCrushed(body));
		assertFalse(BodyConditionRule.isBurned(body));
		assertFalse(BodyConditionRule.isRelax(body));
		assertFalse(BodyConditionRule.isNightmare(body));
		assertFalse(BodyConditionRule.isRapist(body));
		assertFalse(BodyConditionRule.isSuperRapist(body));
		assertFalse(BodyConditionRule.isWet(body));
		assertFalse(BodyConditionRule.isMelt(body));
		assertFalse(BodyConditionRule.isPealed(body));
		assertFalse(BodyConditionRule.isPacked(body));
		assertFalse(BodyConditionRule.isBlind(body));

		body.setDead(true);
		body.setFirstGround(true);
		body.setCrushed(true);
		body.setBurned(true);
		body.setRelax(true);
		body.setNightmare(true);
		body.setRapist(true);
		body.setSuperRapist(true);
		body.setWet(true);
		body.setMelt(true);
		body.setPealed(true);
		body.setPacked(true);
		body.setBlind(true);

		assertTrue(BodyConditionRule.isDead(body));
		assertTrue(BodyConditionRule.isFirstGround(body));
		assertTrue(BodyConditionRule.isCrushed(body));
		assertTrue(BodyConditionRule.isBurned(body));
		assertTrue(BodyConditionRule.isRelax(body));
		assertTrue(BodyConditionRule.isNightmare(body));
		assertTrue(BodyConditionRule.isRapist(body));
		assertTrue(BodyConditionRule.isSuperRapist(body));
		assertTrue(BodyConditionRule.isWet(body));
		assertTrue(BodyConditionRule.isMelt(body));
		assertTrue(BodyConditionRule.isPealed(body));
		assertTrue(BodyConditionRule.isPacked(body));
		assertTrue(BodyConditionRule.isBlind(body));
	}
}
