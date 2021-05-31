package src.event;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.enums.FavItemType;
import src.enums.PublicRank;
import src.item.Barrier;
import src.system.ResourceUtil;

/***************************************************
	お気に入りの情報を家族で共有するシンプルアクション
	protected Body from;			// イベントを発した個体
	protected Body to;				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class FavCopyEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * コンストラクタ.
	 */
	public FavCopyEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	@Override
	public boolean simpleEventAction(Body b) {
		if(getFrom() == b) return false;
		// イベントの発信者が家族かチェック
		if(b.isParent(getFrom()) || getFrom().isParent(b) || b.isPartner(getFrom())) {
			if (!Barrier.acrossBarrier(b.getX(), b.getY(), getFrom().getX(), getFrom().getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI)) {

				// 片方だけがうんうん奴隷の場合はなにもしない
				if( ((b.getPublicRank() == PublicRank.UnunSlave) && (getFrom().getPublicRank() == PublicRank.UnunSlave)) ||
					((b.getPublicRank() != PublicRank.UnunSlave) && (getFrom().getPublicRank() != PublicRank.UnunSlave)) )
				{
					b.setFavItem(FavItemType.BED, getFrom().getFavItem(FavItemType.BED));
				}
			}
		}
		return true;
	}
	@Override
	public boolean checkEventResponse(Body b) {
		return false;
	}
	@Override
	public void start(Body b) {
	}
	@Override
	public boolean execute(Body b) {
		return true;
	}
	
	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_favcopy");
	}
}