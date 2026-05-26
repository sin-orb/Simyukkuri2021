package org.simyukkuri.util;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.system.MessagePool;

/**
 * MessageSource interface.
 */
public interface MessageSource {
	/**
	 * @param loader クラスローダー
	 */
	void loadMessage(ClassLoader loader);

	/**
	 * @param body   対象ゆっくり
	 * @param action メッセージアクション
	 * @return メッセージ文字列
	 */
	String getMessage(Yukkuri body, MessagePool.Action action);
}
