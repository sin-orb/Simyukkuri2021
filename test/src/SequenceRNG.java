package src;

import java.util.Random;

/**
 * A Random subclass that produces controlled sequences for testing.
 * Can be configured to return specific values to hit specific code branches.
 */
public class SequenceRNG extends Random {
    private int[] sequence;
    private int index = 0;
    private boolean repeat = true;

    /**
     * Create a SequenceRNG that returns values from the given sequence.
     * 
     * @param sequence Array of values to return from nextInt()
     */
    public SequenceRNG(int... sequence) {
        this.sequence = sequence;
        this.index = 0;
    }

    /**
     * Create a SequenceRNG with a specific seed for reproducible randomness.
     * 
     * @param seed Seed for the random number generator
     */
    public SequenceRNG(long seed) {
        super(seed);
        this.sequence = null;
    }

    /**
     * Set whether to repeat the sequence when it ends.
     * 
     * @param repeat If true, loops back to start; if false, uses Random after
     *               sequence ends
     */
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    @Override
    public int nextInt(int bound) {
        if (sequence != null && index < sequence.length) {
            int value = sequence[index++];
            if (index >= sequence.length && repeat) {
                index = 0; // Loop back to start
            }
            // Return value modulo bound to ensure it's within range
            return Math.abs(value) % bound;
        }
        // Fall back to actual random if no sequence or sequence exhausted
        return super.nextInt(bound);
    }

    @Override
    public int nextInt() {
        if (sequence != null && index < sequence.length) {
            int value = sequence[index++];
            if (index >= sequence.length && repeat) {
                index = 0;
            }
            return value;
        }
        return super.nextInt();
    }

    /**
     * Reset the sequence to the beginning.
     */
    public void reset() {
        this.index = 0;
    }
}
