package org.simyukkuri.draw;

import java.util.Comparator;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.visual.TerrainBillboard;

/**
 * 描画順と選択順を決める比較器
 */
public final class RenderOrderComparator implements Comparator<Object> {
	private static final RenderOrderComparator INSTANCE = new RenderOrderComparator();

	/**
	 * シングルトンインスタンスを返す。
	 *
	 * @return 唯一のインスタンス
	 */
	public static RenderOrderComparator getInstance() {
		return INSTANCE;
	}

	/**
	 * Y 座標を基準に 2 オブジェクトの描画順を比較する。
	 * 同じ Y 座標の場合は年齢層の大きいゆっくりを先に（小さく見えるオブジェクトを後から）描画する。
	 *
	 * @param o1 比較対象オブジェクト 1
	 * @param o2 比較対象オブジェクト 2
	 * @return 負値なら o1 が先、正値なら o2 が先、0 なら同順
	 */
	@Override
	public final int compare(Object o1, Object o2) {
		int c = getSortY(o1) - getSortY(o2);
		if (c == 0) {
			// Improve visibility: at the same y-coordinate, draw small
			// objects after large ones.
			c = getTieBreak(o2) - getTieBreak(o1);
		}
		return c;
	}

	private int getSortY(Object obj) {
		if (obj instanceof TerrainBillboard) {
			return ((TerrainBillboard) obj).getSortY();
		}
		if (obj instanceof Entity) {
			return ((Entity) obj).getY();
		}
		return 0;
	}

	private int getTieBreak(Object obj) {
		if (obj instanceof Yukkuri) {
			return ((Yukkuri) obj).getAgeState().ordinal();
		}
		return 1;
	}
}
