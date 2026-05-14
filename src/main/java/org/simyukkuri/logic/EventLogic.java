package org.simyukkuri.logic;

import java.util.Iterator;

import org.simyukkuri.Const;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.util.GameWorld;

/***************************************************
 * イベントの処理ロジック
 */
public class EventLogic {

	/**
	 * ワールドイベントへの登録(ショートカットVer)
	 *
	 * @param event   登録するイベント
	 * @param msgBody イベント開始時にしゃべる個体
	 * @param message しゃべる内容
	 */
	public static final void addWorldEvent(EventPacket event, Yukkuri msgBody, String message) {
		addWorldEvent(event, msgBody, message, Const.HOLDMESSAGE);
	}

	/**
	 * ワールドイベントへの登録
	 *
	 * @param event   登録するイベント
	 * @param msgBody イベント開始時にしゃべる個体
	 * @param message しゃべる内容
	 * @param count   しゃべる時間
	 */
	public static final void addWorldEvent(EventPacket event, Yukkuri msgBody, String message, int count) {
		GameWorld.get().getCurrentMap().getEvent().add(event);
		if (msgBody != null) {
			msgBody.setWorldEventSendMessage(message, count);
		}
	}

	/**
	 * 個体イベントへの登録(ショートカットVer)
	 *
	 * @param to      イベントを登録する個体
	 * @param event   登録するイベント
	 * @param msgBody イベント開始時にしゃべる個体
	 * @param message しゃべる内容
	 */
	public static final void addBodyEvent(Yukkuri to, EventPacket event, Yukkuri msgBody, String message) {
		addBodyEvent(to, event, msgBody, message, Const.HOLDMESSAGE);
	}

	/**
	 * 個体イベントへの登録
	 *
	 * @param to      イベントを登録する個体
	 * @param event   登録するイベント
	 * @param msgBody イベント開始時にしゃべる個体
	 * @param message しゃべる内容
	 *                * @param count しゃべる時間
	 */
	public static final void addBodyEvent(Yukkuri to, EventPacket event, Yukkuri msgBody, String message, int count) {
		to.getEventList().add(event);
		if (msgBody != null) {
			msgBody.setBodyEventSendMessage(message, count);
		}
	}

	/** ワールドイベントの有効期間チェック */
	public static final void clockWorldEvent() {
		EventPacket e;
		// リストに登録されているイベントすべてをチェック
		for (Iterator<EventPacket> i = GameWorld.get().getCurrentMap().getEvent().iterator(); i.hasNext();) {
			e = i.next();
			if (e.countDown()) {
				i.remove();
			}
		}
	}

	/**
	 * ワールドイベントの開始チェック
	 *
	 * @param b 参加ゆっくり
	 * @return 始まるイベント
	 */
	public static final EventPacket checkWorldEvent(Yukkuri body) {
		EventPacket eventPacket = null;
		EventPacket e;
		// リストに登録されているイベントすべてをチェック
		for (Iterator<EventPacket> i = GameWorld.get().getCurrentMap().getEvent().iterator(); i.hasNext();) {
			e = i.next();
			// from が設定されていてかつ実体が消えていればイベントを除去
			if (e.getFrom() != -1 && org.simyukkuri.util.BodyRegistry.getBodyInstance(e.getFrom()) == null) {
				i.remove();
				continue;
			}
			// if(e.from == b) continue;
			if (e.simpleEventAction(body)) {
				continue;
			}
			if (e.checkEventResponse(body)) {
				eventPacket = e;
				break;
			}
		}
		return eventPacket;
	}

	/**
	 * 個体イベントの開始チェック
	 * 
	 * @param b 対象ゆっくり
	 * @return 始めるイベント
	 */
	public static final EventPacket checkBodyEvent(Yukkuri body) {
		EventPacket eventPacket = null;
		EventPacket e;

		// リストに登録されているイベントすべてをチェック
		for (Iterator<EventPacket> i = body.getEventList().iterator(); i.hasNext();) {
			e = i.next();
			if (e.simpleEventAction(body)) {
				i.remove();
				continue;
			}
			if (eventPacket == null) {
				if (e.checkEventResponse(body)) {
					eventPacket = e;
					i.remove();
					continue;
				}
			}
			if (e.countDown()) {
				i.remove();
			}
		}
		return eventPacket;
	}

	/**
	 * ワールドイベントのチェック simpleEventAction用
	 * 
	 * @param b ゆっくり
	 */
	public static final void checkSimpleWorldEvent(Yukkuri body) {
		EventPacket e;
		// リストに登録されているイベントすべてをチェック
		for (Iterator<EventPacket> i = GameWorld.get().getCurrentMap().getEvent().iterator(); i.hasNext();) {
			e = i.next();
			Yukkuri from = org.simyukkuri.util.BodyRegistry.getBodyInstance(e.getFrom());
			if (from == body)
				continue;
			if (e.simpleEventAction(body)) {
				continue;
			}
		}
	}

	/**
	 * 固体イベントのチェック simpleEventAction用
	 * 
	 * @param b ゆっくり
	 */
	public static final void checkSimpleBodyEvent(Yukkuri body) {
		EventPacket e;

		for (Iterator<EventPacket> i = body.getEventList().iterator(); i.hasNext();) {
			e = i.next();

			if (e.simpleEventAction(body)) {
				i.remove();
				continue;
			}
		}
	}

	/**
	 * イベントの毎フレーム処理
	 * 
	 * @param b ゆっくり
	 */
	public static final void eventUpdate(Yukkuri body) {
		BodyEventState.updateCurrentEvent(body);
	}
}
