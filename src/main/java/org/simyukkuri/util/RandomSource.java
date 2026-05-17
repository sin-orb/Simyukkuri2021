package org.simyukkuri.util;

/**
 * RandomSource interface.
 */
public interface RandomSource {
	int nextInt(int bound);

	boolean nextBoolean();
}
