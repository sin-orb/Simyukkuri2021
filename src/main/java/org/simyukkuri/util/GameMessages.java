package org.simyukkuri.util;

import org.simyukkuri.entity.core.living.LivingEntity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.system.MessagePool;

/**
 * GameMessages.
 */
public final class GameMessages {
	private static MessageSource override;

	private GameMessages() {
	}

	/** メッセージプールをクラスローダーでロードする。 */
	public static void loadMessage(ClassLoader loader) {
		if (override != null) {
			override.loadMessage(loader);
			return;
		}
		MessagePool.loadMessage(loader);
	}

	/** ゆっくりの行動に対応するメッセージ文字列を返す。 */
	public static String getMessage(Yukkuri body, MessagePool.Action action) {
		if (override != null) {
			return override.getMessage(body, action);
		}
		return MessagePool.getMessage(body, action);
	}

	/** ゆっくりの行動に対応するメッセージ文字列を返す。 */
	public static String getMessage(LivingEntity body, MessagePool.Action action) {
		if (body instanceof Yukkuri) {
			return getMessage((Yukkuri) body, action);
		}
		return null;
	}

	/** テスト用の注入ソースをセットする。 */
	public static void setOverride(MessageSource source) {
		override = source;
	}

	/** テスト用注入ソースをクリアする。 */
	public static void clearOverride() {
		override = null;
	}
}
