package src.logic;

import src.base.Yukkuri;

import src.enums.Damage;

/**
 * Bodyのコア状態を集約する.
 */
public final class BodyCoreStateRule {
	private BodyCoreStateRule() {
	}

	public static Damage getDamageState(Yukkuri body) {
		int limit = body.getDamageLimitBase()[body.getBodyAgeState().ordinal()];
		int damage = body.getDamageRaw();
		if (damage > limit) {
			body.toDead();
			return Damage.TOOMUCH;
		}
		if (damage >= limit * 3 / 4) {
			return Damage.TOOMUCH;
		}
		if (damage >= limit / 2) {
			return Damage.VERY;
		}
		return Damage.NONE;
	}
}
