package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class YukkuriPlayerRelationTest {
	@Test
	void doSurisuriByPlayerReturnsFalseWhenNotFlagged() {
		StubBody body = new StubBody();

		assertFalse(new YukkuriPlayerRelation(body).doSurisuriByPlayer());
	}

	@Test
	void doSurisuriByPlayerInitialResponseUpdatesTime() {
		StubBody body = new StubBody();
		body.setSurisuriFromPlayer(true);

		assertTrue(new YukkuriPlayerRelation(body).doSurisuriByPlayer());
		assertTrue(body.getLastSurisuriTime() > 0);
	}
}
