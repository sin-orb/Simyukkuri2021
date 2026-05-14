package org.simyukkuri.command;

import java.util.LinkedList;
import java.util.List;

import org.simyukkuri.command.GadgetMenu.GadgetList;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.MapPlaceData;
import org.simyukkuri.util.GameWorld;

final class GadgetCleanupAction {

	private GadgetCleanupAction() {
	}

	static void immediateEvaluate(GadgetList item) {
		boolean isBody = false;
		boolean isDead = false;
		boolean isShit = false;
		boolean isFood = false;
		boolean isWall = false;
		boolean isRemoveAll = false;

		switch (item) {
			case YU_CLEAN:
				isBody = true;
				break;
			case BODY:
				isDead = true;
				break;
			case SHIT:
				isShit = true;
				break;
			case ETC:
				isFood = true;
				break;
			case ALL:
				isShit = true;
				isFood = true;
				isDead = true;
				break;
			case ALL_DELETE:
				isWall = true;
				break;
			case REMOVEALL:
				isRemoveAll = true;
			default:
				break;
		}

		List<Yukkuri> bodyList = new LinkedList<Yukkuri>(GameWorld.get().getCurrentMap().getBody().values());
		List<Shit> shitList = new LinkedList<Shit>(GameWorld.get().getCurrentMap().getShit().values());
		List<Vomit> vomitList = new LinkedList<Vomit>(GameWorld.get().getCurrentMap().getVomit().values());
		List<Food> foodList = new LinkedList<Food>(GameWorld.get().getCurrentMap().getFood().values());
		List<Stalk> stalkList = new LinkedList<Stalk>(GameWorld.get().getCurrentMap().getStalk().values());
		List<Barrier> wallList = GameWorld.get().getCurrentMap().getBarrier();
		if (isBody) {
			for (Yukkuri body : bodyList) {
				if (!body.isDead()) {
					body.setCleaning();
				}
			}
		}
		if (isDead) {
			for (Yukkuri body : bodyList) {
				if (body.isDead()) {
					body.remove();
				}
			}
		}
		if (isShit) {
			for (Shit s : shitList) {
				s.remove();
			}
			for (Vomit v : vomitList) {
				v.remove();
			}
		}
		if (isFood) {
			for (Food f : foodList) {
				if (f.isEmpty() || f.getFoodType() == Food.FoodType.STALK) {
					f.remove();
				}
			}
			Stalk[] stalks = stalkList.toArray(new Stalk[0]);
			for (Stalk s : stalks) {
				int id = s.getPlantYukkuri();
				if (GameWorld.get().getCurrentMap().getBody().get(id) == null) {
					s.remove();
				} else if (checkNoBabyStalk(s)) {
					s.remove();
				}
			}
		}
		if (isWall) {
			wallList.clear();
			MapPlaceData.clearMap(GameWorld.get().getCurrentMap().getWallMap());
		}
		if (isRemoveAll) {
			for (Yukkuri body : bodyList) {
				body.remove();
			}
			for (Shit s : shitList) {
				s.remove();
			}
			GameWorld.get().getCurrentMap().getShit().clear();
			for (Vomit v : vomitList) {
				v.remove();
			}
			GameWorld.get().getCurrentMap().getVomit().clear();
			for (Food f : foodList) {
				if (f.isEmpty()) {
					f.remove();
				}
				if (f.getFoodType() == Food.FoodType.STALK) {
					f.remove();
				}
			}

			for (Stalk s : stalkList) {
				int id = s.getPlantYukkuri();
				if (GameWorld.get().getCurrentMap().getBody().get(id) == null) {
					s.remove();
				}
			}
			MapPlaceData.clearMap(GameWorld.get().getCurrentMap().getWallMap());
		}
	}

	private static boolean checkNoBabyStalk(Stalk s) {
		if (s.getBindBabies().size() == 0) {
			return true;
		}
		for (Integer i : s.getBindBabies()) {
			if (i == null) {
				continue;
			}
			Yukkuri body = org.simyukkuri.util.BodyRegistry.getBodyInstance(i);
			if (body != null && !body.isDead()) {
				return false;
			}
		}
		return true;
	}
}
