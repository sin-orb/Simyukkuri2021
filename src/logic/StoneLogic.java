package src.logic;

import java.util.Map;

import src.draw.Translate;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.world.item.Stone;
import src.enums.CriticalDamegeType;
import src.enums.Intelligence;
import src.util.GameWorld;

/***************************************************
 * 小石関係の処理
 */
public class StoneLogic {

	/**
	 * 小石チェック
	 * 
	 * @param b ゆっくり
	 */
	public static final void checkPubble(Yukkuri b) {
		if (b == null) {
			return;
		}
		if (b.getCriticalDamegeType() == CriticalDamegeType.CUT) {
			return;
		}
		for (Map.Entry<Integer, Stone> entry : GameWorld.get().getCurrentMap().getStone().entrySet()) {
			Stone t = entry.getValue();

			int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY());
			if (t.getZ() != b.getZ()) {
				continue;
			}
			if (b.getStepDist() > distance) {
				if (b.isBaby())
					b.bodyCut();
				else {
					b.bodyInjure();
					b.runAway(t.getX(), t.getY());
				}
				break;
			}
			if (b.getStepDist() * 3 > distance && b.getIntelligence() == Intelligence.WISE) {
				b.runAway(t.getX(), t.getY());
				continue;
			}
		}
		return;
	}
}
