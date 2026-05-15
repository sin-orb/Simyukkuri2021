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

	public static RenderOrderComparator getInstance() {
		return INSTANCE;
	}

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
