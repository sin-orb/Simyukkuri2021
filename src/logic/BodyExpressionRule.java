package src.logic;

import src.base.BodyAttributes;
import src.logic.BodyPresentationRule;

/**
 * Bodyの表情・表示状態を集約する.
 */
public final class BodyExpressionRule {
	private BodyExpressionRule() {
	}

	public static boolean isSilent(BodyAttributes body) {
		return BodyPresentationRule.isSilent(body);
	}

	public static boolean isShutmouth(BodyAttributes body) {
		return BodyPresentationRule.isShutmouth(body);
	}

	public static boolean isPikopiko(BodyAttributes body) {
		return BodyPresentationRule.isPikopiko(body);
	}

	public static boolean isPurupuru(BodyAttributes body) {
		return BodyPresentationRule.isPurupuru(body);
	}

	/**
	 * ぷるぷるアニメーション位相を判定する.
	 *
	 * @param body 判定対象
	 * @return 位相が立っていればtrue
	 */
	public static boolean isShakePhase(BodyAttributes body) {
		return body.isShakePhaseRaw();
	}
}
