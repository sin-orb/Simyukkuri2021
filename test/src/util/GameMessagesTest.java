package src.util;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import src.entity.core.living.yukkuri.Yukkuri;
import src.system.MessagePool;

public class GameMessagesTest {

	@AfterEach
	public void tearDown() {
		GameMessages.clearOverride();
	}

	@Test
	public void testLoadAndGetMessageUseOverrideWhenSet() {
		final ClassLoader expectedLoader = getClass().getClassLoader();
		final RecordingMessageSource source = new RecordingMessageSource();
		GameMessages.setOverride(source);

		GameMessages.loadMessage(expectedLoader);
		String message = GameMessages.getMessage(null, MessagePool.Action.Hungry);

		assertSame(expectedLoader, source.loader);
		assertSame(MessagePool.Action.Hungry, source.action);
		assertTrue(source.loaded);
		assertEquals("fake-message", message);
	}

	private static class RecordingMessageSource implements MessageSource {
		private boolean loaded;
		private ClassLoader loader;
		private MessagePool.Action action;

		@Override
		public void loadMessage(ClassLoader loader) {
			this.loaded = true;
			this.loader = loader;
		}

		@Override
		public String getMessage(Yukkuri body, MessagePool.Action action) {
			this.action = action;
			return "fake-message";
		}
	}
}
