package org.simyukkuri.engine;

import java.util.Iterator;
import java.util.List;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.Diffuser;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

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
	public static void processWorldTicks(WorldState curMap, int intervalCount) {
		TerrariumCollisionProcessor.processCollisions(curMap, intervalCount);
		List<WorldEntity> objectList = GameWorld.get().getObjects();
		Terrarium.resetTerrariumEnvironment();
		for (Iterator<WorldEntity> i = objectList.iterator(); i.hasNext();) {
			WorldEntity oex = i.next();
			TickResult ret = oex.clockTick();
			if (ret == TickResult.REMOVED) {
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
				if (o instanceof org.simyukkuri.entity.core.world.item.Food) {
					return WorldEntity.FOOD;
				}
				if (o instanceof org.simyukkuri.entity.core.world.item.Toilet) {
					return WorldEntity.TOILET;
				}
				if (o instanceof org.simyukkuri.entity.core.world.item.Toy) {
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
