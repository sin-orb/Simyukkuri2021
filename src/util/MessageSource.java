package src.util;

import src.entity.core.living.yukkuri.Yukkuri;
import src.system.MessagePool;

public interface MessageSource {
	void loadMessage(ClassLoader loader);

	String getMessage(Yukkuri body, MessagePool.Action action);
}
