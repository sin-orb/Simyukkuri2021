package src.draw;

import src.enums.YukkuriType;
import src.util.GameView;

/**
 * Terrarium から UI 側へ渡す描画補助。
 */
public final class TerrariumViewBridge {
	private TerrariumViewBridge() {
	}

	/**
	 * ゆっくり画像の再読込を要求する。
	 *
	 * @param type 読み直す種別
	 */
	public static void loadBodyImageSafe(YukkuriType type) {
		if (GameView.getPane() != null) {
			GameView.loadBodyImage(type);
		}
	}
}
