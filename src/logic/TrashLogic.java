package src.logic;


import java.util.Map;

import src.SimYukkuri;
import src.util.GameWorld;
import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.AgeState;
import src.event.GetTrashOkazariEvent;
import src.field.impl.Barrier;
import src.item.Trash;



/***************************************************
 * ガラクタ関係の処理
 * ゴミや武器が必要なときにガラクタを検索して
 * イベントで取りに行く
 */
public class TrashLogic {

	/**
	 *  ゴミおかざりチェック
	 * @param body ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkTrashOkazari(Body body) {
		
		if(body.hasOkazari()) return false;

		Obj trashCandidate = searchTrashObj(body);

		if (trashCandidate != null) {
			EventLogic.addBodyEvent(body, new GetTrashOkazariEvent(body, null, trashCandidate, 1), null, null);
			return true;
		}
		return false;
	}
	
	// 共通ガラクタ検索
	private static final Obj searchTrashObj(Body body) {

		Obj trashCandidate = null;
		int nearestDistance = body.getEyesightBase();
		int wallMode = body.getBodyAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if(body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		for (Map.Entry<Integer, Trash> entry : GameWorld.get().getCurrentMap().getTrash().entrySet()) {
			Trash t = entry.getValue();
			// 最小距離のものが見つかっていたら
			if( nearestDistance < 1 )
			{
				break;
			}
			int distance = Translate.distance(body.getX(), body.getY(), t.getX(), t.getY() - t.getH()/6);
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY() - t.getH()/6, Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				trashCandidate = t;
				nearestDistance = distance;
			}
		}
		return trashCandidate;
	}
}



