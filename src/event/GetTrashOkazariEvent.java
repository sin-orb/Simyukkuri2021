package src.event;
import src.util.GameText;

import src.base.Body;
import src.event.EventPacket;
import src.base.Obj;
import src.entity.world.bodylinked.Okazari;
import src.system.ResourceUtil;

/***************************************************
 * ゴミからおかざり入手イベント
 * protected Body from; // イベントを発した個体
 * protected Body to; // 未使用
 * protected Obj target; // ガラクタ
 * protected int count; // 1
 */
public class GetTrashOkazariEvent extends EventPacket {

	private static final long serialVersionUID = -1160350622771927821L;

	/**
	 * コンストラクタ.
	 */
	public GetTrashOkazariEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	public GetTrashOkazariEvent() {

	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Body body) {

		priority = EventPriority.MIDDLE;
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Body body) {
		Obj targetObject = body.takeMappedObj(this.target);
		body.moveToEvent(this, targetObject.getX(), targetObject.getY());
	}

	@Override
	public UpdateState update(Body body) {
		Obj targetObject = body.takeMappedObj(this.target);
		if (targetObject.isRemoved())
			return UpdateState.ABORT;
		if (body.hasOkazari())
			return UpdateState.ABORT;
		body.moveToEvent(this, targetObject.getX(), targetObject.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body body) {
		Obj targetObject = body.takeMappedObj(this.target);
		if (targetObject.isRemoved())
			return true;
		// おかざりランダム入手
		body.giveOkazari(Okazari.getRandomOkazari(body.getBodyAgeState()));
		return true;
	}

	@Override
	public String toString() {
		return GameText.read("event_trashokazari");
	}
}
