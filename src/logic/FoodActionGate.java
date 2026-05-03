package src.logic;

import src.base.Body;
import src.base.EventPacket;
import src.enums.BodyRank;
import src.enums.CoreAnkoState;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.event.FlyingEatEvent;
import src.event.SuperEatingTimeEvent;
import src.util.GameRandom;

/**
 * 食事行動開始前のキャンセル条件をまとめたガード。
 */
public final class FoodActionGate {
	private FoodActionGate() {
	}

	public static boolean shouldSkipBeforeSearch(Body b, boolean[] forceEat) {
		if (!b.isVeryHungry()) {
			if (b.isToBody() || b.isToBed() || (b.isToShit() && b.getIntelligence() == src.enums.Intelligence.WISE)
					|| (b.isAdult() && b.isToSukkiri()) || b.isToSteal()
					|| (b.isRaper() && b.isExciting())) {
				if (b.isToFood()) {
					b.setToFood(false);
				}
				return true;
			}
		}
		if (b.getCurrentEvent() != null && b.getCurrentEvent() instanceof FlyingEatEvent) {
			return true;
		}
		if (b.isSleepy()) {
			if ((!b.isHungry() && b.isSmart() && b.getBodyRank() == BodyRank.KAIYU)
					|| (b.isFull() && b.getIntelligence() != src.enums.Intelligence.WISE && b.getBodyRank() != BodyRank.KAIYU)) {
				return true;
			}
		}
		if (b.geteCoreAnkoState() == CoreAnkoState.NonYukkuriDisease) {
			return true;
		}
		if (b.isRaper() && b.isExciting() && !b.isStarving()) {
			return true;
		}

		EventPacket ev = b.getCurrentEvent();
		if (ev != null && ev.getPriority() != EventPacket.EventPriority.LOW) {
			if (ev instanceof SuperEatingTimeEvent
					&& ((SuperEatingTimeEvent) ev).getState() == SuperEatingTimeEvent.STATE.START) {
				forceEat[0] = true;
			} else if (!b.isVeryHungry()) {
				return true;
			}
		}

		if (b.getPublicRank() != PublicRank.UnunSlave) {
			if (b.getTakeoutItem(TakeoutItemType.SHIT) != null) {
				return true;
			}
		}
		if (b.getBaryState() != src.enums.BaryInUGState.NONE) {
			return true;
		}
		if (!b.isRude() && !b.isIdiot() && b.wantToShit() && !b.isSoHungry()) {
			b.clearActions();
			return true;
		}
		if (b.isExciting() && !b.isRaper() && !b.isSoHungry()) {
			if (b.isToFood()) {
				b.setToFood(false);
			}
			return true;
		}
		if (b.isExciting() && !b.isRaper() && b.isSoHungry()) {
			b.setCalm();
		}
		if (b.isSleeping() || b.nearToBirth() || b.isUnBirth() || b.isShutmouth()) {
			return true;
		}
		if (!b.canAction()) {
			// isVeryHungry かつ currentEvent のみが canAction=false の原因なら食事を許可する
			if (b.isVeryHungry() && b.getCurrentEvent() != null
					&& !b.isDead()
					&& b.getCriticalDamegeType() == null
					&& !b.isPealed() && !b.isPacked() && !b.isShitting()
					&& !b.isBirth() && !b.isSukkiri() && !b.isbNeedled()
					&& !b.isNYD() && b.getBaryState() == src.enums.BaryInUGState.NONE) {
				// currentEvent のみが阻害要因 → 食事を許可（スキップしない）
			} else {
				return true;
			}
		}
		if ((b.isScare() || b.isFeelHardPain()) && GameRandom.nextBoolean()) {
			return true;
		}
		if (GameRandom.nextInt(300) == 0 && !b.isEating() && !forceEat[0]) {
			b.clearActions();
			return true;
		}
		return false;
	}
}
