package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.event.EventPacket;
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
		body.setToYukkuri(true);
		body.setToSteal(true);
		body.setMoveTargetId(42);

		YukkuriEventState.clearActions(body);

		assertFalse(body.isToSukkiri(),   "clearActions 後は toSukkiri=false");
		assertFalse(body.isToBed(),       "clearActions 後は toBed=false");
		assertFalse(body.isToFood(),      "clearActions 後は toFood=false");
		assertFalse(body.isToShit(),      "clearActions 後は toShit=false");
		assertFalse(body.isToYukkuri(),   "clearActions 後は toYukkuri=false");
		assertFalse(body.isToSteal(),     "clearActions 後は toSteal=false");
		assertEquals(-1, body.getMoveTargetId(), "clearActions 後は moveTargetId=-1");
	}

	@Test
	void clearEventResetsCurrentEventAndForceFace() {
		// currentEvent を非null に設定してから clearEvent でクリアされることを確認
		TrackingEventPacket event = new TrackingEventPacket();
		body.setCurrentEvent(event);
		body.setForceFace(5);

		assertSame(event, body.getCurrentEvent(), "clearEvent 前は currentEvent が設定されていること");
		assertEquals(5, body.getForceFace(), "clearEvent 前は forceFace=5");

		YukkuriEventState.clearEvent(body);

		assertNull(body.getCurrentEvent(), "clearEvent 後は currentEvent=null");
		assertEquals(-1, body.getForceFace(), "clearEvent 後は forceFace=-1");
	}

	@Test
	void setMessageIgnoresEmptyString() {
		body.setMessageTicks(0);
		// 空文字列では messageTicks が変化しないこと
		YukkuriEventState.setMessage(body, "");
		assertEquals(0, body.getMessageTicks(), "空文字列では messageTicks は変化しないこと");

		// 対比: 非空文字列では messageTicks が変化すること
		YukkuriEventState.setMessage(body, "hello");
		assertTrue(body.getMessageTicks() > 0, "非空文字列では messageTicks が増加すること");
	}

	@Test
	void setPikoMessageWithCountUpdatesMessageCount() {
		body.setMessageTicks(0);
		int before = body.getMessageTicks();

		YukkuriEventState.setPikoMessage(body, "hi", 3, true);

		assertEquals(3, body.getMessageTicks(), "setPikoMessage(count=3) で messageTicks=3 になること");
		assertTrue(body.getMessageTicks() > before, "setPikoMessage で messageTicks が増加すること");
	}

	@Test
	void processPendingEventsStartsBodyEventBeforeWorldEvent() {
		TrackingEventPacket worldEvent = new TrackingEventPacket();
		TrackingEventPacket bodyEvent = new TrackingEventPacket();
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().add(worldEvent);
		body.getEvents().add(bodyEvent);

		YukkuriEventState.processPendingEvents(body);

		assertSame(bodyEvent, body.getCurrentEvent(),   "body イベントが先に処理されること");
		assertTrue(bodyEvent.started,                  "body イベントが start されること");
		assertFalse(worldEvent.started,                "world イベントはまだ start されないこと");
		assertEquals(0, body.getEvents().size(),       "処理後 body.events が空になること");
		assertFalse(bodyEvent.ended,                   "start されただけで end はされないこと");
	}

	@Test
	void processPendingEventsConsumesSimpleEventsWhenResponseDisabled() {
		TrackingEventPacket bodyEvent = new TrackingEventPacket();
		TrackingEventPacket worldEvent = new TrackingEventPacket();
		bodyEvent.simpleAction = true;
		worldEvent.simpleAction = true;
		body.setSleeping(true);
		body.getEvents().add(bodyEvent);
		org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().add(worldEvent);

		YukkuriEventState.processPendingEvents(body);

		assertNull(body.getCurrentEvent(),              "レスポンス無効時は currentEvent が null のまま");
		assertTrue(bodyEvent.simpleActionCalled,        "body の simple event が処理されること");
		assertTrue(worldEvent.simpleActionCalled,       "world の simple event が処理されること");
		assertEquals(0, body.getEvents().size(),        "処理後 body.events が空になること");
		assertFalse(bodyEvent.started,                  "simple event は start されないこと");
	}

	@Test
	void resolveEventResultActionOverridesDoNothingWithLowPriorityEvent() {
		body.setCurrentEvent(new TrackingEventPacket(EventPacket.EventPriority.LOW));
		body.setEventResult(TickResult.SHIT);

		// fallback=NONE のとき LOW 優先度のイベント結果で上書きされること
		TickResult result = YukkuriEventState.resolveEventResultAction(body, TickResult.NONE);

		assertEquals(TickResult.SHIT, result,         "fallback=NONE のとき eventResult(SHIT) が返ること");
		assertEquals(TickResult.NONE, body.getEventResult(), "消費後は eventResult が NONE にクリアされること");
	}

	@Test
	void resolveEventResultActionKeepsExistingActionForLowPriorityEvent() {
		body.setCurrentEvent(new TrackingEventPacket(EventPacket.EventPriority.LOW));
		body.setEventResult(TickResult.SHIT);

		TickResult result = YukkuriEventState.resolveEventResultAction(body, TickResult.BIRTH);

		assertEquals(TickResult.BIRTH, result);
		assertEquals(TickResult.SHIT, body.getEventResult());
	}

	@Test
	void resolveEventResultActionOverridesExistingActionForHighPriorityEvent() {
		body.setCurrentEvent(new TrackingEventPacket(EventPacket.EventPriority.HIGH));
		body.setEventResult(TickResult.SHIT);

		// HIGH 優先度: fallback(BIRTH)があっても eventResult(SHIT) が優先されること
		TickResult result = YukkuriEventState.resolveEventResultAction(body, TickResult.BIRTH);

		assertEquals(TickResult.SHIT, result,         "HIGH 優先度では fallback(BIRTH) より eventResult(SHIT) が返ること");
		assertEquals(TickResult.NONE, body.getEventResult(), "消費後は eventResult が NONE にクリアされること");
	}

	@Test
	void updateCurrentEventClearsCurrentEventWhenAbortReturned() {
		TrackingEventPacket event = new TrackingEventPacket();
		event.updateState = EventPacket.UpdateState.ABORT;
		body.setCurrentEvent(event);

		YukkuriEventState.updateCurrentEvent(body);

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

		YukkuriEventState.updateCurrentEvent(body);

		assertTrue(event.executed,              "目標到達で execute が呼ばれること");
		assertTrue(event.ended,                 "execute 後に end が呼ばれること");
		assertNull(body.getCurrentEvent(),      "end 後は currentEvent が null になること");
		assertFalse(event.started,              "update→execute の流れで start は呼ばれないこと");
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

		YukkuriEventState.updateCurrentEvent(body);

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
