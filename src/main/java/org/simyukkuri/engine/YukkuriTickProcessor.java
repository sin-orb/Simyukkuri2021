package org.simyukkuri.engine;

import java.util.List;
import java.util.Map;

import org.simyukkuri.command.GadgetAction;
import org.simyukkuri.entity.core.attachment.impl.Fire;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.PanicType;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.logic.BedLogic;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.logic.EventLogic;
import org.simyukkuri.logic.FamilyActionLogic;
import org.simyukkuri.logic.FoodLogic;
import org.simyukkuri.logic.StoneLogic;
import org.simyukkuri.logic.ToiletLogic;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/**
 * Terrarium の 1 体分の更新処理を担当する。
 */
public final class YukkuriTickProcessor {

	private YukkuriTickProcessor() {
	}

	/**
	 * 1 体分の更新を実行する。
	 *
	 * @param terrarium  生成補助を提供する Terrarium
	 * @param curMap     現在のマップ
	 * @param yukkuri    更新対象
	 * @param babyList   生成待ちの赤ゆリスト
	 * @param transCheck 突然変異チェックを行うか
	 * @return 突然変異候補。なければ null
	 */
	public static Yukkuri processYukkuri(Terrarium terrarium, WorldState curMap, Yukkuri yukkuri, List<Yukkuri> babyList,
			boolean transCheck) {
		TickResult ret = yukkuri.clockTick();
		switch (ret) {
			case DEAD:
				handleDead(terrarium, curMap, yukkuri, babyList);
				yukkuri.upDate();
				return null;
			case BIRTH:
				handleBirthBaby(terrarium, yukkuri, curMap, babyList);
				break;
			case SHIT:
				int objId = terrarium.addShit(yukkuri.getX(), yukkuri.getY(), yukkuri.getZ() + yukkuri.getSize() / 15,
						yukkuri, yukkuri.getShitType());
				curMap.getShit().get(objId).kick(0, 1, 1);
				break;
			case CRUSHED_SHIT:
				terrarium.addCrushedShit(yukkuri.getX(), yukkuri.getY(), yukkuri.getZ(), yukkuri,
						yukkuri.getShitType());
				break;
			case VOMIT:
				terrarium.addVomit(yukkuri.getX(), yukkuri.getY(), yukkuri.getZ(), yukkuri, yukkuri.getShitType());
				break;
			case REMOVED:
				yukkuri.upDate();
				yukkuri.remove();
				return null;
			default:
				break;
		}

		if (yukkuri.isUnBirth()) {
			yukkuri.upDate();
			return null;
		}

		checkFire(yukkuri, curMap);
		StoneLogic.checkPubble(yukkuri, curMap);

		if (yukkuri.getPanicType() != null && !yukkuri.isUnBirth() && !yukkuri.isDamagedHeavily()) {
			checkPanic(yukkuri, curMap);
		} else {
			if (yukkuri.getCurrentEvent() != null) {
				EventLogic.eventUpdate(yukkuri);
			}

			boolean hasChildren = false;
			List<Yukkuri> childrenList = YukkuriLogic.createActiveChildren(yukkuri, true);
			if (childrenList != null && !childrenList.isEmpty()) {
				hasChildren = true;
			}

			boolean shouldCheck = yukkuri.getBlockedTicks() == 0;
			if (hasChildren) {
				if (shouldCheck) {
					if (FamilyActionLogic.checkFamilyAction(yukkuri)) {
						shouldCheck = false;
					} else {
						shouldCheck = true;
					}
				}
			}

			if (shouldCheck) {
				if (FoodLogic.checkFood(yukkuri, curMap)) {
					shouldCheck = false;
				} else {
					shouldCheck = true;
				}
			}

			if (shouldCheck) {
				if (YukkuriLogic.checkPartner(yukkuri)) {
					shouldCheck = false;
				} else {
					shouldCheck = true;
				}
			}

			if (shouldCheck) {
				if (ToiletLogic.checkShit(yukkuri, curMap)) {
					shouldCheck = false;
				} else {
					shouldCheck = true;
				}
			}

			if (shouldCheck) {
				if (ToiletLogic.checkToilet(yukkuri, curMap)) {
					shouldCheck = false;
				} else {
					shouldCheck = true;
				}
			}

			if (shouldCheck) {
				if (BedLogic.checkBed(yukkuri, curMap)) {
					shouldCheck = false;
				} else {
					shouldCheck = true;
				}
			}

			if (!hasChildren) {
				if (shouldCheck) {
					if (!FamilyActionLogic.checkFamilyAction(yukkuri)) {
						shouldCheck = true;
					} else {
						shouldCheck = false;
					}
				}
			}
		}

		if (yukkuri.getStalkBabyTypes().size() > 0) {
			int j = 0;
			Stalk s = null;
			for (Dna babyTypes : yukkuri.getStalkBabyTypes()) {
				if (j % 5 == 0) {
					s = (Stalk) GadgetAction.putObjEX(Stalk.class, yukkuri.getX(), yukkuri.getY(),
							yukkuri.getDirection().ordinal());
					yukkuri.getStalks().add(s);
					s.setPlantYukkuri(yukkuri);
				}
				if (babyTypes != null) {
					Yukkuri baby = terrarium.makeYukkuri(yukkuri.getX(), yukkuri.getY(), 0, babyTypes,
							AgeState.BABY, yukkuri, org.simyukkuri.util.YukkuriLookup.getYukkuriById(yukkuri.getPartner()));
					babyList.add(baby);
					baby.setBindStalk(s);
					s.addAttachedBaby(baby);
					baby.setUnBirth(true);
					baby.setShadowVisible(false);
				} else {
					s.addAttachedBaby(null);
				}
				j++;
			}
			yukkuri.getStalkBabyTypes().clear();
		}
		yukkuri.upDate();

		if (transCheck) {
			return yukkuri.checkTransform();
		}
		return null;
	}

