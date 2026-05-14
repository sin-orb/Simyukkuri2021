package org.simyukkuri.util;

import org.simyukkuri.entity.core.living.LivingEntity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.system.MessagePool;

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

	public static String getMessage(LivingEntity body, MessagePool.Action action) {
		if (body instanceof Yukkuri) {
			return getMessage((Yukkuri) body, action);
		}
		return null;
	}

	public static void setOverride(MessageSource source) {
		override = source;
	}

	public static void clearOverride() {
		override = null;
	}
}
