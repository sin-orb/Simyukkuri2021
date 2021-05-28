package src.event;

import src.attachment.Ants;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.BaryInUGState;
import src.enums.Happiness;
import src.enums.Intelligence;
import src.system.MessagePool;

/*
	出産時の励ましイベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 2
*/
public class BreedEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * コンストラクタ.
	 */
	public BreedEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	/**
	 *  参加チェック
	 *  ここで各種チェックを行い、イベントへ参加するかを返す
	 *  また、イベント優先度も必要に応じて設定できる
	 */
	@Override
	public boolean checkEventResponse(Body b) {
		// このイベントは固体どうしのイベントだが親子関係の探索が面倒なので
		// ワールドイベントとして登録、受け取り側が自分のつがいか親かを確認する
		boolean ret = false;

		priority = EventPriority.MIDDLE;

		if(b.nearToBirth())return false;
		if(b.isUnBirth()) return false;
		if(getFrom() == b) return false;
		if(!b.canEventResponse()) return false;
		
		// 興奮してるレイパーは参加しない
		if(b.isRaper() && b.isExciting())
		{
			return false;
		}
		
		// うんうん奴隷など格差があれば祝わない
		if( b.getPublicRank() != getFrom().getPublicRank() )
		{
			return false;
		}

		// 埋まっていたら参加しない
		if( b.getBaryState() != BaryInUGState.NONE )
		{
			return false;
		}

		// 自分が馬鹿で親におかざりがなかったら参加しない
		if(!getFrom().hasOkazari() && b.getIntelligence() == Intelligence.FOOL) return false;

		if(getFrom().isParent(b) || getFrom().isPartner(b) || b.isParent(getFrom()) || b.isPartner(getFrom())) return true;
		
		//アリにたかられてたらそれどころじゃないので参加しない
		if (getFrom().getAttachmentSize(Ants.class) != 0 || getFrom().getNumOfAnts() != 0) {
			return false;
		}
		
		return ret;
	}

	/**
	 *  イベント開始動作
	 */
	@Override
	public void start(Body b) {
		b.moveToEvent(this, getFrom().getX(), getFrom().getY());
	}
	
	/**
	 * 毎フレーム処理
	 * UpdateState.ABORTを返すとイベント終了
	 */
	@Override
	public UpdateState update(Body b) {
		if(b.nearToBirth())return UpdateState.FORCE_EXEC;
		// 相手の一定距離まで近づいたら移動終了
		if(Translate.distance(b.getX(), b.getY(), getFrom().getX(), getFrom().getY()) < 20000) {
			b.moveToEvent(this, b.getX(), b.getY());
			return UpdateState.FORCE_EXEC;
		}
		else {
			b.moveToEvent(this, getFrom().getX(), getFrom().getY());
		}
		
		if (getFrom().isDead() || getFrom().isPealed() ||
				getFrom().isBurned() || getFrom().isBurst()||
				getFrom().isRemoved() || getFrom().isCrushed() ||
				getFrom().isPacked() || !getFrom().nearToBirth()) {
				return UpdateState.ABORT;
		}

		// アリにたかられたら参加どころではなくなる
		if (b.getNumOfAnts() != 0 || b.getAttachmentSize(Ants.class) != 0) {
			b.clearEvent();
			return UpdateState.ABORT;
		}
		
		return null;
	}

	/**
	 * イベント目標に到着した際に呼ばれる
	 * trueを返すとイベント終了
	 */
	@Override
	public boolean execute(Body b) {
		if(b.nearToBirth())return true;
		if( b.isNYD() ){
			return false;
		}
		// 相手が出産前なら応援
		if(getFrom().isBirth()) {
			b.setHappiness(Happiness.AVERAGE);
			b.lookTo(getFrom().getX(), getFrom().getY());
			b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RootForPartner), 40, false, false);
			getFrom().addMemories(1);
			b.addMemories(1);
			return false;
		}
		else {
			// 誕生
			if(!getFrom().hasBabyOrStalk()) {
				b.lookTo(getFrom().getX(), getFrom().getY());
				if(getFrom().isHasPants()) {
					b.setHappiness(Happiness.VERY_SAD);
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Surprise), 40, true, true);
					b.addStress(1800);
					b.addMemories(-30);
				}
				else {
					b.setHappiness(Happiness.VERY_HAPPY);
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FirstGreeting), 40, true, false);
					b.addStress(-30);
					b.addMemories(20);
				}
			}
			else {
				b.setHappiness(Happiness.AVERAGE);
				b.lookTo(getFrom().getX(), getFrom().getY());
				return false;
			}
			return true;
		}
	}
	
	@Override
	public String toString() {
		return "おちび迎え";
	}
}