package org.simyukkuri.logic;

import java.util.Map;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

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
		checkPubble(b, GameWorld.get().getCurrentWorldState());
	}

	public static final void checkPubble(Yukkuri b, WorldState ws) {
		if (b == null) {
			return;
		}
		if (b.getCriticalDamageType() == CriticalDamageType.CUT) {
			return;
		}
		for (Map.Entry<Integer, Stone> entry : ws.getStones().entrySet()) {
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
