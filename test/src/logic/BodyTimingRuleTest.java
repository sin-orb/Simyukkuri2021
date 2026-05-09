package src.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyTimingRuleTest {
	@Test
	void delegatesTimingProfiles() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setDeclinePeriodBase(10);
		body.setBlockedLimitBase(11);
		body.setDirtyPeriodBase(12);
		body.setEyesightBase(13);
		body.setIncubationPeriodBase(14);
		body.setBabyLimitBase(11);
		body.setChildLimitBase(22);
		body.setLifeLimitBase(33);
		body.setRottingTimeBase(44);
		body.setStepBase(new int[] { 5, 6, 7 });
		body.setRelaxPeriodBase(55);
		body.setExcitePeriodBase(66);
		body.setPregPeriodBase(77);
		body.setSleepPeriodBase(88);
		body.setActivePeriodBase(99);
		body.setAngryPeriodBase(111);
		body.setScarePeriodBase(122);

		assertEquals(10, BodyTimingRule.getDeclinePeriodBase(body));
		assertEquals(11, BodyTimingRule.getBlockedLimitBase(body));
		assertEquals(12, BodyTimingRule.getDirtyPeriodBase(body));
		assertEquals(13, BodyTimingRule.getEyesightBase(body));
		assertEquals(14, BodyTimingRule.getIncubationPeriodBase(body));
		assertEquals(11, BodyTimingRule.getBabyLimitBase(body));
		assertEquals(22, BodyTimingRule.getChildLimitBase(body));
		assertEquals(33, BodyTimingRule.getLifeLimitBase(body));
		assertEquals(44, BodyTimingRule.getRottingTimeBase(body));
		assertArrayEquals(new int[] { 5, 6, 7 }, BodyTimingRule.getStepBase(body));
		assertEquals(55, BodyTimingRule.getRelaxPeriodBase(body));
		assertEquals(66, BodyTimingRule.getExcitePeriodBase(body));
		assertEquals(77, BodyTimingRule.getPregPeriodBase(body));
		assertEquals(88, BodyTimingRule.getSleepPeriodBase(body));
		assertEquals(99, BodyTimingRule.getActivePeriodBase(body));
		assertEquals(111, BodyTimingRule.getAngryPeriodBase(body));
		assertEquals(122, BodyTimingRule.getScarePeriodBase(body));

		BodyTimingRule.setBabyLimitBase(body, 12);
		BodyTimingRule.setChildLimitBase(body, 23);
		BodyTimingRule.setLifeLimitBase(body, 34);
		BodyTimingRule.setRottingTimeBase(body, 45);
		BodyTimingRule.setStepBase(body, new int[] { 8, 9, 10 });
		BodyTimingRule.setRelaxPeriodBase(body, 56);
		BodyTimingRule.setExcitePeriodBase(body, 67);
		BodyTimingRule.setPregPeriodBase(body, 78);
		BodyTimingRule.setSleepPeriodBase(body, 89);
		BodyTimingRule.setActivePeriodBase(body, 100);
		BodyTimingRule.setAngryPeriodBase(body, 112);
		BodyTimingRule.setScarePeriodBase(body, 123);

		assertEquals(10, body.getDeclinePeriodBase());
		assertEquals(11, body.getBlockedLimitBase());
		assertEquals(12, body.getDirtyPeriodBase());
		assertEquals(13, body.getEyesightBase());
		assertEquals(14, body.getIncubationPeriodBase());
		assertEquals(12, body.getBabyLimitBase());
		assertEquals(23, body.getChildLimitBase());
		assertEquals(34, body.getLifeLimitBase());
		assertEquals(45, body.getRottingTimeBase());
		assertArrayEquals(new int[] { 8, 9, 10 }, body.getStepBase());
		assertEquals(56, body.getRelaxPeriodBase());
		assertEquals(67, body.getExcitePeriodBase());
		assertEquals(78, body.getPregPeriodBase());
		assertEquals(89, body.getSleepPeriodBase());
		assertEquals(100, body.getActivePeriodBase());
		assertEquals(112, body.getAngryPeriodBase());
		assertEquals(123, body.getScarePeriodBase());
	}
}
