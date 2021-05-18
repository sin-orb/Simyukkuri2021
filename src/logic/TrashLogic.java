package src.logic;


import java.util.List;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.AgeState;
import src.event.GetTrashOkazariEvent;
import src.item.Barrier;
import src.item.Trash;



/***************************************************
 * ガラクタ関係の処理
 * ゴミや武器が必要なときにガラクタを検索して
 * イベントで取りに行く
 */
public class TrashLogic {

	/**
	 *  ゴミおかざりチェック
	 * @param b ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkTrashOkazari(Body b) {
		
		if(b.hasOkazari()) return false;

		Obj found = searchTrashObj(b);

		if (found != null) {
			EventLogic.addBodyEvent(b, new GetTrashOkazariEvent(b, null, found, 1), null, null);
			return true;
		}
		return false;
	}
	
	// 共通ガラクタ検索
	private static final Obj searchTrashObj(Body b) {

		Obj found = null;
		int minDistance = b.getEYESIGHT();
		int wallMode = b.getBodyAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if(b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		List<Trash> list = SimYukkuri.world.currentMap.trash;
		for (Trash t: list) {
			// 最小距離のものが見つかっていたら
			if( minDistance < 1 )
			{
				break;
			}
			int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY() - t.getH()/6);
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY() - t.getH()/6, Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = t;
				minDistance = distance;
			}
		}
		return found;
	}
}


