package src.draw;

import java.util.Iterator;
import java.util.List;

import src.entity.core.Entity;
import src.entity.core.world.WorldEntity;
import src.entity.core.world.bodylinked.Stalk;
import src.entity.core.world.item.Diffuser;
import src.enums.Event;
import src.enums.WorldEntityKind;
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
	 * @param curMap        現在のマップ
	 * @param intervalCount 処理インターバル
	 */
	public static void processMapTicks(MapPlaceData curMap, int intervalCount) {
		TerrariumCollisionProcessor.processCollisions(curMap, intervalCount);
		List<WorldEntity> objectList = GameWorld.get().getObjectList();
		Terrarium.resetTerrariumEnvironment();
		for (Iterator<WorldEntity> i = objectList.iterator(); i.hasNext();) {
			WorldEntity oex = i.next();
			Event ret = oex.clockTick();
			if (ret == Event.REMOVED) {
				i.remove();
			}
			if (oex.getWorldEntityType() == WorldEntityKind.DIFFUSER && oex.getEnabled()) {
				boolean[] flags = ((Diffuser) oex).getSteamType();
				Terrarium.applyDiffuserSteamFlags(flags);
			}
		}

		TerrariumEntryProcessor.processEntries(curMap);
	}

	static int toObjExType(Entity o) {
		switch (o.getObjType()) {
			case YUKKURI:
				return WorldEntity.YUKKURI;
			case SHIT:
				return WorldEntity.SHIT;
			case PLATFORM:
				return WorldEntity.PLATFORM;
			case FIX_OBJECT:
				return WorldEntity.FIX_OBJECT;
			case OBJECT:
				if (o instanceof src.entity.core.world.item.Food) {
					return WorldEntity.FOOD;
				}
				if (o instanceof src.entity.core.world.item.Toilet) {
					return WorldEntity.TOILET;
				}
				if (o instanceof src.entity.core.world.item.Toy) {
					return WorldEntity.TOY;
				}
				if (o instanceof Stalk) {
					return WorldEntity.STALK;
				}
				return WorldEntity.OBJECT;
			case VOMIT:
				return WorldEntity.VOMIT;
			default:
				return 0;
		}
	}

}
