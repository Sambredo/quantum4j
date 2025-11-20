package io.quantum4j.core.backend;

import java.util.Collections;
import java.util.Map;

/**
 * Immutable container for quantum circuit execution results.
 * <p>
 * Stores measurement outcome counts from multiple circuit executions (shots). Each key is a classical bit string (e.g.,
 * "110") and each value is the number of times that outcome was measured across all shots.
 * </p>
 */
public final class Result {
    private final Map<String, Integer> counts;

    /**
     * Construct a Result from measurement counts.
     *
     * @param counts
     *            map of classical bit strings to outcome frequencies
     */
    public Result(Map<String, Integer> counts) {
        this.counts = Collections.unmodifiableMap(counts);
    }

    /**
     * Get the measurement outcome counts.
     *
     * @return immutable map of bit strings to outcome counts
     */
    public Map<String, Integer> getCounts() {
        return counts;
    }

    @Override
    public String toString() {
        return "Result{counts=" + counts + '}';
    }
}
