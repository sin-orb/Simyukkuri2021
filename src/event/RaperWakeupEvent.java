package src.event;
import src.util.GameText;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.item.Barrier;
import src.logic.EventLogic;
import src.system.ResourceUtil;

/***************************************************
 * レイパー発情通知イベント
 * protected Body from; // レイパー
 * protected Body to; // 未使用
 * protected Obj target; // 未使用
 * protected int count; // 1
 */
public class RaperWakeupEvent extends EventPacket {

	private static final long serialVersionUID = 1123319861445649770L;

	/**
	 * コンストラクタ.
	 */
	public RaperWakeupEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	public RaperWakeupEvent() {

	}

	@Override
	public boolean simpleEventAction(Body body) {
		// 自分自身はスキップ、またはレイパーが既に消えていればスキップ
		Body sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null || body == sourceBody)
			return false;
		// 死体、睡眠、皮なし、目無しはスキップ
		if (!body.canEventResponse())
			return true;

		// 非ゆっくり症、針刺し状態のはスキップ
		if (body.isNYD() || body.isNeedled()) {
			return false;
		}

		// 相手との間に壁があればスキップ
		if (Barrier.acrossBarrier(body.getX(), body.getY(), sourceBody.getX(), sourceBody.getY(),
				Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
			return false;
		}

		// 自分もレイパーなら連鎖して発情
		if (body.isRaper()) {
			body.forceToRaperExcite(true);
			return true;
		}

		// 一般人の反応
		// 固体ごとに異なる行動をするため新しいイベントのインスタンスを作成して固体イベントに登録
		EventLogic.addBodyEvent(body, new RaperReactionEvent(sourceBody, null, null, 1), null, null);
		return true;
	}

	// 参加チェック
	@Override
	public boolean checkEventResponse(Body body) {
		return false;
	}

	// イベント開始動作
	@Override
	public void start(Body body) {
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body body) {
		return true;
	}

	@Override
	public String toString() {
		return GameText.read("event_raperawakening");
	}
}
