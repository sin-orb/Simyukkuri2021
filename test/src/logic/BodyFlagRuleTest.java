package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;
import src.enums.CoreAnkoState;
import src.enums.Trauma;

class BodyFlagRuleTest {
	@Test
	void detectsTraumaTakenAndNYDFlags() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setCoreAnkoState(CoreAnkoState.DEFAULT);
		body.setTrauma(null);

		assertFalse(BodyFlagRule.hasTrauma(body));
		assertFalse(BodyFlagRule.isTaken(body));
		assertFalse(BodyFlagRule.isNYD(body));
		assertTrue(BodyFlagRule.isNotNYD(body));

		body.setTrauma(Trauma.Factory);
		body.setTaken(true);
		body.setCoreAnkoState(CoreAnkoState.NonYukkuriDiseaseNear);

		assertTrue(BodyFlagRule.hasTrauma(body));
		assertTrue(BodyFlagRule.isTaken(body));
		assertTrue(BodyFlagRule.isNYD(body));
		assertFalse(BodyFlagRule.isNotNYD(body));
	}

	@Test
	void detectsPeroperoBeggingAndStubbornlyDirtyFlags() {
		StubBodyAttributes body = new StubBodyAttributes();

		assertFalse(BodyFlagRule.isPeropero(body));
		assertFalse(BodyFlagRule.isBegging(body));
		assertFalse(BodyFlagRule.isStubbornlyDirty(body));

		body.setPeropero(true);
		body.setBegging(true);
		body.setStubbornlyDirty(true);

		assertTrue(BodyFlagRule.isPeropero(body));
		assertTrue(BodyFlagRule.isBegging(body));
		assertTrue(BodyFlagRule.isStubbornlyDirty(body));

		body.setDead(true);
		assertFalse(BodyFlagRule.isPeropero(body));
		assertFalse(BodyFlagRule.isStubbornlyDirty(body));
	}

	@Test
	void detectsSupplementalLegacyFlags() {
		StubBodyAttributes body = new StubBodyAttributes();
		body.setPenipeniCutted(false);
		body.setPheromone(false);
		body.setNoticeNoOkazari(false);
		body.setOnNonMovingConveyor(false);

		assertFalse(BodyFlagRule.isPenipeniCutted(body));
		assertFalse(BodyFlagRule.isPheromone(body));
		assertFalse(BodyFlagRule.isNoticeNoOkazari(body));
		assertFalse(BodyFlagRule.isOnNonMovingConveyor(body));

		body.setPenipeniCutted(true);
		body.setPheromone(true);
		body.setNoticeNoOkazari(true);
		body.setOnNonMovingConveyor(true);

		assertTrue(BodyFlagRule.isPenipeniCutted(body));
		assertTrue(BodyFlagRule.isPheromone(body));
		assertTrue(BodyFlagRule.isNoticeNoOkazari(body));
		assertTrue(BodyFlagRule.isOnNonMovingConveyor(body));
	}
}
