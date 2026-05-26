package org.simyukkuri.command;

import java.util.LinkedList;
import java.util.List;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

final class GadgetCleanupAction {

	private GadgetCleanupAction() {
	}

	static void immediateEvaluate(GadgetMenuChoice item) {
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
				break;
			default:
				break;
		}

		List<Yukkuri> bodyList = new LinkedList<Yukkuri>(GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values());
		List<Shit> shitList = new LinkedList<Shit>(GameWorld.get().getCurrentWorldState().getShit().values());
		List<Vomit> vomitList = new LinkedList<Vomit>(GameWorld.get().getCurrentWorldState().getVomit().values());
		final List<Food> foodList = new LinkedList<Food>(GameWorld.get().getCurrentWorldState().getFoods().values());
		final List<Stalk> stalkList = new LinkedList<Stalk>(GameWorld.get().getCurrentWorldState().getStalks().values());
		final List<Barrier> wallList = GameWorld.get().getCurrentWorldState().getBarriers();
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
				if (GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id) == null) {
					s.remove();
				} else if (checkNoBabyStalk(s)) {
					s.remove();
				}
			}
		}
		if (isWall) {
			wallList.clear();
			WorldState.clearGrid(GameWorld.get().getCurrentWorldState().getWallGrid());
		}
		if (isRemoveAll) {
			for (Yukkuri body : bodyList) {
				body.remove();
			}
			for (Shit s : shitList) {
				s.remove();
			}
			GameWorld.get().getCurrentWorldState().getShit().clear();
			for (Vomit v : vomitList) {
				v.remove();
			}
			GameWorld.get().getCurrentWorldState().getVomit().clear();
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
				if (GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id) == null) {
					s.remove();
				}
			}
			WorldState.clearGrid(GameWorld.get().getCurrentWorldState().getWallGrid());
		}
	}

	private static boolean checkNoBabyStalk(Stalk s) {
		if (s.getAttachedBabyIds().size() == 0) {
			return true;
		}
		for (Integer i : s.getAttachedBabyIds()) {
			if (i == null) {
				continue;
			}
			Yukkuri body = org.simyukkuri.util.YukkuriLookup.getYukkuriById(i);
			if (body != null && !body.isDead()) {
				return false;
			}
		}
		return true;
	}
}
