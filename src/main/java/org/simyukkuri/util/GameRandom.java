package org.simyukkuri.util;

import java.util.Random;

import org.simyukkuri.SimYukkuri;

public final class GameRandom {
	private static RandomSource override;

	private GameRandom() {
	}

	public static int nextInt(int bound) {
		return current().nextInt(bound);
	}

	public static boolean nextBoolean() {
		return current().nextBoolean();
	}

	public static void setOverride(RandomSource source) {
		override = source;
	}

	public static void clearOverride() {
		override = null;
	}

	private static RandomSource current() {
		if (override != null) {
			return override;
		}
		final Random rnd = SimYukkuri.RND;
		return new RandomSource() {
			@Override
			public int nextInt(int bound) {
				return rnd.nextInt(bound);
			}

			@Override
			public boolean nextBoolean() {
				return rnd.nextBoolean();
			}
		};
	}
}
