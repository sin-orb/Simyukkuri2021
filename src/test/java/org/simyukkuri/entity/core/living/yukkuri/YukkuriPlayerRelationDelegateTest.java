package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class YukkuriPlayerRelationDelegateTest {
	@Test
	void doSurisuriByPlayerReturnsFalseWhenNotFlagged() {
		StubBody body = new StubBody();

		assertFalse(new YukkuriPlayerRelationDelegate(body).doSurisuriByPlayer());
	}

	@Test
	void doSurisuriByPlayerInitialResponseUpdatesTime() {
		StubBody body = new StubBody();
		body.setSurisuriFromPlayer(true);

		assertTrue(new YukkuriPlayerRelationDelegate(body).doSurisuriByPlayer());
		assertTrue(body.getLastSurisuriTime() > 0);
	}
}
