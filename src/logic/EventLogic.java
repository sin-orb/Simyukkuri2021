package src.logic;
import java.util.Iterator;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.draw.Translate;

/***************************************************
	イベントの処理ロジック
 */
public class EventLogic {

	/**ワールドイベントへの登録(ショートカットVer)
	 *
	 * @param event 登録するイベント
	 * @param msgBody イベント開始時にしゃべる個体
	 * @param message しゃべる内容
	 */
	public static final void addWorldEvent(EventPacket event, Body msgBody, String message) {
		addWorldEvent(event, msgBody, message, Const.HOLDMESSAGE);
	}

	/**ワールドイベントへの登録
	 *
	 * @param event 登録するイベント
	 * @param msgBody イベント開始時にしゃべる個体
	 * @param message しゃべる内容
	 * @param count しゃべる時間
	 */
	public static final void addWorldEvent(EventPacket event, Body msgBody, String message, int count) {
		SimYukkuri.world.getCurrentMap().event.add(event);
		if(msgBody != null) {
			msgBody.setWorldEventSendMessage(message, count);
		}
	}

	/**個体イベントへの登録(ショートカットVer)
	 *
	 * @param to イベントを登録する個体
	 * @param event 登録するイベント
	 * @param msgBody イベント開始時にしゃべる個体
	 * @param message しゃべる内容
	 */
	public static final void addBodyEvent(Body to, EventPacket event, Body msgBody, String message) {
		addBodyEvent(to, event, msgBody, message, Const.HOLDMESSAGE);
	}

	/**個体イベントへの登録
	 *
	 * @param to イベントを登録する個体
	 * @param event 登録するイベント
	 * @param msgBody イベント開始時にしゃべる個体
	 * @param message しゃべる内容
	 * * @param count しゃべる時間
	 */
	public static final void addBodyEvent(Body to, EventPacket event, Body msgBody, String message, int count) {
		to.getEventList().add(event);
		if(msgBody != null) {
			msgBody.setBodyEventSendMessage(message, count);
		}
	}

	/** ワールドイベントの有効期間チェック*/
	public static final void clockWorldEvent() {
		EventPacket e;
		//リストに登録されているイベントすべてをチェック
		for (Iterator<EventPacket> i = SimYukkuri.world.getCurrentMap().event.iterator(); i.hasNext();) {
			e = i.next();
			if(e.countDown()) {
				i.remove();
			}
		}
	}

	/** ワールドイベントの開始チェック
	 *
	 * @param b 参加ゆっくり
	 * @return 始まるイベント
	 */
	public static final EventPacket checkWorldEvent(Body b) {
		EventPacket ret = null;
		EventPacket e;
		//リストに登録されているイベントすべてをチェック
		for (Iterator<EventPacket> i = SimYukkuri.world.getCurrentMap().event.iterator(); i.hasNext();) {
			e = i.next();
//			if(e.from == b) continue;
			if(e.simpleEventAction(b)) {
				continue;
			}
			if(e.checkEventResponse(b)) {
				ret = e;
				break;
			}
		}
		return ret;
	}

	/**個体イベントの開始チェック
	 * @param b 対象ゆっくり
	 * @return 始めるイベント
	 */
	public static final EventPacket checkBodyEvent(Body b) {
		EventPacket ret = null;
		EventPacket e;

		//リストに登録されているイベントすべてをチェック
		for (Iterator<EventPacket> i = b.getEventList().iterator(); i.hasNext();) {
			e = i.next();
			if(e.simpleEventAction(b)) {
				i.remove();
				continue;
			}
			if(ret == null) {
				if(e.checkEventResponse(b)) {
					ret = e;
					i.remove();
					continue;
				}
			}
			if(e.countDown()) {
				i.remove();
			}
		}
		return ret;
	}

	/**
	 * ワールドイベントのチェック simpleEventAction用
	 * @param b ゆっくり
	 */
	public static final void checkSimpleWorldEvent(Body b) {
		EventPacket e;
		//リストに登録されているイベントすべてをチェック
		for (Iterator<EventPacket> i = SimYukkuri.world.getCurrentMap().event.iterator(); i.hasNext();) {
			e = i.next();

			if(e.getFrom() == b) continue;
			if(e.simpleEventAction(b)) {
				continue;
			}
		}
	}

	/**
	 *  固体イベントのチェック simpleEventAction用
	 * @param b ゆっくり
	 */
	public static final void checkSimpleBodyEvent(Body b) {
		EventPacket e;

		for (Iterator<EventPacket> i = b.getEventList().iterator(); i.hasNext();) {
			e = i.next();

			if(e.simpleEventAction(b)) {
				i.remove();
				continue;
			}
		}
	}

	/**
	 *  イベントの毎フレーム処理
	 * @param b ゆっくり
	 */
	public static final void eventUpdate(Body b) {
		EventPacket.UpdateState state = null;
		EventPacket ev = b.getCurrentEvent();
		if( ev == null ){
			return;
		}
		// フレーム更新
		state = ev.update(b);
		// ABORTが返されたらイベント中断
		if(EventPacket.UpdateState.ABORT == state) {
			ev.end(b);
			b.clearActions();
			return;
		}

		// 移動先に到達またはupdateがFORCE_EXECを返したらexecute呼び出し
		// 相手の消滅、死亡などのチェックはexecuteで行う
		if (EventPacket.UpdateState.FORCE_EXEC == state ||
				b.getZ() == ev.getToZ() && (b.getStepDist() + 2) >= Translate.distance(b.getX(), b.getY(), ev.getToX(), ev.getToY())) {
			if(ev.execute(b)) {
				ev.end(b);
				b.clearActions();
			}
		}
	}
}


