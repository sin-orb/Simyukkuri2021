package src.logic;

import java.util.Map;

import src.draw.Translate;
import src.entity.core.Entity;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.world.item.Trash;
import src.enums.AgeState;
import src.event.impl.GetTrashOkazariEvent;
import src.field.impl.Barrier;
import src.util.GameWorld;

/***************************************************
 * ガラクタ関係の処理
 * ゴミや武器が必要なときにガラクタを検索して
 * イベントで取りに行く
 */
public class TrashLogic {

	/**
	 * ゴミおかざりチェック
	 * 
	 * @param body ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkTrashOkazari(Yukkuri body) {

		if (body.hasOkazari())
			return false;

		Entity trashCandidate = searchTrashObj(body);

		if (trashCandidate != null) {
			EventLogic.addBodyEvent(body, new GetTrashOkazariEvent(body, null, trashCandidate, 1), null, null);
			return true;
		}
		return false;
	}

	// 共通ガラクタ検索
	private static final Entity searchTrashObj(Yukkuri body) {

		Entity trashCandidate = null;
		int nearestDistance = body.getEyesightBase();
		int wallMode = body.getBodyAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if (body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		for (Map.Entry<Integer, Trash> entry : GameWorld.get().getCurrentMap().getTrash().entrySet()) {
			Trash t = entry.getValue();
			// 最小距離のものが見つかっていたら
			if (nearestDistance < 1) {
				break;
			}
			int distance = Translate.distance(body.getX(), body.getY(), t.getX(), t.getY() - t.getH() / 6);
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY() - t.getH() / 6,
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				trashCandidate = t;
				nearestDistance = distance;
			}
		}
		return trashCandidate;
	}
}
