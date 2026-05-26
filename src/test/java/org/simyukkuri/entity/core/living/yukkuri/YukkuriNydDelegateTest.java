package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class YukkuriNydDelegateTest {
	@Test
	void hasNonYukkuriDisease_defaultBodyReturnsFalse() {
		StubBody body = new StubBody();

		assertFalse(new YukkuriNydDelegate(body).hasNonYukkuriDisease());
		assertTrue(body.isNotNyd());
	}
}
