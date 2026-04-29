package src.util;

import java.util.Locale;

import src.system.ResourceUtil;

public final class GameLocale {
	private static LocaleSource override;

	private GameLocale() {
	}

	public static Locale getLocale() {
		if (override != null) {
			return override.getLocale();
		}
		return Locale.getDefault();
	}

	public static boolean isJapanese() {
		return ResourceUtil.JAPANESE.getLanguage().equals(getLocale().getLanguage());
	}

	public static void setOverride(LocaleSource source) {
		override = source;
	}

	public static void clearOverride() {
		override = null;
	}
}
