package src.logic;

import src.base.Body;
import src.base.EventPacket.EventPriority;
import src.base.Obj;
import src.draw.Translate;
import src.enums.TakeoutItemType;
import src.item.Barrier;
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
	public static boolean shouldSkipPartnerAction(Body b) {
		if (b.isToFood() || b.isToBed() || b.isToShit()) {
			return true;
		}
		if ((!b.isExciting() && !b.isRude() && b.wantToShit()) || b.nearToBirth()) {
			return true;
		}
		if (b.isNYD()) {
			return true;
		}
		if (b.getCurrentEvent() != null && b.getCurrentEvent().getPriority() != EventPriority.LOW) {
			return true;
		}
		if (b.getTakeoutItem(TakeoutItemType.SHIT) != null) {
			if (b.isExciting()) {
				b.dropTakeoutItem(TakeoutItemType.SHIT);
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * 既存の移動対象が相手ボディなら、到達済みか確認して返す.
	 */
	public static Body resolveMappedTarget(Body b, int minDistance) {
		Obj target = b.takeMappedObj(b.getMoveTarget());
		if ((b.isToBody() || b.isToSukkiri() || b.isToSteal()) && target instanceof Body) {
			Body p = (Body) target;
			Body found = p;
			int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
			if (minDistance > dist) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
						Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					found = null;
				}
			}
			return found;
		}
		return null;
	}

	/**
	 * 親に向かうべきか判定する.
	 */
	public static boolean shouldGoToParent(Body b) {
		return b.isCallingParents();
	}

	/**
	 * つがいが優先対象か判定する.
	 */
	public static Body getPartnerIfPreferred(Body b) {
		Body pa = src.util.YukkuriUtil.getBodyInstance(b.getPartner());
		if (b.isExciting() && b.isRaper() && b.isToSukkiri()) {
			return null;
		}
		if (b.isExciting() && pa != null && !(pa.isDead()) && !b.isRaper()) {
			if (b.getPublicRank() == pa.getPublicRank()) {
				return pa;
			}
		}
		return null;
	}

	/**
	 * 既存候補が見つからなければ null を返す.
	 */
	public static Body resolveMoveTarget(Body b) {
		Obj oMoveTarget = b.takeMoveTarget();
		if (oMoveTarget instanceof Body) {
			return (Body) oMoveTarget;
		}
		return null;
	}

	/**
	 * 目標が見つからなかったときの fallback を処理する.
	 *
	 * @return 行動したかどうか
	 */
	public static boolean handleNoFoundTarget(Body b) {
		if (b.isExciting() && GameRandom.nextInt(60) == 0) {
			b.doOnanism();
			return true;
		}
		return false;
	}
}
