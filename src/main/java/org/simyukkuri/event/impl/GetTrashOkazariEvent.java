package org.simyukkuri.event.impl;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.event.EventPacket.UpdateState;
import org.simyukkuri.util.GameText;

/***************************************************
 * ゴミからおかざり入手イベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 未使用
 * protected Entity target; // ガラクタ
 * protected int count; // 1
 */
public class GetTrashOkazariEvent extends EventPacket {

	private static final long serialVersionUID = -1160350622771927821L;

	/**
	 * コンストラクタ.
	 */
	public GetTrashOkazariEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public GetTrashOkazariEvent() {

	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	/** イベントへの参加可否を判定し、参加可能なら true を返す。 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {

		priority = EventPriority.MIDDLE;
		return true;
	}

	// イベント開始動作
	/** イベントの開始処理を実行する。 */
	@Override
	public void start(Yukkuri body) {
		Entity targetObject = body.takeMappedObj(this.target);
		body.moveToEvent(this, targetObject.getX(), targetObject.getY());
	}

	/** 毎ティック状態を更新する。 */
	@Override
	public UpdateState update(Yukkuri body) {
		Entity targetObject = body.takeMappedObj(this.target);
		if (targetObject.isRemoved())
			return UpdateState.ABORT;
		if (body.hasOkazari())
			return UpdateState.ABORT;
		body.moveToEvent(this, targetObject.getX(), targetObject.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	/** イベント終了判定を行い true で終了する。 */
	@Override
	public boolean execute(Yukkuri body) {
		Entity targetObject = body.takeMappedObj(this.target);
		if (targetObject.isRemoved())
			return true;
		// おかざりランダム入手
		body.giveOkazari(Okazari.getRandomOkazari(body.getAgeState()));
		return true;
	}

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_trashokazari");
	}
}
