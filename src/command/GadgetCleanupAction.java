package src.command;

import java.util.LinkedList;
import java.util.List;

import src.base.Body;
import src.command.GadgetMenu.GadgetList;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Barrier;
import src.item.Food;
import src.system.MapPlaceData;
import src.util.GameWorld;
import src.util.YukkuriUtil;

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

		List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
		List<Shit> shitList = new LinkedList<Shit>(GameWorld.get().getCurrentMap().getShit().values());
		List<Vomit> vomitList = new LinkedList<Vomit>(GameWorld.get().getCurrentMap().getVomit().values());
		List<Food> foodList = new LinkedList<Food>(GameWorld.get().getCurrentMap().getFood().values());
		List<Stalk> stalkList = new LinkedList<Stalk>(GameWorld.get().getCurrentMap().getStalk().values());
		List<Barrier> wallList = GameWorld.get().getCurrentMap().getBarrier();
		if (isBody) {
			for (Body b : bodyList) {
				if (!b.isDead()) {
					b.setCleaning();
				}
			}
		}
		if (isDead) {
			for (Body b : bodyList) {
				if (b.isDead()) {
					b.remove();
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
			for (Body bodyTarget : bodyList) {
				bodyTarget.remove();
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
			Body b = YukkuriUtil.getBodyInstance(i);
			if (b != null && !b.isDead()) {
				return false;
			}
		}
		return true;
	}
}
