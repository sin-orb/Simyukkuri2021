package src.event;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.item.Barrier;
import src.logic.EventLogic;

/***************************************************
	レイパー発情通知イベント
	protected Body from;			// レイパー
	protected Body to;				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class RaperWakeupEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * コンストラクタ.
	 */
	public RaperWakeupEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	@Override
	public boolean simpleEventAction(Body b) {
		// 自分自身はスキップ
		if(b == getFrom()) return false;
		// 死体、睡眠、皮なし、目無しはスキップ
		if(!b.canEventResponse()) return true;

		//非ゆっくり症、針刺し状態のはスキップ
		if( b.isNYD() ||b.isNeedled())
		{
			return false;
		}

		// 相手との間に壁があればスキップ
		if (Barrier.acrossBarrier(b.getX(), b.getY(), getFrom().getX(), getFrom().getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI)) {
			return false;
		}
		
		// 自分もレイパーなら連鎖して発情
		if(b.isRaper()) {
			b.forceToRaperExcite(true);
			return true;
		}
		
		// 一般人の反応
		// 固体ごとに異なる行動をするため新しいイベントのインスタンスを作成して固体イベントに登録
		EventLogic.addBodyEvent(b, new RaperReactionEvent(getFrom(), null, null, 1), null, null);
		return true;
	}

	// 参加チェック
	@Override
	public boolean checkEventResponse(Body b) {
		return false;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
	}
	
	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body b) {
		return true;
	}
	
	@Override
	public String toString() {
		return "れいぱーが覚醒";
	}
}