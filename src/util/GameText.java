package src.util;

import src.system.ResourceUtil;

public final class GameText {
	private static TextSource override;

	private GameText() {
	}

	public static String read(String property) {
		if (override != null) {
			return override.read(property);
		}
		return ResourceUtil.getInstance().read(property);
	}

	public static void setOverride(TextSource source) {
		override = source;
	}

	public static void clearOverride() {
		override = null;
	}
}
