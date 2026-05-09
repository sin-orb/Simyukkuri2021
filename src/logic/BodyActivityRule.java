package src.logic;

import src.base.BodyAttributes;

/**
 * 行動状態の単純判定を集約する.
 */
public final class BodyActivityRule {
	private BodyActivityRule() {
	}

	public static boolean isSleeping(BodyAttributes body) {
		return !body.isDead() && body.isSleepingRaw();
	}

	public static boolean isSleepy(BodyAttributes body) {
		return !body.isSleepingRaw() && body.getWakeUpTime() + body.getActivePeriodBase() < body.getAge();
	}

	public static boolean isShitting(BodyAttributes body) {
		return !body.isDead() && body.isShittingRaw();
	}

	public static boolean isExciting(BodyAttributes body) {
		return !body.isDead() && body.isExcitingRaw();
	}

	public static boolean isForceExciting(BodyAttributes body) {
		return !body.isDead() && body.isExcitingRaw() && body.isForceExcitingRaw();
	}

	public static boolean isYunnyaa(BodyAttributes body) {
		return !body.isDead() && body.isYunnyaaRaw();
	}

	public static boolean isCallingParents(BodyAttributes body) {
		return !body.isDead() && body.isCallingParentsRaw();
	}

	public static boolean isDirty(BodyAttributes body) {
		return !body.isDead() && (body.isDirtyRaw() || body.isStubbornlyDirtyRaw());
	}

	public static boolean isNormalDirty(BodyAttributes body) {
		return !body.isDead() && body.isDirtyRaw();
	}
}
