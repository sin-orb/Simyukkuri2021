package org.simyukkuri.util;

import java.util.Locale;

import org.simyukkuri.system.ResourceUtil;

/**
 * GameLocale.
 */
public final class GameLocale {
	private static LocaleSource override;

	private GameLocale() {
	}

	/** システムロケールを返す。 */
	public static Locale getLocale() {
		if (override != null) {
			return override.getLocale();
		}
		return Locale.getDefault();
	}

	/** ロケールが日本語かどうかを返す。 */
	public static boolean isJapanese() {
		return ResourceUtil.JAPANESE.getLanguage().equals(getLocale().getLanguage());
	}

	/** テスト用の注入ソースをセットする。 */
	public static void setOverride(LocaleSource source) {
		override = source;
	}

	/** テスト用注入ソースをクリアする。 */
	public static void clearOverride() {
		override = null;
	}
}
