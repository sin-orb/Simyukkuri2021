package src.logic;

import java.util.ArrayList;
//import java.util.Random;

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
		小石関係の処理
*/
public class StoneLogic {

//	private static final Random rnd = new Random();

	// 小石
	public static final void checkPubble(Body b) {
		if( b == null ){
			return;
		}
		if( b.getCriticalDamegeType()  == CriticalDamegeType.CUT){
			return;
		}
		ArrayList<Stone> list = SimYukkuri.world.currentMap.stone;
		if( list == null || list.size() == 0 ){
			return;
		}
		for (Stone t: list) {
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
