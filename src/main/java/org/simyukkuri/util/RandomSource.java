package org.simyukkuri.util;

/**
 * RandomSource interface.
 */
public interface RandomSource {
	/**
	 * @param bound 上限（exclusive）
	 * @return 0 以上 bound 未満のランダム整数
	 */
	int nextInt(int bound);

	/**
	 * @return ランダムな boolean 値
	 */
	boolean nextBoolean();
}
