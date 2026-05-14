package org.simyukkuri.logic;

import java.awt.Color;

import org.simyukkuri.Const;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Event;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.WindowType;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * {@link Yukkuri} が持つイベント状態とメッセージ状態の更新を扱う補助クラス。
 * <p>
 * Phase 2 では、public API を {@link Yukkuri} 側へ残したまま、message 表示と
 * action reset の副作用をこのクラスへ委譲する。
 * </p>
 */
public final class YukkuriEventState {
	private YukkuriEventState() {
	}

	/**
	 * 通常メッセージを表示する。
	 *
	 * @param body    更新対象のゆっくり
	 * @param message 表示文字列
	 */
	public static void setMessage(Yukkuri body, String message) {
		if (message == null || message.length() == 0) {
			return;
		}
		int size = message.length();
		if (20 < size) {
			setMessage(body, message, WindowType.NORMAL, size, false, false, false);
			return;
		}
		setMessage(body, message, WindowType.NORMAL, Const.HOLDMESSAGE, false, false, false);
	}

	/**
	 * ピコピコ付きメッセージを表示する。
	 *
	 * @param body      更新対象のゆっくり
	 * @param message   表示文字列
	 * @param interrupt 割り込み可否
	 */
	public static void setPikoMessage(Yukkuri body, String message, boolean interrupt) {
		setMessage(body, message, WindowType.NORMAL, Const.HOLDMESSAGE, interrupt, true, false);
	}

	/**
	 * ピコピコ付きメッセージを時間指定で表示する。
	 *
	 * @param body      更新対象のゆっくり
	 * @param message   表示文字列
	 * @param count     表示時間
	 * @param interrupt 割り込み可否
	 */
	public static void setPikoMessage(Yukkuri body, String message, int count, boolean interrupt) {
		setMessage(body, message, WindowType.NORMAL, count, interrupt, true, false);
	}

	/**
	 * 時間指定の通常メッセージを表示する。
	 *
	 * @param body    更新対象のゆっくり
	 * @param message 表示文字列
	 * @param count   表示時間
	 */
	public static void setMessage(Yukkuri body, String message, int count) {
		setMessage(body, message, WindowType.NORMAL, count, false, false, false);
	}

	/**
	 * 割り込み可否付きの通常メッセージを表示する。
	 *
	 * @param body      更新対象のゆっくり
	 * @param message   表示文字列
	 * @param interrupt 割り込み可否
	 */
	public static void setMessage(Yukkuri body, String message, boolean interrupt) {
		setMessage(body, message, WindowType.NORMAL, Const.HOLDMESSAGE, interrupt, false, false);
	}

	/**
	 * 時間とピコピコ指定付きの通常メッセージを表示する。
	 *
	 * @param body      更新対象のゆっくり
	 * @param message   表示文字列
	 * @param count     表示時間
	 * @param interrupt 割り込み可否
	 * @param piko      ピコピコ有無
	 */
	public static void setMessage(Yukkuri body, String message, int count, boolean interrupt, boolean piko) {
		setMessage(body, message, WindowType.NORMAL, count, interrupt, piko, false);
	}

	/**
	 * ワールドイベント送信用メッセージを表示する。
	 *
	 * @param body    更新対象のゆっくり
	 * @param message 表示文字列
	 * @param count   表示時間
	 */
	public static void setWorldEventSendMessage(Yukkuri body, String message, int count) {
		setMessage(body, message, WindowType.WORLD_SEND, count, true, false, false);
	}

	/**
	 * ワールドイベント応答メッセージを表示する。
	 *
	 * @param body      更新対象のゆっくり
	 * @param message   表示文字列
	 * @param count     表示時間
	 * @param interrupt 割り込み可否
	 * @param piko      ピコピコ有無
	 */
	public static void setWorldEventResMessage(Yukkuri body, String message, int count, boolean interrupt,
			boolean piko) {
		setMessage(body, message, WindowType.WORLD_RES, count, interrupt, piko, false);
	}

	/**
	 * 個体イベント送信用メッセージを表示する。
	 *
	 * @param body    更新対象のゆっくり
	 * @param message 表示文字列
	 * @param count   表示時間
	 */
	public static void setEventSendMessage(Yukkuri body, String message, int count) {
		setMessage(body, message, WindowType.BODY_SEND, count, true, false, false);
	}

	/**
	 * 個体イベント応答メッセージを表示する。
	 *
	 * @param body      更新対象のゆっくり
	 * @param message   表示文字列
	 * @param count     表示時間
	 * @param interrupt 割り込み可否
	 * @param piko      ピコピコ有無
	 */
	public static void setEventResMessage(Yukkuri body, String message, int count, boolean interrupt,
			boolean piko) {
		setMessage(body, message, WindowType.BODY_RES, count, interrupt, piko, false);
	}

