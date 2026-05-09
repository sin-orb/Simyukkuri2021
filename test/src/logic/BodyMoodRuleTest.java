package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.base.Yukkuri;
import src.enums.Happiness;
import src.util.WorldTestHelper;

class BodyMoodRuleTest {

	private Yukkuri body;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		body = WorldTestHelper.createBody();
	}

	@AfterEach
	void tearDown() {
		WorldTestHelper.resetWorld();
	}

	@Test
	void detectsAngryAndScareFlags() {
		assertFalse(BodyMoodRule.isAngry(body));
		assertFalse(BodyMoodRule.isScare(body));

		body.setAngry(true);
		body.setScare(true);

		assertTrue(BodyMoodRule.isAngry(body));
		assertTrue(BodyMoodRule.isScare(body));
	}

	@Test
	void detectsHappinessStates() {
		body.setHappiness(Happiness.SAD);
		assertTrue(BodyMoodRule.isSad(body));
		assertTrue(BodyMoodRule.isUnhappy(body));
		assertFalse(BodyMoodRule.isHappy(body));

		body.setHappiness(Happiness.VERY_HAPPY);
		assertTrue(BodyMoodRule.isHappy(body));
		assertFalse(BodyMoodRule.isUnhappy(body));
	}
}
