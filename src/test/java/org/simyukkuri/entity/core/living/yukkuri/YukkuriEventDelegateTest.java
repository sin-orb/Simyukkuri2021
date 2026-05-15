package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.PlayStyle;
import org.simyukkuri.event.impl.CutPenipeniEvent;
import org.simyukkuri.event.impl.BegForLifeEvent;

class YukkuriEventDelegateTest {
	@Test
	void clearActionsClearsPlayingAndMoveTarget() {
		StubBody body = new StubBody();
		body.setPlaying(PlayStyle.BALL);
		body.setMoveTargetId(123);

		new YukkuriEventDelegate(body).clearActions();

		assertEquals(null, body.getPlaying());
		assertEquals(-1, body.getMoveTargetId());
	}

	@Test
	void forceToSleepPutsBodyToSleep() {
		StubBody body = new StubBody();
		body.setDead(false);
		body.setSleeping(false);
		body.setPanicType(null);

		new YukkuriEventDelegate(body).forceToSleep();

		assertTrue(body.isSleeping());
	}

	@Test
	void begForLifeForcedAddsBodyEvent() {
		StubBody body = new StubBody();
		body.setDead(false);
		body.setDamage(0);

		new YukkuriEventDelegate(body).begForLife(true);

		assertFalse(body.getEvents().isEmpty());
		assertTrue(body.getEvents().get(0) instanceof BegForLifeEvent);
	}

	@Test
	void canEventResponse_returnsFalseWhenBlindAndNotCutPeni() {
		StubBody body = new StubBody();
		body.setDead(false);
		body.setCriticalDamageType(CriticalDamageType.INJURED);
		body.setBlind(true);

		assertFalse(new YukkuriEventDelegate(body).canEventResponse());
	}

	@Test
	void canEventResponse_returnsTrueWhenBlindButCutPeniEventIsQueued() {
		StubBody body = new StubBody();
		body.setDead(false);
		body.setCriticalDamageType(CriticalDamageType.INJURED);
		body.setBlind(true);
		body.getEvents().add(new CutPenipeniEvent(body, null, null, 1));

		assertTrue(new YukkuriEventDelegate(body).canEventResponse());
	}

	@Test
	void isCutPeni_returnsTrueWhenFirstEventIsCutPenipeniEvent() {
		StubBody body = new StubBody();
		body.getEvents().add(new CutPenipeniEvent(body, null, null, 1));

		assertTrue(new YukkuriEventDelegate(body).isCutPeni());
	}
}
