package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.base.Yukkuri;
import src.util.WorldTestHelper;

class BodyDependencyRuleTest {

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
	void detectsBoundStalk() {
		assertFalse(BodyDependencyRule.hasBindStalk(body));

		body.setBindStalk(new src.game.Stalk());

		assertTrue(BodyDependencyRule.hasBindStalk(body));
	}

	@Test
	void detectsBabyOrStalkFlags() {
		assertFalse(BodyDependencyRule.hasBabyOrStalk(body));

		body.setHasBaby(true);
		assertTrue(BodyDependencyRule.hasBabyOrStalk(body));

		body.setHasBaby(false);
		body.setHasStalk(true);
		assertTrue(BodyDependencyRule.hasBabyOrStalk(body));
	}
}