	private static void handleDead(Terrarium terrarium, WorldState curMap, Yukkuri b, List<Yukkuri> babyList) {
		if (b.isInfration()) {
			int burstPower = (b.getSize() - b.getOriginSize()) * 3 / 4;
			for (Dna babyTypes : b.getBabyTypes()) {
				Yukkuri baby = terrarium.makeYukkuri(b.getX(), b.getY(), b.getZ() + b.getSize() / 20, babyTypes,
						AgeState.BABY, b, org.simyukkuri.util.YukkuriLookup.getYukkuriById(b.getPartner()));
				baby.kick(GameRandom.nextInt(burstPower / 4 + 1) - burstPower / 8,
						GameRandom.nextInt(burstPower / 4 + 1) - burstPower / 8,
						GameRandom.nextInt(burstPower / 5 + 1) - burstPower / 10 - 1);
				babyList.add(baby);
			}
			b.getBabyTypes().clear();
			if (b.getStalks() != null) {
				for (Stalk s : b.getStalks()) {
					if (s != null) {
						s.kick(GameRandom.nextInt(burstPower / 4 + 1) - burstPower / 8,
								GameRandom.nextInt(burstPower / 4 + 1) - burstPower / 8,
								GameRandom.nextInt(burstPower / 5 + 1) - burstPower / 10 - 1);
					}
				}
			}
			b.disPlantStalks();
			if (b.getShit() > b.getShitLimitBase()[b.getAgeState().ordinal()]) {
				for (int j = 0; b.getShit() / b.getShitLimitBase()[b.getAgeState().ordinal()] > j; j++) {
					int i = terrarium.addShit(b.getX(), b.getY(), b.getZ() + b.getSize() / 15, b, b.getShitType());
					curMap.getShit().get(i).kick(GameRandom.nextInt(burstPower / 4 + 1) - burstPower / 8,
							GameRandom.nextInt(burstPower / 4 + 1) - burstPower / 8,
							GameRandom.nextInt(burstPower / 5 + 1) - burstPower / 10 - 1);
				}
			}
			b.setShit(0);
			if (!b.isCrushed()) {
				b.strikeByPress();
			}
		} else if (b.isCrushed()) {
			b.disPlantStalks();
		}
	}

