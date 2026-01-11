package src.logic;

import java.util.Map;

import src.SimYukkuri;
import src.base.Body;
import src.draw.Translate;
import src.enums.CriticalDamegeType;
import src.enums.Intelligence;
//import src.Body.CoreAnkoState;
//import src.item.Barrier;
import src.item.Stone;
//import src.EventPacket;

/***************************************************
 * 小石関係の処理
*/
public class StoneLogic {

	/**
	 *  小石チェック
	 * @param b ゆっくり
	 */
	public static final void checkPubble(Body b) {
		if( b == null ){
			return;
		}
		if( b.getCriticalDamegeType()  == CriticalDamegeType.CUT){
			return;
		}
		for (Map.Entry<Integer, Stone> entry : SimYukkuri.world.getCurrentMap().getStone().entrySet()) {
			Stone t = entry.getValue();

			int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY());
			if(t.getZ() != b.getZ()){
				continue;
			}
			if (b.getStepDist() > distance) {
				if(b.isBaby()) b.bodyCut();
				else{
					b.bodyInjure();
					b.runAway(t.getX(), t.getY());
				}
				break;
			}
			if (b.getStepDist()*3 > distance && b.getIntelligence() ==Intelligence.WISE) {
				b.runAway(t.getX(), t.getY());
				continue;
			}
		}
		return;
	}
}

