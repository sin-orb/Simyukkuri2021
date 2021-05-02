package src.event;

import java.util.Random;

import src.Const;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.item.Sui;
import src.logic.EventLogic;
import src.system.MessagePool;

/***************************************************
	すぃーに関連した会話イベント
	protected Body from;			// 乗るゆっくり
	protected Body to;				// 未使用
	protected Obj target;			// すぃー
	protected int count;			// 1
*/
public class SuiSpeake extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();

	public SuiSpeake(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	public boolean simpleEventAction(Body b) {
		if(b.getCurrentEvent() != null || b.isTalking() || rnd.nextInt(20) !=0) return true;
		if( !b.canEventResponse() )
		{
			return false;
		}
		
		if(getFrom()==null){
			if(target==null){
				if(b.isRude() || rnd.nextBoolean()){
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.WantingSui), Const.HOLDMESSAGE, true, false);
					EventLogic.addWorldEvent(new SuiSpeake(b, null, null, 10), null, null);
				}
				else{
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.YukkuringSui), true);
				}
			}
			else{
				if(Translate.distance(b.getX(), b.getY(), target.getX(), target.getY()) < 200000){
					Body db = (Body) ((Sui)target).getbindobj();
					if(db == null) return false;
					if(db.isParent(b)){
						if(db.isFather(b)){
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.DrivingSuiPAPA), true);
						}
						else{
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.DrivingSuiMAMA), true);
						}
						
					}
					else if(b.isPartner(db) ){
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.DrivingSuiPartner), true);
					}
					else if(b.isParent(db)) {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.DrivingSuiChild), true);
					}
					else if(db.isSister(b)){
						if(db.isElderSister(b)){
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.DrivingSuiOldSister), true);
						}else{
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.DrivingSuiYoungSister), true);
						}
						
					}
					else{
						EventLogic.addBodyEvent(b,new SuiSpeake(null, null, null, 1), null, null);
					}
				}
			}
		}
		else{
			if(getFrom() == b) return false;
			if(target==null){
				if(Translate.distance(b.getX(), b.getY(), getFrom().getX(), getFrom().getY()) < 200000){
					if(b.isParent(getFrom())){
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantingSuiParent), true);
					}
					else if(b.isPartner(getFrom()) ){
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantingSuiPartner), true);
					}
				}
			}
			else{
				if(Translate.distance(b.getX(), b.getY(), target.getX(), target.getY()) < 200000){
					if(getFrom().isParent(b)){
						if(getFrom().isMother(b)){
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.hasSuiPAPAChild), true);
						}
						else{
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.hasSuiMAMAChild), true);
						}
					}
					else if(b.isPartner(getFrom()) ){
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.hasSuiPartner), true);
					}
					else if(b.isParent(getFrom())) {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.hasSuiChild), true);
					}
					else if(getFrom().isSister(b)){
						if(getFrom().isElderSister(b)){
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.hasSuiOldSister), true);
						}else{
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.hasSuiYoungSister), true);
						}
						
					}
					else{
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.YukkuringSui), true);
					}
				}
			}
		}
		return true;
	}
	
	public boolean checkEventResponse(Body b) {
		return false;
	}

	public void start(Body b) {
	}
	
	public boolean execute(Body b) {
		return true;
	}
	
	@Override
	public String toString() {
		return "すぃーのうわさ";
	}
}