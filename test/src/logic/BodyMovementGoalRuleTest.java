package src.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.base.StubBodyAttributes;
import src.enums.PurposeOfMoving;

class BodyMovementGoalRuleTest {
	@Test
	void classifiesMovementFlagsAndPurposes() {
		StubBodyAttributes body = new StubBodyAttributes();

		body.setLockmove(true);
		assertTrue(BodyMovementGoalRule.isLockmove(body));
		body.setLockmove(false);
		assertFalse(BodyMovementGoalRule.isLockmove(body));

		body.setCanPullOrPush(true);
		assertTrue(BodyMovementGoalRule.canPullOrPush(body));
		body.setCanPullOrPush(false);
		assertFalse(BodyMovementGoalRule.canPullOrPush(body));

		body.setTargetBind(true);
		assertTrue(BodyMovementGoalRule.isTargetBind(body));
		body.setTargetBind(false);
		assertFalse(BodyMovementGoalRule.isTargetBind(body));

		body.setPurposeOfMoving(PurposeOfMoving.FOOD);
		assertTrue(BodyMovementGoalRule.isToFood(body));
		assertFalse(BodyMovementGoalRule.isToSukkiri(body));
		assertFalse(BodyMovementGoalRule.isToShit(body));

		body.setPurposeOfMoving(PurposeOfMoving.SUKKIRI);
		assertTrue(BodyMovementGoalRule.isToSukkiri(body));
		assertFalse(BodyMovementGoalRule.isToFood(body));

		body.setPurposeOfMoving(PurposeOfMoving.SHIT);
		assertTrue(BodyMovementGoalRule.isToShit(body));
		assertFalse(BodyMovementGoalRule.isToBed(body));

		body.setPurposeOfMoving(PurposeOfMoving.BED);
		assertTrue(BodyMovementGoalRule.isToBed(body));
		assertFalse(BodyMovementGoalRule.isToBody(body));

		body.setPurposeOfMoving(PurposeOfMoving.YUKKURI);
		assertTrue(BodyMovementGoalRule.isToBody(body));
		assertFalse(BodyMovementGoalRule.isToSteal(body));

		body.setPurposeOfMoving(PurposeOfMoving.STEAL);
		assertTrue(BodyMovementGoalRule.isToSteal(body));
		assertFalse(BodyMovementGoalRule.isToTakeout(body));

		body.setPurposeOfMoving(PurposeOfMoving.TAKEOUT);
		assertTrue(BodyMovementGoalRule.isToTakeout(body));
	}
}