	/**
	 * 非ゆっくり症向けメッセージを表示する。
	 *
	 * @param body    更新対象のゆっくり
	 * @param message 表示文字列
	 * @param piko    ピコピコ有無
	 */
	public static void setNydMessage(Yukkuri body, String message, boolean piko) {
		setMessage(body, message, WindowType.NORMAL, Const.HOLDMESSAGE, true, piko, true);
	}

	/**
	 * メッセージ本体の表示状態を更新する。
	 *
	 * @param body      更新対象のゆっくり
	 * @param message   表示文字列
	 * @param type      ウィンドウ種別
	 * @param count     表示時間
	 * @param interrupt 割り込み可否
	 * @param piko      ピコピコ有無
	 * @param nyd       非ゆっくり症メッセージかどうか
	 */
	public static void setMessage(Yukkuri body, String message, WindowType type, int count, boolean interrupt,
			boolean piko, boolean nyd) {
		if (!nyd && (body.isNYD() || body.isSleeping())) {
			return;
		}
		if (body.isSilent() || body.isUnBirth()) {
			body.setMessageTicks(0);
			body.setMessageBuffer(null);
			return;
		}
		if (!interrupt && GameRandom.nextInt(body.getSpeechDiscipline() + 1) != 0
				&& body.getIntelligence() != Intelligence.WISE) {
			message = GameMessages.getMessage(body, MessagePool.Action.BeingQuiet);
			return;
		}
		if (!body.isCanTalk()) {
			body.setMessageTicks(0);
			body.setMessageBuffer(null);
			return;
		}
		if (message == null || message.length() == 0) {
			body.setMessageTicks(0);
			body.setMessageBuffer(null);
			return;
		}
		if (interrupt || body.getMessageTicks() == 0) {
			body.setMessageTicks(count);
			body.setMessageBuffer(message);
			resetMessageActions(body, piko);
			body.setOrigMessageLineColor(Const.WINDOW_COLOR[type.ordinal()][0]);
			body.setOrigMessageBoxColor(Const.WINDOW_COLOR[type.ordinal()][1]);
			body.setOrigMessageTextColor(Const.WINDOW_COLOR[type.ordinal()][2]);
			body.setMessageWindowStroke(Const.WINDOW_STROKE[type.ordinal()]);
			applyMessageWindowStyle(body);
		}
	}

	/**
	 * ねぎぃメッセージを表示する。
	 *
	 * @param body    更新対象のゆっくり
	 * @param message 表示文字列
	 * @param piko    ピコピコ有無
	 */
	public static void setNegiMessage(Yukkuri body, String message, boolean piko) {
		setNegiMessage(body, message, Const.HOLDMESSAGE, piko);
	}

	/**
	 * ねぎぃメッセージを時間指定で表示する。
	 *
	 * @param body    更新対象のゆっくり
	 * @param message 表示文字列
	 * @param count   表示時間
	 * @param piko    ピコピコ有無
	 */
	public static void setNegiMessage(Yukkuri body, String message, int count, boolean piko) {
		if (!body.isCanTalk() || body.isUnBirth()) {
			body.setMessageTicks(0);
			body.setMessageBuffer(null);
			return;
		}
		body.setMessageTicks(count);
		body.setMessageBuffer(message);
		body.setPikopiko(piko);
		if (!body.isFixBack()) {
			body.setFurifuri(false);
		}
		body.setStrike(false);
		body.setEating(false);
		body.setEatingShit(false);
		body.setPeropero(false);
		body.setSukkiri(false);
		body.setNobinobi(false);
		body.setBeVain(false);
		body.setYunnyaa(false);
		body.setInOutTakeoutItem(false);
		body.setOrigMessageLineColor(Const.NEGI_WINDOW_COLOR[0]);
		body.setOrigMessageBoxColor(Const.NEGI_WINDOW_COLOR[1]);
		body.setOrigMessageTextColor(Const.NEGI_WINDOW_COLOR[2]);
		body.setMessageWindowStroke(Const.WINDOW_STROKE[0]);
		body.setMessageTextSize(12);
	}

	/**
	 * 行動とイベントをまとめて解除する。
	 *
	 * @param body 更新対象のゆっくり
	 */
	public static void clearActions(Yukkuri body) {
		clearActionsForEvent(body);
		if (body.getCurrentEvent() != null) {
			body.getCurrentEvent().end(body);
		}
		body.setCurrentEvent(null);
		body.setMoveTargetId(-1);
		body.setForceFace(-1);
		body.setShadowVisible(true);
		body.setTargetOffsetX(0);
		body.setTargetOffsetY(0);
		body.setTargetBind(false);
		body.stopPlaying();
		body.setOfsXY(0, 0);
	}

	/**
	 * イベント本体だけを解除する。
	 *
	 * @param body 更新対象のゆっくり
	 */
	public static void clearEvent(Yukkuri body) {
		if (body.getCurrentEvent() != null) {
			body.getCurrentEvent().end(body);
		}
		body.setCurrentEvent(null);
		body.setForceFace(-1);
		body.setShadowVisible(true);
		body.stopPlaying();
	}

