package src.logic;

import src.draw.Translate;
import src.entity.core.Entity;
import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.TakeoutItemType;
import src.event.EventPacket.EventPriority;
import src.field.impl.Barrier;
import src.util.GameRandom;

/**
 * checkPartner の入口条件と既存ターゲット判定.
 */
public final class BodyPartnerEntryRule {

	private BodyPartnerEntryRule() {
	}

	/**
	 * パートナー処理をスキップすべきか判定する.
	 */
	public static boolean shouldSkipPartnerAction(Yukkuri body) {
		if (body.isToFood() || body.isToBed() || body.isToShit()) {
			return true;
		}
		if ((!body.isExciting() && !body.isRude() && body.wantToShit()) || body.nearToBirth()) {
			return true;
		}
		if (body.isNYD()) {
			return true;
		}
		if (body.getCurrentEvent() != null && body.getCurrentEvent().getPriority() != EventPriority.LOW) {
			return true;
		}
		if (body.getCarryItem(TakeoutItemType.SHIT) != null) {
			if (body.isExciting()) {
				body.dropTakeoutItem(TakeoutItemType.SHIT);
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * 既存の移動対象が相手ボディなら、到達済みか確認して返す.
	 */
	public static Yukkuri resolveMappedTarget(Yukkuri body, int nearestDistance) {
		Entity target = body.takeMappedObj(body.getMoveTargetId());
		if ((body.isToBody() || body.isToSukkiri() || body.isToSteal()) && target instanceof Yukkuri) {
			Yukkuri targetBody = (Yukkuri) target;
			Yukkuri foundBody = targetBody;
			int distanceToTarget = Translate.distance(body.getX(), body.getY(), targetBody.getX(), targetBody.getY());
			if (nearestDistance > distanceToTarget) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), targetBody.getX(), targetBody.getY(),
						Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					foundBody = null;
				}
			}
			return foundBody;
		}
		return null;
	}

	/**
	 * 親に向かうべきか判定する.
	 */
	public static boolean shouldGoToParent(Yukkuri body) {
		return body.isCallingParents();
	}

	/**
	 * つがいが優先対象か判定する.
	 */
	public static Yukkuri getPartnerIfPreferred(Yukkuri body) {
		Yukkuri partnerBody = src.util.BodyRegistry.getBodyInstance(body.getPartner());
		if (body.isExciting() && body.isRaper() && body.isToSukkiri()) {
			return null;
		}
		if (body.isExciting() && partnerBody != null && !(partnerBody.isDead()) && !body.isRaper()) {
			if (body.getPublicRank() == partnerBody.getPublicRank()) {
				return partnerBody;
			}
		}
		return null;
	}

	/**
	 * 既存候補が見つからなければ null を返す.
	 */
	public static Yukkuri resolveMoveTarget(Yukkuri body) {
		Entity moveTarget = body.takeMoveTarget();
		if (moveTarget instanceof Yukkuri) {
			return (Yukkuri) moveTarget;
		}
		return null;
	}

	/**
	 * 目標が見つからなかったときの fallback を処理する.
	 *
	 * @return 行動したかどうか
	 */
	public static boolean handleNoFoundTarget(Yukkuri body) {
		if (body.isExciting() && GameRandom.nextInt(60) == 0) {
			body.doOnanism();
			return true;
		}
		return false;
	}
}
