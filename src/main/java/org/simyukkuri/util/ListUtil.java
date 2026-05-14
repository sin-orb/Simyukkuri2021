package org.simyukkuri.util;

import java.util.List;

/**
 * リストの汎用補助.
 */
public final class ListUtil {
	private ListUtil() {
	}

	/**
	 * リストから数値その値のもの（indexではなく）を取り除く.
	 *
	 * @param list 取り除きたいリスト
	 * @param num 取り除きたい値
	 */
	public static void removeContent(List<Integer> list, int num) {
		int removal = -1;
		for (int i = 0; i < list.size(); i++) {
			int val = list.get(i);
			if (val == num) {
				removal = i;
				break;
			}
		}
		if (removal != -1) {
			list.remove(removal);
		}
	}
}
