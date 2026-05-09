package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.base.Body;
import src.util.WorldTestHelper;

class BodyActivityRuleTest {

	private Body body;

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
	void detectsSleepingAndSleepy() {
		body.setSleeping(true);
		assertTrue(BodyActivityRule.isSleeping(body));

		body.setSleeping(false);
		body.setWakeUpTime(0);
		body.setAge(body.getActivePeriodBase() + 1);
		assertTrue(BodyActivityRule.isSleepy(body));
	}

	@Test
	void detectsActivityFlags() {
		body.setShitting(true);
		body.setExciting(true);
		body.setYunnyaa(true);
		body.setCallingParents(true);
		body.setDirty(true);

		assertTrue(BodyActivityRule.isShitting(body));
		assertTrue(BodyActivityRule.isExciting(body));
		assertTrue(BodyActivityRule.isYunnyaa(body));
		assertTrue(BodyActivityRule.isCallingParents(body));
		assertTrue(BodyActivityRule.isDirty(body));
		assertTrue(BodyActivityRule.isNormalDirty(body));

		body.setDirty(false);
		body.setStubbornlyDirty(false);
		body.setExciting(false);
		body.setShitting(false);
		body.setYunnyaa(false);
		body.setCallingParents(false);

		assertFalse(BodyActivityRule.isShitting(body));
		assertFalse(BodyActivityRule.isExciting(body));
		assertFalse(BodyActivityRule.isYunnyaa(body));
		assertFalse(BodyActivityRule.isCallingParents(body));
		assertFalse(BodyActivityRule.isDirty(body));
		assertFalse(BodyActivityRule.isNormalDirty(body));
	}
}
