package src.util;

import src.entity.core.living.yukkuri.Yukkuri;
import src.system.MessagePool;

public final class GameMessages {
	private static MessageSource override;

	private GameMessages() {
	}

	public static void loadMessage(ClassLoader loader) {
		if (override != null) {
			override.loadMessage(loader);
			return;
		}
		MessagePool.loadMessage(loader);
	}

	public static String getMessage(Yukkuri body, MessagePool.Action action) {
		if (override != null) {
			return override.getMessage(body, action);
		}
		return MessagePool.getMessage(body, action);
	}

	public static void setOverride(MessageSource source) {
		override = source;
	}

	public static void clearOverride() {
		override = null;
	}
}
