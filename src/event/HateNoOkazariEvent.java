package src.event;

import java.util.ArrayList;
import java.util.Random;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.Direction;
import src.enums.EffectType;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.item.Barrier;
import src.item.Toilet;
import src.logic.BodyLogic;
import src.system.MessagePool;

/***************************************************
	おかざりのないゆっくりへの攻撃イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 攻撃対象
	protected Obj target;			// 未使用
	protected int count;			// 10
*/
public class HateNoOkazariEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();

	public HateNoOkazariEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	public boolean checkEventResponse(Body b) {
		boolean ret = false;

		priority = EventPriority.MIDDLE;
		// うんうん奴隷は参加しない
		if( b.getPublicRank() == PublicRank.UnunSlave ) return false;		
		// 善良は参加しない
		if(b.isSmart()) return false;
		// 足りないゆは参加しない
		if(b.isIdiot()) return false;
		
		// 自分が賢い場合はおかざりがなくても家族を認識して参加しない
		if(b.getIntelligence() == Intelligence.WISE) {
			if(to.isParent(b) || to.isPartner(b) || b.isParent(to) || b.isPartner(to)) return false;
		}
		// 死体、睡眠、皮なし、目無し、非ゆっくり症は参加しない
		if( !b.canEventResponse()){
			return false;
		}
		
		// 自分が通常種で相手が捕食種の場合は参加しない
		if( !b.isPredatorType() && to.isPredatorType()){
			return false;
		}
		
		// 自分がお飾りあり、健康で動ける状況にあるなら参加チェック
		if(b.hasOkazari() && !b.isDamaged() && !b.isDontMove()) {
			// ドゲスは参加
			if(b.isVeryRude()) ret = true;
			else {
				// ゲス、普通は相手が瀕死じゃなければ参加
				if(!to.isDamaged()) {
					if(b.isRude() || rnd.nextBoolean()) ret = true;
				}
			}
		}
		// 相手との間に壁があればスキップ
		if (Barrier.acrossBarrier(b.getX(), b.getY(), to.getX(), to.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI)) {
			return false;
		}
		
		if(ret) {
			if(getFrom() != b) {
				b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.HateYukkuri), Const.HOLDMESSAGE, true, false);
			}
		}
		return ret;
	}

	// イベント開始動作
	public void start(Body b) {
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
	}

	// 毎フレーム処理
	// trueを返すとイベント終了
	public UpdateState update(Body b) {
		// 相手が消えてしまったらイベント中断
		if(to.isRemoved()) return UpdateState.ABORT;
		// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
		if(Translate.distance(b.getX(), b.getY(), to.getX(), to.getY()) < 2500) {
			to.stay();
		}
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		// 相手が残っていたら攻撃
		if(!to.isDead() && !to.isRemoved() && to.getZ() < 5) {
			// うんうん奴隷ではない場合
			if( to.getPublicRank() != PublicRank.UnunSlave ){
				boolean bIsInToiletForSlave = false;
				ArrayList<Toilet> toiletList = SimYukkuri.world.currentMap.toilet;
				for (Toilet t: toiletList) {
					// うんうん奴隷用トイレがあるか
					if( t.isForSlave() ){
						bIsInToiletForSlave = true;
						break;
					}
				}
				// うんうん奴隷用トイレがある場合
				if( bIsInToiletForSlave ){
					to.setPublicRank( PublicRank.UnunSlave ) ; // うんうんどれい認定
					Body p = b.getPartner();
					if (p != null) {
						// うんうんどれいになるようなくずとは りこんっ！だよ！！
						b.setPartner(null);
						p.setPartner(null);
					}
					b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.EngageUnunSlave), Const.HOLDMESSAGE, true, false);
				}
				else{
					b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.HateYukkuri), Const.HOLDMESSAGE, true, false);					
				}
			}
			else{
				b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.HateYukkuri), Const.HOLDMESSAGE, true, false);
			}
			
			if(b.getDirection() == Direction.LEFT) {
				SimYukkuri.mypane.terrarium.addEffect(EffectType.HIT, b.getX()-10, b.getY(), 0,
														0, 0, 0, false, 500, 1, true, false, true);
			}
			else {
				SimYukkuri.mypane.terrarium.addEffect(EffectType.HIT,b.getX()+10, b.getY(),0,0,0,0,true, 500,1,true,false,true);
			}
			
			// 瀕死の場合は攻撃されないで見逃される
			if( !to.isDamagedHeavily() ){
				to.strikeByYukkuri(b, this, true);
			}
			b.setForceFace(ImageCode.PUFF.ordinal());
			b.addStress(-800);
		}
		return true;
	}
	@Override
	public String toString() {
		return "無おかざりいじめ";
	}
}