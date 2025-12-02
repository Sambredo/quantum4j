package com.quantum4j.core.backend;

import com.quantum4j.core.circuit.QuantumCircuit;

/**
 * Abstraction for executing quantum circuits.
 * <p>
 * Implementations must be stateless and thread-safe.
 * </p>
 */
public interface Backend {

    /**
     * Execute the given circuit with options.
     *
     * @param circuit circuit to run
     * @param options execution options
     * @return execution result
     */
    Result run(QuantumCircuit circuit, RunOptions options);
}


