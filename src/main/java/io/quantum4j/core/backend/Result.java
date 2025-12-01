package io.quantum4j.core.backend;

import java.util.Collections;
import java.util.Map;

import io.quantum4j.core.math.StateVector;

/**
 * Immutable container for quantum circuit execution results.
 * <p>
 * Stores measurement outcome counts from multiple circuit executions (shots). Each key is a classical bit string (e.g.,
 * "110") and each value is the number of times that outcome was measured across all shots.
 * </p>
 */
public final class Result {
    private final Map<String, Integer> counts;
    private final BackendType backendType;
    private final StateVector stateVector;

    /**
     * Construct a Result from measurement counts.
     *
     * @param counts
     *            map of classical bit strings to outcome frequencies
     */
    public Result(Map<String, Integer> counts) {
        this(counts, BackendType.STATEVECTOR, null);
    }

    /**
     * Construct a Result from measurement counts and backend metadata.
     *
     * @param counts
     *            map of classical bit strings to outcome frequencies
     * @param backendType
     *            backend that produced this result
     * @param stateVector
     *            optional final statevector (may be null)
     */
    public Result(Map<String, Integer> counts, BackendType backendType, StateVector stateVector) {
        this.counts = Collections.unmodifiableMap(counts);
        this.backendType = backendType;
        this.stateVector = stateVector;
    }

    /**
     * Get the measurement outcome counts.
     *
     * @return immutable map of bit strings to outcome counts
     */
    public Map<String, Integer> getCounts() {
        return counts;
    }

    /**
     * Get the backend type used for execution.
     *
     * @return backend type
     */
    public BackendType getBackendType() {
        return backendType;
    }

    /**
     * Get the final statevector, if available.
     *
     * @return statevector or null
     */
    public StateVector getStateVector() {
        return stateVector;
    }

    @Override
    public String toString() {
        return "Result{counts=" + counts + ", backend=" + backendType + '}';
    }
}
