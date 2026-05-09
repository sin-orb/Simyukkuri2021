package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.base.Yukkuri;
import src.enums.PredatorType;
import src.util.WorldTestHelper;

class BodyAppearanceRuleTest {

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
	void detectsOkazariPresence() {
		assertTrue(BodyAppearanceRule.hasOkazari(body));

		body.setOkazari(null);
		assertFalse(BodyAppearanceRule.hasOkazari(body));
	}

	@Test
	void detectsPredatorTypePresence() {
		assertFalse(BodyAppearanceRule.isPredatorType(body));

		body.setPredatorType(PredatorType.BITE);
		assertTrue(BodyAppearanceRule.isPredatorType(body));
	}
}
