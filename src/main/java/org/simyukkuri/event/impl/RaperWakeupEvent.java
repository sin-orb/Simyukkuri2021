package org.simyukkuri.event.impl;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.logic.EventLogic;
import org.simyukkuri.util.GameText;

/***************************************************
 * レイパー発情通知イベント
 * protected Yukkuri from; // レイパー
 * protected Yukkuri to; // 未使用
 * protected Entity target; // 未使用
 * protected int count; // 1
 */
public class RaperWakeupEvent extends EventPacket {

	private static final long serialVersionUID = 1123319861445649770L;

	/**
	 * コンストラクタ.
	 */
	public RaperWakeupEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public RaperWakeupEvent() {

	}

	/** ゆっくり以外のエンティティに対する簡易参加チェック。 */
	@Override
	public boolean simpleEventAction(Yukkuri body) {
		// 自分自身はスキップ、またはレイパーが既に消えていればスキップ
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
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
				Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
			return false;
		}

		// 自分もレイパーなら連鎖して発情
		if (body.isRaper()) {
			body.forceToRaperExcite(true);
			return true;
		}

		// 一般人の反応
		// 固体ごとに異なる行動をするため新しいイベントのインスタンスを作成して固体イベントに登録
		EventLogic.addYukkuriEvent(body, new RaperReactionEvent(sourceBody, null, null, 1), null, null);
		return true;
	}

	// 参加チェック
	/** イベントへの参加可否を判定し、参加可能なら true を返す。 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		return false;
	}

	// イベント開始動作
	/** イベントの開始処理を実行する。 */
	@Override
	public void start(Yukkuri body) {
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	/** イベント終了判定を行い true で終了する。 */
	@Override
	public boolean execute(Yukkuri body) {
		return true;
	}

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_raperawakening");
	}
}
