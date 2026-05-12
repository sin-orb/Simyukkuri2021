package src.logic;

import src.entity.core.living.yukkuri.Yukkuri;

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
		switch (self.getIntelligence()) {
			case WISE:
				if (targetBody.isSick()) {
					return true;
				}
				break;
			case AVERAGE:
				if (targetBody.isSick()) {
					return true;
				}
				break;
			case FOOL:
				if (targetBody.isSickHeavily()) {
					return true;
				}
				break;
		}
		return false;
	}
}
