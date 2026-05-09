package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyTraitRuleTest {
	@Test
	void detectsRareTypeAndPreferences() {
		StubBodyAttributes body = new StubBodyAttributes();
		BodyTraitRule.setRareType(body, false);
		BodyTraitRule.setLikeBitterFood(body, false);
		BodyTraitRule.setLikeHotFood(body, false);
		BodyTraitRule.setLikeWater(body, false);
		BodyTraitRule.setFlyingType(body, false);
		BodyTraitRule.setBraidType(body, false);

		assertFalse(BodyTraitRule.isRareType(body));
		assertFalse(BodyTraitRule.isLikeBitterFood(body));
		assertFalse(BodyTraitRule.isLikeHotFood(body));
		assertFalse(BodyTraitRule.isLikeWater(body));
		assertFalse(BodyTraitRule.isFlyingType(body));
		assertFalse(BodyTraitRule.isBraidType(body));

		BodyTraitRule.setRareType(body, true);
		BodyTraitRule.setLikeBitterFood(body, true);
		BodyTraitRule.setLikeHotFood(body, true);
		BodyTraitRule.setLikeWater(body, true);
		BodyTraitRule.setFlyingType(body, true);
		BodyTraitRule.setBraidType(body, true);

		assertTrue(BodyTraitRule.isRareType(body));
		assertTrue(BodyTraitRule.isLikeBitterFood(body));
		assertTrue(BodyTraitRule.isLikeHotFood(body));
		assertTrue(BodyTraitRule.isLikeWater(body));
		assertTrue(BodyTraitRule.isFlyingType(body));
		assertTrue(BodyTraitRule.isBraidType(body));
	}
}
