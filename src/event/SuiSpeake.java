package src.event;
import src.util.GameMessages;
import src.util.GameText;

import src.Const;
import src.SimYukkuri;
import src.util.GameRandom;
import src.base.Yukkuri;
import src.event.EventPacket;
import src.base.Entity;
import src.draw.Translate;
import src.item.Sui;
import src.logic.EventLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;

/***************************************************
 * すぃーに関連した会話イベント
 * protected Yukkuri from; // 乗るゆっくり
 * protected Yukkuri to; // 未使用
 * protected Entity target; // すぃー
 * protected int count; // 1
 */
public class SuiSpeake extends EventPacket {

	private static final long serialVersionUID = -2170271875003339906L;

	/**
	 * コンストラクタ.
	 */
	public SuiSpeake(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	public SuiSpeake() {

	}

	@Override
	public boolean simpleEventAction(Yukkuri b) {
		if (b.getCurrentEvent() != null || b.isTalking() || GameRandom.nextInt(20) != 0)
			return true;
		if (!b.canEventResponse()) {
			return false;
		}
		Entity target = b.takeMappedObj(this.target);
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null) {
			if (target == null) {
				if (b.isRude() || GameRandom.nextBoolean()) {
					b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.WantingSui),
							Const.HOLDMESSAGE, true, false);
					EventLogic.addWorldEvent(new SuiSpeake(b, null, null, 10), null, null);
				} else {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.YukkuringSui), true);
				}
			} else {
				if (Translate.distance(b.getX(), b.getY(), target.getX(), target.getY()) < 200000) {
					Yukkuri db = (Yukkuri) ((Sui) target).getBindobj();
					if (db == null)
						return false;
					if (db.isParent(b)) {
						if (db.isFather(b)) {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.DrivingSuiPAPA), true);
						} else {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.DrivingSuiMAMA), true);
						}

					} else if (b.isPartner(db)) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.DrivingSuiPartner), true);
					} else if (b.isParent(db)) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.DrivingSuiChild), true);
					} else if (db.isSister(b)) {
						if (db.isElderSister(b)) {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.DrivingSuiOldSister), true);
						} else {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.DrivingSuiYoungSister), true);
						}

					} else {
						EventLogic.addBodyEvent(b, new SuiSpeake(null, null, null, 1), null, null);
					}
				}
			}
		} else {
			if (sourceBody == b)
				return false;
			if (target == null) {
				if (Translate.distance(b.getX(), b.getY(), sourceBody.getX(), sourceBody.getY()) < 200000) {
					if (b.isParent(sourceBody)) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.WantingSuiParent), true);
					} else if (b.isPartner(sourceBody)) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.WantingSuiPartner), true);
					}
				}
			} else {
				if (Translate.distance(b.getX(), b.getY(), target.getX(), target.getY()) < 200000) {
					if (sourceBody.isParent(b)) {
						if (sourceBody.isMother(b)) {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.hasSuiPAPAChild), true);
						} else {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.hasSuiMAMAChild), true);
						}
					} else if (b.isPartner(sourceBody)) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.hasSuiPartner), true);
					} else if (b.isParent(sourceBody)) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.hasSuiChild), true);
					} else if (sourceBody.isSister(b)) {
						if (sourceBody.isElderSister(b)) {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.hasSuiOldSister), true);
						} else {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.hasSuiYoungSister), true);
						}

					} else {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.YukkuringSui), true);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean checkEventResponse(Yukkuri b) {
		return false;
	}

	@Override
	public void start(Yukkuri b) {
	}

	@Override
	public boolean execute(Yukkuri b) {
		return true;
	}

	@Override
	public String toString() {
		return GameText.read("event_suispeak");
	}
}
