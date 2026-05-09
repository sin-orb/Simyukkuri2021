package src.draw;

import java.util.Map;

import src.base.Yukkuri;
import src.enums.PanicType;
import src.field.impl.Barrier;
import src.util.GameWorld;

/**
 * Terrarium に残っていたワールド寄りの個別ロジックをまとめる。
 * <p>
 * 現時点ではパニック伝播と家族関係の更新を担当する。
 * Terrarium 側の private メソッドは互換のため残し、実体だけをここへ移す。
 * </p>
 */
public final class TerrariumWorldLogic {
	private TerrariumWorldLogic() {
	}

	/**
	 * パニック時の挙動を反映する。
	 *
	 * @param b 対象ゆっくり
	 */
	public static void checkPanic(Yukkuri b) {
		if (b.isDead() || b.isPealed()) {
			return;
		}
		int minDistance;
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Yukkuri p = entry.getValue();
			if (p == b) {
				continue;
			}
			if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
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

	/**
	 * 家族の関係を設定する。
	 *
	 * @param b            対象ゆっくり
	 * @param p            対象のつがい
	 * @param bodyNewChild 新たに家族に加える新しい個体
	 */
	public static void setNewFamily(Yukkuri b, Yukkuri p, Yukkuri bodyNewChild) {
		if (b == null) {
			return;
		}
		for (Integer childId : b.getChildrenList()) {
			Yukkuri child = src.util.BodyRegistry.getBodyInstance(childId);
			if (child == null) {
				continue;
			}
			bodyNewChild.addElderSisterList(child);
			child.addSisterList(bodyNewChild);
		}
		b.addChildrenList(bodyNewChild);
		if ((p != null) && (p != b)) {
			setNewFamily(p, null, bodyNewChild);
		}
	}
}
