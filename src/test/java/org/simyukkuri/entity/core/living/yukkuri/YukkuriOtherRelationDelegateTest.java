package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class YukkuriOtherRelationDelegateTest {
	@Test
	void doSurisuriReturnsWithoutThrowingWhenActorIsDead() {
		StubBody body = new StubBody();
		StubBody target = new StubBody();
		body.setDead(true);

		assertDoesNotThrow(() -> new YukkuriOtherRelationDelegate(body).doSurisuri(target));
	}

	@Test
	void doPeroperoReturnsWithoutThrowingWhenActorIsDead() {
		StubBody body = new StubBody();
		StubBody target = new StubBody();
		body.setDead(true);

		assertDoesNotThrow(() -> new YukkuriOtherRelationDelegate(body).doPeropero(target));
	}
}
