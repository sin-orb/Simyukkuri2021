package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの制御・表示寄りの状態を集約する.
 */
public final class BodyControlRule {
	private BodyControlRule() {
	}

	public static boolean isLockmove(BodyAttributes body) {
		return body.isLockmoveRaw();
	}

	public static boolean isPullAndPush(BodyAttributes body) {
		return body.isPullAndPushRaw();
	}

	public static boolean isInOutTakeoutItem(BodyAttributes body) {
		return body.isInOutTakeoutItemRaw();
	}

	public static boolean isStaying(BodyAttributes body) {
		return body.isStayingRaw();
	}

	public static boolean isFixBack(BodyAttributes body) {
		return body.isFixBackRaw();
	}

	public static boolean isShakePhase(BodyAttributes body) {
		return body.isShakePhaseRaw();
	}

	public static boolean isPinned(BodyAttributes body) {
		return body.isPinRaw();
	}

	public static boolean isNoDamageNextFall(BodyAttributes body) {
		return body.isNoDamageNextFallRaw();
	}

	public static boolean isFirstGround(BodyAttributes body) {
		return body.isFirstGroundRaw();
	}

	public static boolean isTargetBind(BodyAttributes body) {
		return body.isTargetBindRaw();
	}
}
