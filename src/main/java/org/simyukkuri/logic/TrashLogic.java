package org.simyukkuri.logic;

import java.util.Map;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Trash;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.event.impl.GetTrashOkazariEvent;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.util.GameWorld;

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
			EventLogic.addYukkuriEvent(body, new GetTrashOkazariEvent(body, null, trashCandidate, 1), null, null);
			return true;
		}
		return false;
	}

	// 共通ガラクタ検索
	private static final Entity searchTrashObj(Yukkuri body) {

		Entity trashCandidate = null;
		int nearestDistance = body.getEyesightBase();
		int wallMode = body.getAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if (body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		for (Map.Entry<Integer, Trash> entry : GameWorld.get().getCurrentWorldState().getTrashObjects().entrySet()) {
			Trash t = entry.getValue();
			// 最小距離のものが見つかっていたら
			if (nearestDistance < 1) {
				break;
			}
			int distance = Translate.distance(body.getX(), body.getY(), t.getX(), t.getY() - t.getH() / 6);
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY() - t.getH() / 6,
						Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				trashCandidate = t;
				nearestDistance = distance;
			}
		}
		return trashCandidate;
	}
}
