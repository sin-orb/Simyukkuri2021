package src.draw;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.enums.Event;
import src.field.impl.Beltconveyor;
import src.field.impl.Farm;
import src.field.impl.Pool;
import src.field.FieldShape;
import src.system.MapPlaceData;
import src.util.GameWorld;

/**
 * Terrarium.stepRun() の衝突系更新を担当する。
 */
public final class TerrariumCollisionProcessor {

	private TerrariumCollisionProcessor() {
	}

	/**
	 * 床置き、ベルト、プール、畑の判定を実行する。
	 *
	 * @param curMap 現在のマップ
	 * @param intervalCount 処理インターバル
	 */
	public static void processCollisions(MapPlaceData curMap, int intervalCount) {
		Event ret = Event.DONOTHING;
		List<ObjEX> platformList = GameWorld.get().getHitBaseList();
		List<Obj> objList = GameWorld.get().getHitTargetList();

		for (Iterator<ObjEX> i = platformList.iterator(); i.hasNext();) {
			ObjEX platform = i.next();
			ret = platform.clockTick();
			if (ret == Event.REMOVED) {
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
			for (Obj o : objList) {
				if (o == null) {
					continue;
				}
				int objType = TerrariumTickProcessor.toObjExType(o);
				if ((objType & platform.getHitCheckObjType()) != 0) {
					platform.checkHitObj(re, o);
				}
			}
		}

		List<Beltconveyor> beltList = curMap.getBeltconveyor();
		objList = GameWorld.get().getHitTargetList();
		for (Obj o : objList) {
			if (beltList == null || beltList.size() == 0) {
				break;
			}
			if (o == null || o.isRemoved()) {
				continue;
			}
			if ((Translate.getCurrentFieldMapNum(o.getX(), o.getY()) & FieldShape.FIELD_BELT) == 0) {
				continue;
			}
			for (Iterator<Beltconveyor> i = beltList.iterator(); i.hasNext();) {
				Beltconveyor belt = i.next();
				ret = belt.clockTick();
				if (ret == Event.REMOVED) {
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

		List<Pool> poolList = curMap.getPool();
		if (poolList != null && poolList.size() > 0) {
			for (Iterator<Pool> i = poolList.iterator(); i.hasNext();) {
				Pool pool = i.next();
				ret = pool.clockTick();
				if (ret == Event.REMOVED) {
					i.remove();
				}
			}
		}
		objList = GameWorld.get().getHitTargetList();
		for (Obj o : objList) {
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
			if ((Translate.getCurrentFieldMapNum(o.getX(), o.getY()) & FieldShape.FIELD_POOL) == 0) {
				if (o.isInPool()) {
					if (o instanceof Body) {
						((Body) o).setLockmove(false);
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

		List<Farm> farmList = curMap.getFarm();
		if (farmList != null && farmList.size() > 0) {
			for (Iterator<Farm> i = farmList.iterator(); i.hasNext();) {
				Farm farm = i.next();
				ret = farm.clockTick();
				if (ret == Event.REMOVED) {
					i.remove();
				}
			}
		}
		objList = GameWorld.get().getHitTargetList();
		for (Obj o : objList) {
			if (farmList == null || farmList.size() == 0) {
				break;
			}
			if (o == null || o.isRemoved()) {
				continue;
			}
			if ((Translate.getCurrentFieldMapNum(o.getX(), o.getY()) & FieldShape.FIELD_FARM) == 0) {
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
