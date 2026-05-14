package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.BodyRank;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.impl.FlyingEatEvent;
import org.simyukkuri.event.impl.SuperEatingTimeEvent;
import org.simyukkuri.util.GameRandom;

/**
 * 食事行動開始前のキャンセル条件をまとめたガード。
 */
public final class FoodActionGate {
	private FoodActionGate() {
	}

	public static boolean shouldSkipBeforeSearch(Yukkuri body, boolean[] forceEat) {
		if (!body.isVeryHungry()) {
			if (body.isToBody() || body.isToBed()
					|| (body.isToShit() && body.getIntelligence() == org.simyukkuri.enums.Intelligence.WISE)
					|| (body.isAdult() && body.isToSukkiri()) || body.isToSteal()
					|| (body.isRaper() && body.isExciting())) {
				if (body.isToFood()) {
					body.setToFood(false);
				}
				return true;
			}
		}
		if (body.getCurrentEvent() != null && body.getCurrentEvent() instanceof FlyingEatEvent) {
			return true;
		}
		if (body.isSleepy()) {
			if ((!body.isHungry() && body.isSmart() && body.getBodyRank() == BodyRank.KAIYU)
					|| (body.isFull() && body.getIntelligence() != org.simyukkuri.enums.Intelligence.WISE
							&& body.getBodyRank() != BodyRank.KAIYU)) {
				return true;
			}
		}
		if (body.getCoreAnkoState() == CoreAnkoState.NonYukkuriDisease) {
			return true;
		}
		if (body.isRaper() && body.isExciting() && !body.isStarving()) {
			return true;
		}

		EventPacket ev = body.getCurrentEvent();
		if (ev != null && ev.getPriority() != EventPacket.EventPriority.LOW) {
			if (ev instanceof SuperEatingTimeEvent
					&& ((SuperEatingTimeEvent) ev).getState() == SuperEatingTimeEvent.STATE.START) {
				forceEat[0] = true;
			} else if (!body.isVeryHungry()) {
				return true;
			}
		}

		if (body.getPublicRank() != PublicRank.UnunSlave) {
			if (body.getCarryItem(TakeoutItemType.SHIT) != null) {
				return true;
			}
		}
		if (body.getBurialState() != org.simyukkuri.enums.BurialState.NONE) {
			return true;
		}
		if (!body.isRude() && !body.isIdiot() && body.wantToShit() && !body.isSoHungry()) {
			body.clearActions();
			return true;
		}
		if (body.isExciting() && !body.isRaper() && !body.isSoHungry()) {
			if (body.isToFood()) {
				body.setToFood(false);
			}
			return true;
		}
		if (body.isExciting() && !body.isRaper() && body.isSoHungry()) {
			body.setCalm();
		}
		if (body.isSleeping() || body.nearToBirth() || body.isUnBirth() || body.isShutmouth()) {
			return true;
		}
		if (!body.canAction()) {
			// isVeryHungry かつ currentEvent のみが canAction=false の原因なら食事を許可する
			if (body.isVeryHungry() && body.getCurrentEvent() != null
					&& !body.isDead()
					&& body.getCriticalDamegeType() == null
					&& !body.isPealed() && !body.isPacked() && !body.isShitting()
					&& !body.isBirth() && !body.isSukkiri() && !body.isNeedled()
					&& !body.isNYD() && body.getBurialState() == org.simyukkuri.enums.BurialState.NONE) {
				// currentEvent のみが阻害要因 → 食事を許可（スキップしない）
			} else {
				return true;
			}
		}
		if ((body.isScare() || body.isFeelHardPain()) && GameRandom.nextBoolean()) {
			return true;
		}
		if (GameRandom.nextInt(300) == 0 && !body.isEating() && !forceEat[0]) {
			body.clearActions();
			return true;
		}
		return false;
	}
}
