package src.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import src.base.Yukkuri;

/**
 * 現在のマップに属する body の参照を引く.
 */
public final class BodyRegistry {
	private BodyRegistry() {
	}

	/**
	 * ユニークIDからゆっくりのインスタンスを取得する.
	 *
	 * @param i ユニークID
	 * @return ユニークIDが指し示すゆっくり
	 */
	public static Yukkuri getBodyInstance(int i) {
		if (i == -1) {
			return null;
		}
		Map<Integer, Yukkuri> bodies = GameWorld.get().getCurrentMap().getBody();
		if (bodies.containsKey(i)) {
			return bodies.get(i);
		}
		return null;
	}

	/**
	 * オブジェクトIDからゆっくりを引いてくる.
	 *
	 * @param i オブジェクトID
	 * @return 対象のゆっくり
	 */
	public static Yukkuri getBodyInstanceFromObjId(int i) {
		if (i == -1) {
			return null;
		}
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Yukkuri b = entry.getValue();
			if (b.objId == i) {
				return b;
			}
		}
		return null;
	}

	/**
	 * 現在のマップに属するゆっくりを配列にして返す.
	 *
	 * @return 現在のマップに属するゆっくりの配列
	 */
	public static Yukkuri[] getBodyInstances() {
		List<Yukkuri> bodies = new LinkedList<>();
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			bodies.add(entry.getValue());
		}
		return bodies.toArray(new Yukkuri[0]);
	}
}
