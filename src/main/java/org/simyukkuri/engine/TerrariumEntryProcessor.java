package org.simyukkuri.engine;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.system.WorldState;

/**
 * Terrarium.stepRun() の map entry 系更新を担当する。
 */
public final class TerrariumEntryProcessor {

	private TerrariumEntryProcessor() {
	}

	/**
	 * うんうん、吐餡、おかざりを更新する。
	 *
	 * @param curMap 現在のマップ
	 */
	public static void processEntries(WorldState curMap) {
		updateShit(curMap);
		updateVomit(curMap);
		updateOkazari(curMap);
	}

	private static void updateShit(WorldState curMap) {
		List<Shit> shits = new LinkedList<Shit>();
		for (Map.Entry<Integer, Shit> entry : curMap.getShit().entrySet()) {
			Shit s = entry.getValue();
			TickResult ret = s.clockTick();
			if (ret != TickResult.REMOVED) {
				shits.add(s);
			}
		}
		curMap.getShit().clear();
		for (Shit shit : shits) {
			curMap.getShit().put(shit.objId, shit);
		}
	}

	private static void updateVomit(WorldState curMap) {
		List<Vomit> vomits = new LinkedList<Vomit>();
		for (Map.Entry<Integer, Vomit> entry : curMap.getVomit().entrySet()) {
			Vomit v = entry.getValue();
			TickResult ret = v.clockTick();
			if (ret != TickResult.REMOVED) {
				vomits.add(v);
			}
		}
		curMap.getVomit().clear();
		for (Vomit vomit : vomits) {
			curMap.getVomit().put(vomit.objId, vomit);
		}
	}

	private static void updateOkazari(WorldState curMap) {
		List<Okazari> okazaris = new LinkedList<Okazari>();
		for (Map.Entry<Integer, Okazari> entry : curMap.getOkazaris().entrySet()) {
			Okazari o = entry.getValue();
			TickResult ret = o.clockTick();
			if (ret != TickResult.REMOVED) {
				okazaris.add(o);
			}
		}
		curMap.getOkazaris().clear();
		for (Okazari o : okazaris) {
			curMap.getOkazaris().put(o.objId, o);
		}
	}
}
