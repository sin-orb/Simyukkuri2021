package src.draw;

import java.util.Iterator;
import java.util.List;

import src.base.ObjEX;
import src.enums.Event;
import src.enums.ObjEXType;
import src.item.Diffuser;
import src.system.MapPlaceData;
import src.util.GameWorld;

/**
 * Terrarium.stepRun() の body 以外の更新処理を担当する。
 */
public final class TerrariumTickProcessor {

	private TerrariumTickProcessor() {
	}

	/**
	 * 床置き、ベルト、プール、畑、固定オブジェクト、エフェクトを更新する。
	 *
	 * @param curMap 現在のマップ
	 * @param intervalCount 処理インターバル
	 */
	public static void processMapTicks(MapPlaceData curMap, int intervalCount) {
		TerrariumCollisionProcessor.processCollisions(curMap, intervalCount);
		List<ObjEX> objectList = GameWorld.get().getObjectList();
		Terrarium.resetTerrariumEnvironment();
		for (Iterator<ObjEX> i = objectList.iterator(); i.hasNext();) {
			ObjEX oex = i.next();
			Event ret = oex.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
			}
			if (oex.getObjEXType() == ObjEXType.DIFFUSER && oex.getEnabled()) {
				boolean[] flags = ((Diffuser) oex).getSteamType();
				Terrarium.applyDiffuserSteamFlags(flags);
			}
		}

		TerrariumEntryProcessor.processEntries(curMap);
	}

	static int toObjExType(src.base.Obj o) {
		switch (o.getObjType()) {
			case YUKKURI:
				return ObjEX.YUKKURI;
			case SHIT:
				return ObjEX.SHIT;
			case PLATFORM:
				return ObjEX.PLATFORM;
			case FIX_OBJECT:
				return ObjEX.FIX_OBJECT;
			case OBJECT:
				if (o instanceof src.item.Food) {
					return ObjEX.FOOD;
				}
				if (o instanceof src.item.Toilet) {
					return ObjEX.TOILET;
				}
				if (o instanceof src.item.Toy) {
					return ObjEX.TOY;
				}
				if (o instanceof src.game.Stalk) {
					return ObjEX.STALK;
				}
				return ObjEX.OBJECT;
			case VOMIT:
				return ObjEX.VOMIT;
			default:
				return 0;
		}
	}

}
