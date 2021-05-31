package src.event;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.item.Barrier;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;

/***************************************************
	かびたゆっくりへの反応イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 攻撃対象
	protected Obj target;			// 未使用
	protected int count;			// 10
*/
public class AvoidMoldEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * コンストラクタ.
	 */
	public AvoidMoldEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	/**
	 *  参加チェック
	 *  ここで各種チェックを行い、イベントへ参加するかを返す
	 *  また、イベント優先度も必要に応じて設定できる
	 */
	@Override
	public boolean checkEventResponse(Body b) {
		priority = EventPriority.MIDDLE;
		// うんうん奴隷は参加しない
		if( b.getPublicRank() == PublicRank.UnunSlave ) return false;
		// 足りないゆは参加しない
		if(b.isIdiot()) return false;
		// 非ゆっくり症は参加しない
		if(!b.canEventResponse() )return false;
		// 相手との間に壁があればスキップ
		if (Barrier.acrossBarrier(b.getX(), b.getY(), to.getX(), to.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI)) {
			return false;
		}
		return true;
	}

	/**
	 *  イベント開始動作
	 */
	@Override
	public void start(Body b) {
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
	}

	/**
	 * 毎フレーム処理
	 * UpdateState.ABORTを返すとイベント終了
	 */
	@Override
	public UpdateState update(Body b) {
		// 相手が消えてしまったらイベント中断
		if(to.isDead() || to.isRemoved()) return UpdateState.ABORT;
		to.stay();
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
		return null;
	}

	/**
	 * イベント目標に到着した際に呼ばれる
	 * trueを返すとイベント終了
	 */
	@Override
	public boolean execute(Body b) {

		//ドゲスの場合、楽しんで制裁
		if(getFrom().isVeryRude()){
			getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.HateMoldyYukkuri), Const.HOLDMESSAGE, true, false);
			to.strikeByYukkuri(getFrom(), this, false);
			getFrom().setForceFace(ImageCode.PUFF.ordinal());
			if(getFrom().getIntelligence() == Intelligence.FOOL) getFrom().addSickPeriod(100);
			return true;
		}

		//自分が成体のばあい。ちゃんと制裁する
		if (getFrom().isAdult()) {
			// 自分が成体で相手が家族なら嘆く
			if (!b.isTalking()) {
				//共通処理
				b.setHappiness(Happiness.VERY_SAD);
				b.addMemories(-1);
				b.addStress(80);
				//相手が子供か、又は番
				if (getFrom().isParent(to) || getFrom().isPartner(to)) {
					switch(getFrom().getIntelligence()){
						case FOOL:
							if(SimYukkuri.RND.nextInt(5) == 0){
								getFrom().doPeropero(to);
								return true;
							}
							else{
								saySadMessage(getFrom() , to);
								return false;
							}
						case WISE:
							if(SimYukkuri.RND.nextInt(5) == 0){
								sayApologyMessage(getFrom(),to);
								to.strikeByYukkuri(getFrom(), this, false);
							}
							return false;
						default:
							if(SimYukkuri.RND.nextInt(5) == 0){
								sayApologyMessage(getFrom(),to);
								to.strikeByYukkuri(getFrom(), this, false);
								return true;
							}
							else{
								saySadMessage(getFrom() , to);
								return false;
							}
					}
				}
				//相手が親化、又は姉妹の場合
				else if(getFrom().isFamily(to)){
					switch(getFrom().getIntelligence()){
						case FOOL:
							if(SimYukkuri.RND.nextInt(5) == 0){
								getFrom().doPeropero(to);
							}
							else{
								saySadMessage(getFrom() , to);
							}
							return true;
						case WISE:
							if(SimYukkuri.RND.nextInt(5) == 0){
								sayApologyMessage(getFrom(),to);
								to.runAway(to.getX(),to.getY());
							}
							return true;
						default:
							if(SimYukkuri.RND.nextInt(5) == 0){
								sayApologyMessage(getFrom(),to);
								to.runAway(to.getX(),to.getY());
								return true;
							}
							else{
								saySadMessage(getFrom() , to);
								return false;
							}
					}
				}
				//家族でない場合
				else {
					getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.HateMoldyYukkuri), Const.HOLDMESSAGE, true, false);
					to.strikeByYukkuri(getFrom(), this, false);
					getFrom().setForceFace(ImageCode.PUFF.ordinal());
					if(getFrom().getIntelligence() == Intelligence.FOOL)getFrom().forceSetSick();
					return true;
				}
			}
		}

		//子ゆ、赤ゆの場合。嘆くのみ
		else{
			//共通処理
			b.setHappiness(Happiness.VERY_SAD);
			b.addStress(80);
			b.addMemories(-1);
			//かびてるのが親
			if(getFrom().isChild(to)){
				b.addStress(70);
				switch(getFrom().getIntelligence()){
					case FOOL:
						if(SimYukkuri.RND.nextInt(5)==0){
							getFrom().doPeropero(to);
							return false;
						}
						else{
							saySadMessage(getFrom(),to);
							return false;
						}
				case WISE:
					if(SimYukkuri.RND.nextInt(5) == 0){
						sayApologyMessage(getFrom(),to);
						to.runAway(to.getX(),to.getY());
						return true;
					}
					else{
						saySadMessage(getFrom(),to);
						return false;
					}
				default:
					if(SimYukkuri.RND.nextInt(25) == 0){
						sayApologyMessage(getFrom(),to);
						to.runAway(to.getX(),to.getY());
						return true;
					}
					else if(SimYukkuri.RND.nextInt(5)==0){
						getFrom().doPeropero(to);
						return false;
					}
					else{
						saySadMessage(getFrom(),to);
						return false;
					}
				}
			}
			//かびてるのが家族
			else if(to.isFamily(getFrom())){
				switch(getFrom().getIntelligence()){
					case FOOL:
						if(SimYukkuri.RND.nextInt(5)==0){
							getFrom().doPeropero(to);
							return false;
						}
						else{
							saySadMessage(getFrom(),to);
							return false;
						}
					case WISE:
						if(SimYukkuri.RND.nextInt(5) == 0){
							sayApologyMessage(getFrom(),to);
							to.runAway(to.getX(),to.getY());
							return true;
						}
						else{
							saySadMessage(getFrom(),to);
							return false;
						}
					default:
						if(SimYukkuri.RND.nextInt(10) == 0){
							sayApologyMessage(getFrom(),to);
							to.runAway(to.getX(),to.getY());
							return true;
						}
						else{
							saySadMessage(getFrom(),to);
							return false;
						}
				}
			}
			//家族でない場合
			else {
				getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.HateMoldyYukkuri), Const.HOLDMESSAGE, true, false);
				getFrom().runAway(to.getX(),to.getY());
				getFrom().setForceFace(ImageCode.PUFF.ordinal());
				if(getFrom().getIntelligence() == Intelligence.FOOL)getFrom().forceSetSick();
				return true;
			}
		}
		return true;
	}
	/**
	 * 悲しみのメッセージを言う
	 * @param From イベント発生元
	 * @param To イベント対象
	 */
	public void saySadMessage(Body From , Body To){
		String message = null;
		if(From.isParent(To)){
			message = MessagePool.getMessage(getFrom(), MessagePool.Action.SadnessForMoldyChild);
		}
		else if(From.isPartner(To)){
			message = MessagePool.getMessage(getFrom(), MessagePool.Action.SadnessForMoldyPartner);
		}
		else if(To.isParent(From)){
			if(To.isFather(From))message = MessagePool.getMessage(getFrom(), MessagePool.Action.SadnessForMoldyFather);
			else message = MessagePool.getMessage(getFrom(), MessagePool.Action.SadnessForMoldyMother);
		}
		else if(To.isSister(From)){
			if(To.getAge()>=From.getAge())message = MessagePool.getMessage(getFrom(), MessagePool.Action.SadnessForEldersister);
			else message = MessagePool.getMessage(getFrom(), MessagePool.Action.SadnessForMoldySister);
		}
		From.setBodyEventResMessage(message, Const.HOLDMESSAGE, true, SimYukkuri.RND.nextBoolean());
	}
	/**
	 * 謝罪する.
	 * @param From イベント発生元
	 * @param To イベント対象
	 */
	public void sayApologyMessage(Body From , Body To){
		String message = null;
		if(From.isParent(To)){
			message = MessagePool.getMessage(getFrom(), MessagePool.Action.ApologyToChild);
		}
		else if(To.isFamily(From)){
			message = MessagePool.getMessage(getFrom(), MessagePool.Action.ApologyToFamily);
		}
		From.setBodyEventResMessage(message, Const.HOLDMESSAGE, true, SimYukkuri.RND.nextBoolean());
	}
	
	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_mold");
	}
}