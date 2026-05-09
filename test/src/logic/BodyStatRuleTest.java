package src.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyStatRuleTest {
	@Test
	void delegatesStatProfiles() {
		StubBodyAttributes body = new StubBodyAttributes();

		BodyStatRule.setEatAmountBase(body, new int[] { 1, 2, 3 });
		BodyStatRule.setWeightBase(body, new int[] { 4, 5, 6 });
		BodyStatRule.setHungryLimitBase(body, new int[] { 7, 8, 9 });
		BodyStatRule.setShitLimitBase(body, new int[] { 10, 11, 12 });
		BodyStatRule.setDamageLimitBase(body, new int[] { 13, 14, 15 });
		BodyStatRule.setStressLimitBase(body, new int[] { 16, 17, 18 });
		BodyStatRule.setTangLevelBase(body, new int[] { 19, 20, 21 });
		BodyStatRule.setStrengthBase(body, new int[] { 22, 23, 24 });
		BodyStatRule.setImmunity(body, new int[] { 25, 26, 27, 28 });
		BodyStatRule.setRudeLimit(body, new int[] { 29, 30 });

		assertArrayEquals(new int[] { 1, 2, 3 }, BodyStatRule.getEatAmountBase(body));
		assertArrayEquals(new int[] { 4, 5, 6 }, BodyStatRule.getWeightBase(body));
		assertArrayEquals(new int[] { 7, 8, 9 }, BodyStatRule.getHungryLimitBase(body));
		assertArrayEquals(new int[] { 10, 11, 12 }, BodyStatRule.getShitLimitBase(body));
		assertArrayEquals(new int[] { 13, 14, 15 }, BodyStatRule.getDamageLimitBase(body));
		assertArrayEquals(new int[] { 16, 17, 18 }, BodyStatRule.getStressLimitBase(body));
		assertArrayEquals(new int[] { 19, 20, 21 }, BodyStatRule.getTangLevelBase(body));
		assertArrayEquals(new int[] { 22, 23, 24 }, BodyStatRule.getStrengthBase(body));
		assertArrayEquals(new int[] { 25, 26, 27, 28 }, BodyStatRule.getImmunity(body));
		assertArrayEquals(new int[] { 29, 30 }, BodyStatRule.getRudeLimit(body));
	}
}
