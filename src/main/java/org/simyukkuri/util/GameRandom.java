package org.simyukkuri.util;

import java.util.Random;
import org.simyukkuri.SimYukkuri;

/**
 * GameRandom.
 */
public final class GameRandom {
	private static RandomSource override;

	private GameRandom() {
	}

	/** [0, bound) の範囲でランダムな整数を返す。 */
	public static int nextInt(int bound) {
		return current().nextInt(bound);
	}

	/** ランダムな真偽値を返す。 */
	public static boolean nextBoolean() {
		return current().nextBoolean();
	}

	/** テスト用の注入ソースをセットする。 */
	public static void setOverride(RandomSource source) {
		override = source;
	}

	/** テスト用注入ソースをクリアする。 */
	public static void clearOverride() {
		override = null;
	}

	private static RandomSource current() {
		if (override != null) {
			return override;
		}
		final Random rnd = SimYukkuri.RND;
		return new RandomSource() {
			/** [0, bound) の範囲でランダムな整数を返す。 */
			@Override
			public int nextInt(int bound) {
				return rnd.nextInt(bound);
			}

			/** ランダムな真偽値を返す。 */
			@Override
			public boolean nextBoolean() {
				return rnd.nextBoolean();
			}
		};
	}
}
