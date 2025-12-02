package com.quantum4j.core.backend;

/**
 * Configuration for quantum circuit execution.
 * <p>
 * Specifies execution parameters such as the number of measurement shots (repeated circuit runs) to collect statistics
 * on measurement outcomes.
 * </p>
 */
public final class RunOptions {
    private int shots;
    private BackendType backendType = BackendType.STATEVECTOR;

    private RunOptions(int shots) {
        setShots(shots);
    }

    private RunOptions() {
        this.shots = 1;
    }

    /**
     * Create RunOptions with the specified number of shots.
     *
     * @param shots
     *            number of times to measure the circuit (must be &gt; 0)
     *
     * @return RunOptions configured with the given shot count
     *
     * @throws IllegalArgumentException
     *             if shots &lt;= 0
     */
    public static RunOptions shots(int shots) {
        return new RunOptions(shots);
    }

    /**
     * Create RunOptions selecting a backend.
     *
     * @param type
     *            backend type
     *
     * @return RunOptions configured with the given backend
     */
    public static RunOptions withBackend(BackendType type) {
        RunOptions opt = new RunOptions();
        opt.backendType = type;
        return opt;
    }

    /**
     * Get the number of shots for this execution.
     *
     * @return the shot count
     */
    public int getShots() {
        return shots;
    }

    /**
     * Set the number of shots for this execution.
     *
     * @param shots
     *            number of repetitions (must be &gt; 0)
     * @return this RunOptions for chaining
     */
    public RunOptions withShots(int shots) {
        setShots(shots);
        return this;
    }

    /**
     * Get the backend type to use for execution.
     *
     * @return backend type
     */
    public BackendType getBackendType() {
        return backendType;
    }

    private void setShots(int shots) {
        if (shots <= 0) {
            throw new IllegalArgumentException("shots must be > 0");
        }
        this.shots = shots;
    }
}


