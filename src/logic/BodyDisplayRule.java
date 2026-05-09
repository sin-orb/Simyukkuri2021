package src.logic;

import src.base.BodyAttributes;
import src.logic.BodyControlRule;

/**
 * Bodyの表示・UI状態を集約する.
 */
public final class BodyDisplayRule {
	private BodyDisplayRule() {
	}

	public static boolean isImageNagasiMode(BodyAttributes body) {
		return BodyPresentationRule.isImageNagasiMode(body);
	}

	public static boolean isShadowVisible(BodyAttributes body) {
		return BodyPresentationRule.isShadowVisible(body);
	}

	public static boolean isPinned(BodyAttributes body) {
		return BodyControlRule.isPinned(body);
	}
}
