package src.draw;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import src.entity.world.bodylinked.Okazari;
import src.enums.Event;
import src.game.Shit;
import src.game.Vomit;
import src.system.MapPlaceData;

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
	public static void processEntries(MapPlaceData curMap) {
		updateShit(curMap);
		updateVomit(curMap);
		updateOkazari(curMap);
	}

	private static void updateShit(MapPlaceData curMap) {
		List<Shit> shits = new LinkedList<Shit>();
		for (Map.Entry<Integer, Shit> entry : curMap.getShit().entrySet()) {
			Shit s = entry.getValue();
			Event ret = s.clockTick();
			if (ret != Event.REMOVED) {
				shits.add(s);
			}
		}
		curMap.getShit().clear();
		for (Shit shit : shits) {
			curMap.getShit().put(shit.objId, shit);
		}
	}

	private static void updateVomit(MapPlaceData curMap) {
		List<Vomit> vomits = new LinkedList<Vomit>();
		for (Map.Entry<Integer, Vomit> entry : curMap.getVomit().entrySet()) {
			Vomit v = entry.getValue();
			Event ret = v.clockTick();
			if (ret != Event.REMOVED) {
				vomits.add(v);
			}
		}
		curMap.getVomit().clear();
		for (Vomit vomit : vomits) {
			curMap.getVomit().put(vomit.objId, vomit);
		}
	}

	private static void updateOkazari(MapPlaceData curMap) {
		List<Okazari> okazaris = new LinkedList<Okazari>();
		for (Map.Entry<Integer, Okazari> entry : curMap.getOkazari().entrySet()) {
			Okazari o = entry.getValue();
			Event ret = o.clockTick();
			if (ret != Event.REMOVED) {
				okazaris.add(o);
			}
		}
		curMap.getOkazari().clear();
		for (Okazari o : okazaris) {
			curMap.getOkazari().put(o.objId, o);
		}
	}
}
