package org.simyukkuri.logic;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.enums.Event;
import org.simyukkuri.util.WorldTestHelper;

class BodyEventStateTest {

	private Yukkuri body;

	@BeforeEach
	void setUp() {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		body = WorldTestHelper.createBody();
	}

	@Test
	void clearActionsResetsMoveFlagsAndMoveTarget() {
		body.setToSukkiri(true);
		body.setToBed(true);
		body.setToFood(true);
		body.setToShit(true);
		body.setToBody(true);
		body.setToSteal(true);
		body.setMoveTargetId(42);

		BodyEventState.clearActions(body);

		assertFalse(body.isToSukkiri());
		assertFalse(body.isToBed());
		assertFalse(body.isToFood());
		assertFalse(body.isToShit());
		assertFalse(body.isToBody());
		assertFalse(body.isToSteal());
		assertEquals(-1, body.getMoveTargetId());
	}

	@Test
	void clearEventResetsCurrentEventAndForceFace() {
		body.setForceFace(5);
		body.setCurrentEvent(null);

		BodyEventState.clearEvent(body);

		assertNull(body.getCurrentEvent());
		assertEquals(-1, body.getForceFace());
	}

	@Test
	void setMessageIgnoresEmptyString() {
		body.setMessageTicks(0);

		BodyEventState.setMessage(body, "");

		assertEquals(0, body.getMessageTicks());
	}

	@Test
	void setPikoMessageWithCountUpdatesMessageCount() {
		body.setMessageTicks(0);

		BodyEventState.setPikoMessage(body, "hi", 3, true);

		assertEquals(3, body.getMessageTicks());
	}

	@Test
	void processPendingEventsStartsBodyEventBeforeWorldEvent() {
		TrackingEventPacket worldEvent = new TrackingEventPacket();
		TrackingEventPacket bodyEvent = new TrackingEventPacket();
		org.simyukkuri.SimYukkuri.world.getCurrentMap().getEvent().add(worldEvent);
		body.getEventList().add(bodyEvent);

		BodyEventState.processPendingEvents(body);

		assertSame(bodyEvent, body.getCurrentEvent());
		assertTrue(bodyEvent.started);
		assertFalse(worldEvent.started);
		assertEquals(0, body.getEventList().size());
	}

	@Test
	void processPendingEventsConsumesSimpleEventsWhenResponseDisabled() {
		TrackingEventPacket bodyEvent = new TrackingEventPacket();
		TrackingEventPacket worldEvent = new TrackingEventPacket();
		bodyEvent.simpleAction = true;
		worldEvent.simpleAction = true;
		body.setSleeping(true);
		body.getEventList().add(bodyEvent);
		org.simyukkuri.SimYukkuri.world.getCurrentMap().getEvent().add(worldEvent);

		BodyEventState.processPendingEvents(body);

		assertNull(body.getCurrentEvent());
		assertTrue(bodyEvent.simpleActionCalled);
		assertTrue(worldEvent.simpleActionCalled);
		assertEquals(0, body.getEventList().size());
	}

	@Test
	void resolveEventResultActionOverridesDoNothingWithLowPriorityEvent() {
		body.setCurrentEvent(new TrackingEventPacket(EventPacket.EventPriority.LOW));
		body.setEventResult(Event.DOSHIT);

		Event result = BodyEventState.resolveEventResultAction(body, Event.DONOTHING);

		assertEquals(Event.DOSHIT, result);
		assertEquals(Event.DONOTHING, body.getEventResult());
	}

	@Test
	void resolveEventResultActionKeepsExistingActionForLowPriorityEvent() {
		body.setCurrentEvent(new TrackingEventPacket(EventPacket.EventPriority.LOW));
		body.setEventResult(Event.DOSHIT);

		Event result = BodyEventState.resolveEventResultAction(body, Event.BIRTHBABY);

		assertEquals(Event.BIRTHBABY, result);
		assertEquals(Event.DOSHIT, body.getEventResult());
	}

	@Test
	void resolveEventResultActionOverridesExistingActionForHighPriorityEvent() {
		body.setCurrentEvent(new TrackingEventPacket(EventPacket.EventPriority.HIGH));
		body.setEventResult(Event.DOSHIT);

		Event result = BodyEventState.resolveEventResultAction(body, Event.BIRTHBABY);

		assertEquals(Event.DOSHIT, result);
		assertEquals(Event.DONOTHING, body.getEventResult());
	}

	@Test
	void updateCurrentEventClearsCurrentEventWhenAbortReturned() {
		TrackingEventPacket event = new TrackingEventPacket();
		event.updateState = EventPacket.UpdateState.ABORT;
		body.setCurrentEvent(event);

		BodyEventState.updateCurrentEvent(body);

		assertTrue(event.ended);
		assertNull(body.getCurrentEvent());
	}

	@Test
	void updateCurrentEventExecutesWhenBodyReachedTarget() {
		TrackingEventPacket event = new TrackingEventPacket();
		body.setX(100);
		body.setY(100);
		body.setZ(0);
		event.setToX(101);
		event.setToY(100);
		event.setToZ(0);
		body.setCurrentEvent(event);

		BodyEventState.updateCurrentEvent(body);

		assertTrue(event.executed);
		assertTrue(event.ended);
		assertNull(body.getCurrentEvent());
	}

	@Test
	void updateCurrentEventKeepsCurrentEventWhenTargetIsFar() {
		TrackingEventPacket event = new TrackingEventPacket();
		body.setX(0);
		body.setY(0);
		body.setZ(0);
		event.setToX(10000);
		event.setToY(10000);
		event.setToZ(0);
		body.setCurrentEvent(event);

		BodyEventState.updateCurrentEvent(body);

		assertFalse(event.executed);
		assertSame(event, body.getCurrentEvent());
	}

	private static final class TrackingEventPacket extends EventPacket {
		private static final long serialVersionUID = 1L;

		private boolean simpleAction;
		private boolean simpleActionCalled;
		private boolean started;
		private boolean executed;
		private boolean ended;
		private UpdateState updateState;

		private TrackingEventPacket() {
			this(EventPriority.LOW);
		}

		private TrackingEventPacket(EventPriority priority) {
			setPriority(priority);
		}

		@Override
		public boolean simpleEventAction(Yukkuri b) {
			simpleActionCalled = true;
			return simpleAction;
		}

		@Override
		public boolean checkEventResponse(Yukkuri b) {
			return true;
		}

		@Override
		public void start(Yukkuri b) {
			started = true;
			b.setCurrentEvent(this);
		}

		@Override
		public UpdateState update(Yukkuri b) {
			return updateState;
		}

		@Override
		public boolean execute(Yukkuri b) {
			executed = true;
			return true;
		}

		@Override
		public void end(Yukkuri b) {
			ended = true;
		}
	}
}
