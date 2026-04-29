package src.util;

import src.base.Body;
import src.system.MessagePool;

public interface MessageSource {
	void loadMessage(ClassLoader loader);

	String getMessage(Body body, MessagePool.Action action);
}
