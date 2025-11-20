package io.quantum4j.core.backend;

import io.quantum4j.core.circuit.QuantumCircuit;

/**
 * Interface for quantum circuit execution backends.
 * <p>
 * A Backend is responsible for simulating or executing a quantum circuit and returning measurement results.
 * Implementations may use different simulation strategies (state-vector, density matrix, etc.) or interface with real
 * quantum hardware.
 * </p>
 */
public interface Backend {
    /**
     * Execute a quantum circuit on this backend.
     *
     * @param circuit
     *            the quantum circuit to execute
     * @param options
     *            execution options (e.g., number of shots)
     *
     * @return measurement results aggregated over all shots
     */
    Result run(QuantumCircuit circuit, RunOptions options);
}
