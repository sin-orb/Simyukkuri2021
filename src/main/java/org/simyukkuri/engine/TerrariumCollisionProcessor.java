package org.simyukkuri.engine;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.field.impl.Beltconveyor;
import org.simyukkuri.field.impl.Farm;
import org.simyukkuri.field.impl.Pool;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

/**
 * Terrarium.stepRun() の衝突系更新を担当する。
 */
public final class TerrariumCollisionProcessor {

	private TerrariumCollisionProcessor() {
	}

	/**
	 * 床置き、ベルト、プール、畑の判定を実行する。
	 *
	 * @param curMap        現在のマップ
	 * @param intervalCount 処理インターバル
	 */
	public static void processCollisions(WorldState curMap, int intervalCount) {
		TickResult ret = TickResult.NONE;
		List<WorldEntity> platformList = GameWorld.get().getCollisionBases();
		List<Entity> objList = GameWorld.get().getCollisionTargets();

		for (Iterator<WorldEntity> i = platformList.iterator(); i.hasNext();) {
			WorldEntity platform = i.next();
			ret = platform.clockTick();
			if (ret == TickResult.REMOVED) {
				i.remove();
				continue;
			}
			if (!platform.getEnabled()) {
				continue;
			}
			if (platform.getHitCheckObjType() == 0) {
				continue;
			}
			if (!platform.enableHitCheck()) {
				continue;
			}
			if (!platform.checkInterval(intervalCount)) {
				continue;
			}

			Rectangle re = platform.getCollisionRect(new Rectangle(0, 0, 0, 0));
			for (Entity o : objList) {
				if (o == null) {
					continue;
				}
				int objType = TerrariumTickProcessor.toObjExType(o);
				if ((objType & platform.getHitCheckObjType()) != 0) {
					platform.checkHitObj(re, o);
				}
			}
		}

		List<Beltconveyor> beltList = curMap.getBeltconveyors();
		objList = GameWorld.get().getCollisionTargets();
		for (Entity o : objList) {
			if (beltList == null || beltList.size() == 0) {
				break;
			}
			if (o == null || o.isRemoved()) {
				continue;
			}
			if ((Translate.getCurrentFieldGridValue(o.getX(), o.getY()) & FieldShape.FIELD_BELT) == 0) {
				continue;
			}
			for (Iterator<Beltconveyor> i = beltList.iterator(); i.hasNext();) {
				Beltconveyor belt = i.next();
				ret = belt.clockTick();
				if (ret == TickResult.REMOVED) {
					i.remove();
					continue;
				}
				if (!belt.mapContains(o.getX(), o.getY())) {
					continue;
				}
				if (belt.checkHitObj(o)) {
					belt.processHitObj(o);
					break;
				}
			}
		}

		List<Pool> poolList = curMap.getPools();
		if (poolList != null && poolList.size() > 0) {
			for (Iterator<Pool> i = poolList.iterator(); i.hasNext();) {
				Pool pool = i.next();
				ret = pool.clockTick();
				if (ret == TickResult.REMOVED) {
					i.remove();
				}
			}
		}
		objList = GameWorld.get().getCollisionTargets();
		for (Entity o : objList) {
			if (poolList == null || poolList.size() == 0) {
				if (o.isInPool()) {
					o.setInPool(false);
					o.setMostDepth(0);
					o.setInPool(false);
					o.setFallingUnderGround(false);
					if (o.getZ() < 0) {
						o.setCalcZ(0);
					}
				}
				continue;
			}
			if (o == null || o.isRemoved()) {
				continue;
			}
			if ((Translate.getCurrentFieldGridValue(o.getX(), o.getY()) & FieldShape.FIELD_POOL) == 0) {
				if (o.isInPool()) {
					if (o instanceof Yukkuri) {
						((Yukkuri) o).setLockmove(false);
					}
					o.setInPool(false);
					o.setMostDepth(0);
					o.setInPool(false);
					o.setFallingUnderGround(false);
					if (o.getZ() < 0) {
						o.setCalcZ(0);
					}
				}
				continue;
			}
			for (Pool pool : poolList) {
				if (pool.checkHitObj(o)) {
					pool.objHitProcess(o);
					break;
				}
			}
		}

		List<Farm> farmList = curMap.getFarms();
		if (farmList != null && farmList.size() > 0) {
			for (Iterator<Farm> i = farmList.iterator(); i.hasNext();) {
				Farm farm = i.next();
				ret = farm.clockTick();
				if (ret == TickResult.REMOVED) {
					i.remove();
				}
			}
		}
		objList = GameWorld.get().getCollisionTargets();
		for (Entity o : objList) {
			if (farmList == null || farmList.size() == 0) {
				break;
			}
			if (o == null || o.isRemoved()) {
				continue;
			}
			if ((Translate.getCurrentFieldGridValue(o.getX(), o.getY()) & FieldShape.FIELD_FARM) == 0) {
				continue;
			}
			for (Farm farm : farmList) {
				if (farm.checkHitObj(o)) {
					farm.objHitProcess(o);
					break;
				}
			}
		}
	}
}
