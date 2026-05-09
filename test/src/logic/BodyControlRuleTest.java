package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;

class BodyControlRuleTest {
	@Test
	void classifiesControlFlags() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setLockmove(true);
		assertTrue(BodyControlRule.isLockmove(body));
		body.setLockmove(false);
		assertFalse(BodyControlRule.isLockmove(body));

		body.setCanPullOrPush(true);
		assertTrue(BodyControlRule.canPullOrPush(body));
		body.setCanPullOrPush(false);
		assertFalse(BodyControlRule.canPullOrPush(body));

		body.setInOutTakeoutItem(true);
		assertTrue(BodyControlRule.isInOutTakeoutItem(body));
		body.setInOutTakeoutItem(false);
		assertFalse(BodyControlRule.isInOutTakeoutItem(body));

		body.setStaying(true);
		assertTrue(BodyControlRule.isStaying(body));
		body.setStaying(false);
		assertFalse(BodyControlRule.isStaying(body));

		body.setFixBack(true);
		assertTrue(BodyControlRule.isFixBack(body));
		body.setFixBack(false);
		assertFalse(BodyControlRule.isFixBack(body));

		body.setShakePhase(true);
		assertTrue(BodyControlRule.isShakePhase(body));
		body.setShakePhase(false);
		assertFalse(BodyControlRule.isShakePhase(body));

		body.setPinned(true);
		assertTrue(BodyControlRule.isPinned(body));
		body.setPinned(false);
		assertFalse(BodyControlRule.isPinned(body));

		body.setNoDamageNextFall(true);
		assertTrue(BodyControlRule.isNoDamageNextFall(body));
		body.setNoDamageNextFall(false);
		assertFalse(BodyControlRule.isNoDamageNextFall(body));

		body.setFirstGround(true);
		assertTrue(BodyControlRule.isFirstGround(body));
		body.setFirstGround(false);
		assertFalse(BodyControlRule.isFirstGround(body));

		body.setTargetBind(true);
		assertTrue(BodyControlRule.isTargetBind(body));
		body.setTargetBind(false);
		assertFalse(BodyControlRule.isTargetBind(body));
	}
}
