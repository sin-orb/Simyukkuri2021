package src.event;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.base.Okazari;
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
	public boolean checkEventResponse(Body b) {

		priority = EventPriority.MIDDLE;
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
		Obj target = b.takeMappedObj(this.target);
		b.moveToEvent(this, target.getX(), target.getY());
	}

	@Override
	public UpdateState update(Body b) {
		Obj target = b.takeMappedObj(this.target);
		if (target.isRemoved())
			return UpdateState.ABORT;
		if (b.hasOkazari())
			return UpdateState.ABORT;
		b.moveToEvent(this, target.getX(), target.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body b) {
		Obj target = b.takeMappedObj(this.target);
		if (target.isRemoved())
			return true;
		// おかざりランダム入手
		b.giveOkazari(Okazari.getRandomOkazari(b.getBodyAgeState()));
		return true;
	}

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_trashokazari");
	}
}