	private static void handleBirthBaby(Terrarium terrarium, Yukkuri b, WorldState curMap, List<Yukkuri> babyList) {
		if (b.getAge() % 10 == 0 && !b.isHasPants()) {
			Dna babyType = b.getBabyTypesDequeue();
			if (babyType != null) {
				Yukkuri baby = terrarium.makeYukkuri(b.getX(), b.getY(), b.getZ() + b.getSize() / 15, babyType,
						AgeState.BABY, b, org.simyukkuri.util.YukkuriLookup.getYukkuriById(b.getPartner()));
				baby.kick(0, 5, -2);
				babyList.add(baby);
			}
		}
		if (b.getStalks() != null) {
			for (Stalk s : b.getStalks()) {
				if (s != null) {
					for (Integer bab : s.getAttachedBabyIds()) {
						if (bab == null) {
							continue;
						}
						Yukkuri ba = org.simyukkuri.util.YukkuriLookup.getYukkuriById(bab);
						if (ba != null) {
							ba.setUnBirth(false);
							ba.setShadowVisible(true);
							ba.setBindStalk(null);
							ba.setParentLinkId(-1);
							if (ba.isBaby()) {
								ba.setAgeState(AgeState.BABY);
							}
							ba.kick(0, 0, 0);
						}
					}
					s.getAttachedBabyIds().clear();
					s.setPlantYukkuri(null);
					int fx;
					int fy;
					for (int f = 0; f < 5; f++) {
						fx = s.getX() - 6 + (f * 7);
						fy = s.getY() - 5 + GameRandom.nextInt(10);
						fx = Math.max(0, fx);
						fx = Math.min(fx, Translate.getWorldWidth());
						fy = Math.max(0, fy);
						fy = Math.min(fy, Translate.getWorldHeight());
						org.simyukkuri.entity.core.world.item.Food food = (org.simyukkuri.entity.core.world.item.Food) GadgetAction.putObjEX(
								org.simyukkuri.entity.core.world.item.Food.class, fx, fy,
								org.simyukkuri.entity.core.world.item.Food.FoodType.STALK.ordinal());
						curMap.getFoods().put(food.objId, food);
					}
					s.remove();
				}
			}
			b.removeAllStalks();
		}
		if (b.getBabyTypes().size() == 0) {
			b.setHasBaby(false);
		}
		if (b.getStalks() == null || b.getStalks().size() == 0) {
			b.setHasStalk(false);
		}
	}

	private static void checkPanic(Yukkuri b, WorldState curMap) {
		if (b.isDead() || b.isPealed()) {
			return;
		}
		int minDistance;
		for (Map.Entry<Integer, Yukkuri> entry : curMap.getYukkuriRegistry().entrySet()) {
			Yukkuri p = entry.getValue();
			if (p == b) {
				continue;
			}
			if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
					Barrier.BODY_BLOCK_FLAGS[b.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			minDistance = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
			if (minDistance <= p.getEyesightBase()) {
				if (b.getPanicType() == PanicType.BURN && !p.isRaper()) {
					p.setPanic(true, PanicType.FEAR);
				}
			}
		}
	}

	private static void checkFire(Yukkuri b, WorldState curMap) {
		int minDistance;
		if (b.getAttachmentSize(Fire.class) == 0) {
			return;
		}
		for (Map.Entry<Integer, Yukkuri> entry : curMap.getYukkuriRegistry().entrySet()) {
			Yukkuri p = entry.getValue();
			if (p == b) {
				continue;
			}
			if (b.isRemoved()) {
				continue;
			}
			if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
					Barrier.BODY_BLOCK_FLAGS[b.getAgeState().ordinal()])) {
				continue;
			}
			minDistance = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
			if (minDistance <= Translate.distance(0, 0, b.getStep() * 2, b.getStep() * 2)) {
				p.giveFire();
			}
		}
	}
}
