package org.simyukkuri.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class GameLocaleTest {

	@AfterEach
	public void tearDown() {
		GameLocale.clearOverride();
	}

	@Test
	public void testGetLocaleUsesOverrideWhenSet() {
		final Locale locale = Locale.ENGLISH;
		GameLocale.setOverride(new LocaleSource() {
			@Override
			public Locale getLocale() {
				return locale;
			}
		});

		assertEquals(locale, GameLocale.getLocale());
		assertFalse(GameLocale.isJapanese());
	}

	@Test
	public void testIsJapaneseUsesOverrideLanguage() {
		GameLocale.setOverride(new LocaleSource() {
			@Override
			public Locale getLocale() {
				return Locale.JAPANESE;
			}
		});

		assertTrue(GameLocale.isJapanese());
	}
}
