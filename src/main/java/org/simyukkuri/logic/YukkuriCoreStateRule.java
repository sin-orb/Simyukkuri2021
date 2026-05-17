package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Damage;

/**
 * Bodyのコア状態を集約する.
 */
public final class YukkuriCoreStateRule {
	private YukkuriCoreStateRule() {
	}

	/** ゆっくりのダメージ量を評価してダメージ状態を返す。致死ダメージなら toDead() を呼び出す。 */
	public static Damage getDamageState(Yukkuri body) {
		int limit = body.getDamageLimitBase()[body.getAgeState().ordinal()];
		int damage = body.getDamage();
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
