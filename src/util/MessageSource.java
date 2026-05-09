package src.util;

import src.base.Yukkuri;
import src.system.MessagePool;

public interface MessageSource {
	void loadMessage(ClassLoader loader);

	String getMessage(Yukkuri body, MessagePool.Action action);
}
