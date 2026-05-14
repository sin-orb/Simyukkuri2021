package org.simyukkuri.logic;

import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;

/**
 * かび・病気の見分けに関する判定を集約する.
 */
public final class BodyIllnessRule {
	private BodyIllnessRule() {
	}

	/**
	 * この個体が相手の病気を見抜けるかを返却する.
	 *
	 * @param self       判定元
	 * @param targetBody 判定対象
	 * @return 病気を見抜けるならtrue
	 */
	public static boolean findSick(Yukkuri self, Yukkuri targetBody) {
		return findSick(self.getIntelligence(), targetBody);
	}

	/**
	 * この知能が相手の病気を見抜けるかを返却する.
	 *
	 * @param intelligence 判定元の知能
	 * @param targetBody   判定対象
	 * @return 病気を見抜けるならtrue
	 */
	public static boolean findSick(Intelligence intelligence, Yukkuri targetBody) {
		if (targetBody == null) {
			return false;
		}
		switch (intelligence) {
			case WISE:
			case AVERAGE:
				return targetBody.isSick();
			case FOOL:
				return targetBody.isSickHeavily();
			default:
				return false;
		}
	}
}
