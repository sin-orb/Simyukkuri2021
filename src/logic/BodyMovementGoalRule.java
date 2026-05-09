package src.logic;

import src.base.BodyAttributes;
import src.enums.PurposeOfMoving;

/**
 * Bodyの移動条件・目的フラグを集約する.
 */
public final class BodyMovementGoalRule {
	private BodyMovementGoalRule() {
	}

	public static boolean isLockmove(BodyAttributes body) {
		return body.isLockmoveRaw();
	}

	public static boolean isPullAndPush(BodyAttributes body) {
		return body.isPullAndPushRaw();
	}

	public static boolean isTargetBind(BodyAttributes body) {
		return body.isTargetBindRaw();
	}

	public static boolean isToFood(BodyAttributes body) {
		return body.getPurposeOfMoving() == PurposeOfMoving.FOOD;
	}

	public static boolean isToSukkiri(BodyAttributes body) {
		return body.getPurposeOfMoving() == PurposeOfMoving.SUKKIRI;
	}

	public static boolean isToShit(BodyAttributes body) {
		return body.getPurposeOfMoving() == PurposeOfMoving.SHIT;
	}

	public static boolean isToBed(BodyAttributes body) {
		return body.getPurposeOfMoving() == PurposeOfMoving.BED;
	}

	public static boolean isToBody(BodyAttributes body) {
		return body.getPurposeOfMoving() == PurposeOfMoving.YUKKURI;
	}

	public static boolean isToSteal(BodyAttributes body) {
		return body.getPurposeOfMoving() == PurposeOfMoving.STEAL;
	}

	public static boolean isToTakeout(BodyAttributes body) {
		return body.getPurposeOfMoving() == PurposeOfMoving.TAKEOUT;
	}
}
