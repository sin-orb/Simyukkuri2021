package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの表示・発話に関する単純判定を集約する.
 */
public final class BodyPresentationRule {
	private BodyPresentationRule() {
	}

	public static boolean isImageNagasiMode(BodyAttributes body) {
		return body.isImageNagasiModeRaw();
	}

	public static boolean isShadowVisible(BodyAttributes body) {
		return body.isDropShadowRaw();
	}

	public static boolean isSilent(BodyAttributes body) {
		return body.isSilentRaw();
	}

	public static boolean isShutmouth(BodyAttributes body) {
		return body.isShutmouthRaw();
	}

	public static boolean isPikopiko(BodyAttributes body) {
		return body.isPikopikoRaw();
	}

	public static boolean isPurupuru(BodyAttributes body) {
		return body.isPurupuruRaw();
	}
}
