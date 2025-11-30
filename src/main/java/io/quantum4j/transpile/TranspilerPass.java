package io.quantum4j.transpile;

import io.quantum4j.core.circuit.QuantumCircuit;

/**
 * A transpiler pass that transforms a quantum circuit.
 *
 * <p>
 * Transpiler passes can perform optimizations, gate decompositions, qubit mapping, and other
 * transformations on quantum circuits to adapt them for specific hardware or improve performance.
 * Each pass takes an input QuantumCircuit and produces a new transformed QuantumCircuit.
 * </p>
 */
public interface TranspilerPass {

    /**
     * Human-readable name of this pass (for logging, debugging, profiling).
     *
     * @return pass identifier
     */
    String name();

    /**
     * Apply this pass to the given circuit and return a new circuit.
     * Implementations should not mutate the input circuit.
     *
     * @param circuit input circuit (not mutated)
     * @return transformed circuit
     */
    QuantumCircuit apply(QuantumCircuit circuit);
}
