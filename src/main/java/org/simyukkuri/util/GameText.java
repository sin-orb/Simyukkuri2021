package org.simyukkuri.util;

import org.simyukkuri.system.ResourceUtil;

/**
 * GameText.
 */
public final class GameText {
	private static TextSource override;

	private GameText() {
	}

	/** テキストプロパティキーに対応する文字列を返す。 */
	public static String read(String property) {
		if (override != null) {
			return override.read(property);
		}
		return ResourceUtil.getInstance().read(property);
	}

	/** テスト用の注入ソースをセットする。 */
	public static void setOverride(TextSource source) {
		override = source;
	}

	/** テスト用注入ソースをクリアする。 */
	public static void clearOverride() {
		override = null;
	}
}
