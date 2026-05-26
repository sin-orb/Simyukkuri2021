package org.simyukkuri.event.impl;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.util.GameText;

/**
 * お気に入りの情報を家族で共有するシンプルアクション
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 未使用
 * protected Entity target; // 未使用
 * protected int count; // 1
 */
public class FavCopyEvent extends EventPacket {

	private static final long serialVersionUID = -6152139400229477473L;

	/**
	 * コンストラクタ.
	 */
	public FavCopyEvent(Yukkuri fromBody, Yukkuri toBody, Entity targetObject, int count) {
		super(fromBody, toBody, targetObject, count);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public FavCopyEvent() {

	}

	/** ゆっくり以外のエンティティに対する簡易参加チェック。 */
	@Override
	public boolean simpleEventAction(Yukkuri body) {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == body || sourceBody == null) {
			return false;
		}
		// イベントの発信者が家族かチェック
		if (body.isParent(sourceBody) || sourceBody.isParent(body) || body.isPartner(sourceBody)) {
			if (!Barrier.acrossBarrier(body.getX(), body.getY(), sourceBody.getX(), sourceBody.getY(),
					Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {

				// 片方だけがうんうん奴隷の場合はなにもしない
				if (((body.getPublicRank() == PublicRank.UNUN_SLAVE)
						&& (sourceBody.getPublicRank() == PublicRank.UNUN_SLAVE))
						|| ((body.getPublicRank() != PublicRank.UNUN_SLAVE)
								&& (sourceBody.getPublicRank() != PublicRank.UNUN_SLAVE))) {
					body.setFavoriteItem(FavItemType.BED, sourceBody.getFavoriteItem(FavItemType.BED));
				}
			}
		}
		return true;
	}

	/** イベントへの参加可否を判定し、参加可能なら true を返す。 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		return false;
	}

	/** イベントの開始処理を実行する。 */
	@Override
	public void start(Yukkuri body) {
	}

	/** イベント終了判定を行い true で終了する。 */
	@Override
	public boolean execute(Yukkuri body) {
		return true;
	}

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_favcopy");
	}
}
