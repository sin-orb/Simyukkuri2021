package src.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyPreferenceRuleTest {
	@Test
	void delegatesPreferenceProfiles() {
		StubBodyAttributes body = new StubBodyAttributes();

		BodyPreferenceRule.setLovePlayerLimitBase(body, 11);
		BodyPreferenceRule.setCleaningFailProbWise(body, new int[] { 1, 2, 3 });
		BodyPreferenceRule.setCleaningFailProbAverage(body, new int[] { 4, 5, 6 });
		BodyPreferenceRule.setCleaningFailProbFool(body, new int[] { 7, 8, 9 });
		BodyPreferenceRule.setNiceLimit(body, new int[] { 10, 20 });
		BodyPreferenceRule.setSameDirectionFactor(body, 13);
		BodyPreferenceRule.setNotChangeCharacter(body, true);
		BodyPreferenceRule.setUseRealPregnantLimit(body, false);

		assertTrue(11 == BodyPreferenceRule.getLovePlayerLimitBase(body));
		assertArrayEquals(new int[] { 1, 2, 3 }, BodyPreferenceRule.getCleaningFailProbWise(body));
		assertArrayEquals(new int[] { 4, 5, 6 }, BodyPreferenceRule.getCleaningFailProbAverage(body));
		assertArrayEquals(new int[] { 7, 8, 9 }, BodyPreferenceRule.getCleaningFailProbFool(body));
		assertArrayEquals(new int[] { 10, 20 }, BodyPreferenceRule.getNiceLimit(body));
		assertTrue(13 == BodyPreferenceRule.getSameDirectionFactor(body));
		assertTrue(BodyPreferenceRule.isNotChangeCharacter(body));
		assertFalse(BodyPreferenceRule.isUseRealPregnantLimit(body));
	}
}
