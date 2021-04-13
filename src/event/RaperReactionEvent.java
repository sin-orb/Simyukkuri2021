package src.event;

import java.util.ArrayList;
import java.util.Random;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.Attitude;
import src.enums.BaryInUGState;
import src.enums.CoreAnkoState;
import src.enums.Direction;
import src.enums.EffectType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.item.Barrier;
import src.logic.BodyLogic;
import src.logic.FamilyActionLogic;
import src.system.MessagePool;

/***************************************************
	レイパー襲撃に対する反応イベント
	protected Body from;			// レイパー
	protected Body to;				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class RaperReactionEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Random rnd = new Random();
	private int age = 0;

	// 行動ステート
	enum STATE {
		Escape,		// 逃げる
		Attack,		// 攻撃
	}
	
	public STATE state = null;

	public RaperReactionEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	// 参加チェック
	public boolean checkEventResponse(Body b) {
		// 最低限のチェックはRaperWakeupEventで済んでるんで省略
		priority = EventPriority.HIGH;
		if(b.canflyCheck()) {
			// 飛べる固体
			return false;
		}
		else {
			boolean bIsNearRaper = false;

			// 全ゆっくりに対してチェック
			ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
			for (Body p:bodyList) {
				// 自分同士のチェックは無意味なのでスキップ
				if (p == b) {
					continue;
				}

				// 興奮したレイパーでなければスキップ
				if( !p.isRaper() && !p.isExciting() && !p.isDead()){
					continue;
				}

				// 相手との間に壁があればスキップ
				if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI)) {
					continue;
				}

				bIsNearRaper = true;
			}

			// レイパーが近くにいない
			if( !bIsNearRaper)
			{
				return false;
			}

			// 埋まっていたら参加しない
			if( b.getBaryState() == BaryInUGState.ALL ||  b.getBaryState() == BaryInUGState.NEARLY_ALL )
			{
				return false;
			}

			// うんうん奴隷は逃げる
			if( b.getPublicRank() == PublicRank.UnunSlave )
			{
				state = STATE.Escape;
			}else{
				// 飛べない固体
				if(b.isAdult() && !b.isDamaged() && !b.isSick() && !b.hasBabyOrStalk()
						&& (b.isSmart() && b.getIntelligence() == Intelligence.FOOL) && !b.isDontMove() ) {
					// 健康でバカな善良な大人は迎撃に向かう
					state = STATE.Attack;
				} else {
					// それ以外はひとまず逃げる
					state = STATE.Escape;
				}
			}
		}
		return true;
	}

	// イベント開始動作
	public void start(Body b) {
		if( b.geteCoreAnkoState() != CoreAnkoState.DEFAULT ){
			return;
		}
		if(state == STATE.Attack) {
			// 攻撃は敵に向かう
			moveTarget(b);
			b.setAngry();
		}
		else {
			// 逃げは敵と反対方向へ
			escapeTarget(b);
			b.setHappiness(Happiness.VERY_SAD);
		}
	}
	
	// 毎フレーム処理
	public UpdateState update(Body b) {
		// 相手が消えてしまったら他のレイパーを捜索
		if(getFrom().isRemoved() || getFrom().isDead() || !getFrom().isRaper() ) {
			setFrom(searchNextTarget());
			if(getFrom() == null) return UpdateState.ABORT;
		}
		
		if( rnd.nextInt(500) == 0 ){
			if( !FamilyActionLogic.isRapeTarget() ){
				return UpdateState.ABORT;
			}
		}
		
		if(state == STATE.Attack) {
			// 妊娠したら逃げに変更
			if(b.hasBabyOrStalk()) {
				state = STATE.Escape;
			}
			else {
				// 攻撃は敵に向かう
				b.setForceFace(ImageCode.PUFF.ordinal());
				moveTarget(b);
				if(rnd.nextInt(20) == 0) {
					b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.AttackRapist), Const.HOLDMESSAGE, true, false);
				}
			}
		}
		else {
			// 賢い固体は反撃チェック
			if((age % 10) == 0) {
				if(b.isAdult() && b.getIntelligence() == Intelligence.WISE && b.getPublicRank() != PublicRank.UnunSlave) {
					Body target = null;
					// 何らかの原因で発情が解除されたら制裁
					if(!getFrom().isExciting()) {
						target = getFrom();
					}
					else {
						target = searchAttackTarget();
					}
					if(target != null) {
						int num = 0;
						// 反撃対象が見つかったら同イベント実行中の固体イベントを書き換え
						ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
						for(Body body :bodyList) {
							if(body.getCurrentEvent() instanceof RaperReactionEvent) {
								// うんうん奴隷は不参加
								if( body.getPublicRank() == PublicRank.UnunSlave ) continue;
								// 妊娠、大人以外は不参加.動けない場合も不参加
								if(body.hasBabyOrStalk() || body.isSick() || !body.isAdult() || body.isDontMove()) continue;
								// ドゲスは不参加、善良ほど参加しやすく
								if(body.getAttitude() == Attitude.SUPER_SHITHEAD) num = 1;
								else if(body.getAttitude() == Attitude.SHITHEAD) num = rnd.nextInt(3);
								else if(body.getAttitude() == Attitude.AVERAGE) num = rnd.nextInt(2);
								else num = 0;
								
								if(num == 0) {
									RaperReactionEvent ev = (RaperReactionEvent)body.getCurrentEvent();
									ev.setFrom(target);
									ev.state = STATE.Attack;
								}
							}
						}
						b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.CounterRapist), Const.HOLDMESSAGE, true, false);
					}
				}
			}
			else {
				// 逃げは敵と反対方向へ
				b.setForceFace(ImageCode.CRYING.ordinal());
				if((age % 10) == 0) {
					escapeTarget(b);
				}
				if(rnd.nextInt(20) == 0) {
					b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ScareRapist), Const.HOLDMESSAGE, true, false);
				}
			}
		}
		age++;
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		// 相手が消えてしまったら他のレイパーを捜索
		if(getFrom().isRemoved() || getFrom().isDead()) {
			setFrom(searchNextTarget());
			// レイパー全滅でイベント終了
			if(getFrom() == null) return true;
			return false;
		}

		if(state == STATE.Attack && !b.isDontMove()) {
			// 攻撃
			if(getFrom().getZ() < 5) {
				b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RevengeAttack), Const.HOLDMESSAGE, true, false);
				if(b.getDirection() == Direction.LEFT) {
					SimYukkuri.mypane.terrarium.addEffect(EffectType.HIT, b.getX()-10, b.getY(), 0,
															0, 0, 0, false, 500, 1, true, false, true);
				}
				else {
					SimYukkuri.mypane.terrarium.addEffect(EffectType.HIT, b.getX()+10, b.getY(), 0,
															0, 0, 0, true, 500, 1, true, false, true);
				}
				b.setForceFace(ImageCode.PUFF.ordinal());
				getFrom().strikeByYukkuri(b, this, false);
				b.addStress(-300);
			}
		}
		else {
			// 逃げ
			escapeTarget(b);
			if(rnd.nextInt(20) == 0) {
				b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ScareRapist), Const.HOLDMESSAGE, true, false);
			}
		}
		return false;
	}
	
	// 場にいるレイパーを検索
	private Body searchNextTarget() {
		Body ret = null;
		ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
		for(Body b :bodyList) {
			if(b.isRaper() && b.isExciting() && !b.isDead()) {
				ret = b;
				break;
			}
		}
		return ret;
	}

	// すっきり中のレイパーを検索
	private Body searchAttackTarget() {
		Body ret = null;
		ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
		for(Body b :bodyList) {
			if(b.isExciting() && b.isRaper() && b.isSukkiri()) {
				ret = b;
				break;
			}
		}
		return ret;
	}

	// 敵に向かって移動
	private void moveTarget(Body b) {
		int colX = BodyLogic.calcCollisionX(b, getFrom());
		b.moveToEvent(this, getFrom().getX() + colX, getFrom().getY());
	}
	
	// 敵から逃げるように移動
	private void escapeTarget(Body b) {
		int mapX = Translate.mapW;
		int mapY = Translate.mapH;
		
		int vx = b.getX() - getFrom().getX();
		if(b.getX() < 2) {
			vx = mapX;
		} else if(b.getX() > mapX - 2) {
			vx = 0;
		} else {
			if(vx > 0) vx = mapX;
			else vx = 0;
		}
		int vy = b.getY() - getFrom().getY();
		if(b.getY() < 2) {
			vy = mapY;
		} else if(b.getY() > mapY - 2) {
			vy = 0;
		} else {
			if(vy > 0) vy = mapY;
			else vy = 0;
		}
		b.moveToEvent(this, vx, vy);
	}
}