	/**
	 * イベント起点の移動フラグだけを解除する。
	 *
	 * @param body 更新対象のゆっくり
	 */
	public static void clearActionsForEvent(Yukkuri body) {
		body.setToSukkiri(false);
		body.setToBed(false);
		body.setToFood(false);
		body.setToShit(false);
		body.setToYukkuri(false);
		body.setToSteal(false);
	}

	/**
	 * 保留中イベントの選別と開始を行う。
	 * <p>
	 * 応答可能なときは個体イベントを優先し、未選択ならワールドイベントを見る。
	 * 応答不可のときは simple event だけを消化する。
	 * </p>
	 *
	 * @param body 更新対象のゆっくり
	 */
	public static void processPendingEvents(Yukkuri body) {
		if (body.canEventResponse()) {
			startSelectedEvent(body, selectNextEvent(body));
			return;
		}
		EventLogic.checkSimpleYukkuriEvent(body);
		EventLogic.checkSimpleWorldEvent(body);
	}

	/**
	 * 実行中イベントが設定した結果アクションを、通常処理の戻り値へ反映する。
	 *
	 * @param body           更新対象のゆっくり
	 * @param fallbackAction 通常処理が返そうとしているイベント
	 * @return 反映後の戻り値
	 */
	public static Event resolveEventResultAction(Yukkuri body, Event fallbackAction) {
		EventPacket currentEvent = body.getCurrentEvent();
		if (currentEvent == null) {
			return fallbackAction;
		}
		if (fallbackAction != Event.DONOTHING && currentEvent.getPriority() == EventPacket.EventPriority.LOW) {
			return fallbackAction;
		}
		Event eventResult = body.getEventResult();
		body.setEventResult(Event.DONOTHING);
		return eventResult;
	}

	/**
	 * 実行中イベントの update / execute / end 遷移を1フレーム分進める。
	 *
	 * @param body 更新対象のゆっくり
	 */
	public static void updateCurrentEvent(Yukkuri body) {
		EventPacket currentEvent = body.getCurrentEvent();
		if (currentEvent == null) {
			return;
		}

		EventPacket.UpdateState state = currentEvent.update(body);
		if (EventPacket.UpdateState.ABORT == state) {
			currentEvent.end(body);
			body.clearActions();
			return;
		}

		if (!shouldExecuteCurrentEvent(body, currentEvent, state)) {
			return;
		}
		if (currentEvent.execute(body)) {
			currentEvent.end(body);
			body.clearActions();
		}
	}

	private static EventPacket selectNextEvent(Yukkuri body) {
		EventPacket nextEvent = EventLogic.checkYukkuriEvent(body);
		if (nextEvent != null) {
			return nextEvent;
		}
		return EventLogic.checkWorldEvent(body);
	}

	private static void startSelectedEvent(Yukkuri body, EventPacket nextEvent) {
		body.setCurrentEvent(nextEvent);
		if (nextEvent != null) {
			nextEvent.start(body);
		}
	}

	private static boolean shouldExecuteCurrentEvent(Yukkuri body, EventPacket currentEvent,
			EventPacket.UpdateState state) {
		if (EventPacket.UpdateState.FORCE_EXEC == state) {
			return true;
		}
		return body.getZ() == currentEvent.getToZ()
				&& (body.getStepDist() + 2) >= Translate.distance(body.getX(), body.getY(), currentEvent.getToX(),
						currentEvent.getToY());
	}

	private static void resetMessageActions(Yukkuri body, boolean piko) {
		if (!body.isFixBack()) {
			body.setFurifuri(false);
		}
		body.setSukkiri(false);
		body.setBeVain(false);
		body.setNobinobi(false);
		body.setYunnyaa(false);
		body.setPikopiko(piko);
	}

	private static void applyMessageWindowStyle(Yukkuri body) {
		switch (body.getBurialState()) {
			case NONE:
				body.setMessageTextSize(12);
				break;
			case HALF:
				body.setMessageTextSize(12);
				body.setFurifuri(false);
				break;
			case NEARLY_ALL:
				body.setMessageTextSize(8);
				body.setPikopiko(false);
				body.setFurifuri(false);
				body.setBeVain(false);
				body.setNobinobi(false);
				body.setPeropero(false);
				body.setYunnyaa(false);
				body.setBegging(false);
				body.setOrigMessageBoxColor(new Color(217, 128, 0, 200));
				break;
			case ALL:
				body.setMessageTextSize(7);
				body.setPikopiko(false);
				body.setFurifuri(false);
				body.setBeVain(false);
				body.setNobinobi(false);
				body.setPeropero(false);
				body.setYunnyaa(false);
				body.setBegging(false);
				body.setOrigMessageBoxColor(new Color(128, 54, 0, 200));
				break;
			default:
				body.setMessageTextSize(12);
				break;
		}
	}
}
