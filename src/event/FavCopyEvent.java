package src.event;
import src.util.GameText;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.enums.FavItemType;
import src.enums.PublicRank;
import src.item.Barrier;
import src.system.ResourceUtil;

/***************************************************
 * お気に入りの情報を家族で共有するシンプルアクション
 * protected Body from; // イベントを発した個体
 * protected Body to; // 未使用
 * protected Obj target; // 未使用
 * protected int count; // 1
 */
public class FavCopyEvent extends EventPacket {

	private static final long serialVersionUID = -6152139400229477473L;

	/**
	 * コンストラクタ.
	 */
	public FavCopyEvent(Body fromBody, Body toBody, Obj targetObject, int count) {
		super(fromBody, toBody, targetObject, count);
	}

	public FavCopyEvent() {

	}

	@Override
	public boolean simpleEventAction(Body body) {
		Body sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == body || sourceBody == null)
			return false;
		// イベントの発信者が家族かチェック
		if (body.isParent(sourceBody) || sourceBody.isParent(body) || body.isPartner(sourceBody)) {
			if (!Barrier.acrossBarrier(body.getX(), body.getY(), sourceBody.getX(), sourceBody.getY(),
					Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {

				// 片方だけがうんうん奴隷の場合はなにもしない
				if (((body.getPublicRank() == PublicRank.UnunSlave) && (sourceBody.getPublicRank() == PublicRank.UnunSlave)) ||
						((body.getPublicRank() != PublicRank.UnunSlave)
								&& (sourceBody.getPublicRank() != PublicRank.UnunSlave))) {
					body.setFavoriteItem(FavItemType.BED, sourceBody.getFavoriteItem(FavItemType.BED));
				}
			}
		}
		return true;
	}

	@Override
	public boolean checkEventResponse(Body body) {
		return false;
	}

	@Override
	public void start(Body body) {
	}

	@Override
	public boolean execute(Body body) {
		return true;
	}

	@Override
	public String toString() {
		return GameText.read("event_favcopy");
	}
}
