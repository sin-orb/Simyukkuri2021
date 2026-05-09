package src.logic;

import src.base.Body;
import src.event.EventPacket.EventPriority;
import src.base.Obj;
import src.draw.Translate;
import src.enums.TakeoutItemType;
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
	public static boolean shouldSkipPartnerAction(Body body) {
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
	public static Body resolveMappedTarget(Body body, int nearestDistance) {
		Obj target = body.takeMappedObj(body.getMoveTargetId());
		if ((body.isToBody() || body.isToSukkiri() || body.isToSteal()) && target instanceof Body) {
			Body targetBody = (Body) target;
			Body foundBody = targetBody;
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
	public static boolean shouldGoToParent(Body body) {
		return body.isCallingParents();
	}

	/**
	 * つがいが優先対象か判定する.
	 */
	public static Body getPartnerIfPreferred(Body body) {
		Body partnerBody = src.util.BodyRegistry.getBodyInstance(body.getPartner());
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
	public static Body resolveMoveTarget(Body body) {
		Obj moveTarget = body.takeMoveTarget();
		if (moveTarget instanceof Body) {
			return (Body) moveTarget;
		}
		return null;
	}

	/**
	 * 目標が見つからなかったときの fallback を処理する.
	 *
	 * @return 行動したかどうか
	 */
	public static boolean handleNoFoundTarget(Body body) {
		if (body.isExciting() && GameRandom.nextInt(60) == 0) {
			body.doOnanism();
			return true;
		}
		return false;
	}
}
