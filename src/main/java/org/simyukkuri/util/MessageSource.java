package org.simyukkuri.util;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.system.MessagePool;

/**
 * MessageSource interface.
 */
public interface MessageSource {
	void loadMessage(ClassLoader loader);

	String getMessage(Yukkuri body, MessagePool.Action action);
}
