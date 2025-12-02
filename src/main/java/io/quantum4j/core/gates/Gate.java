package com.quantum4j.core.gates;

import com.quantum4j.core.math.StateVector;

/**
 * Interface representing a quantum gate.
 * <p>
 * A gate is a unitary operation that transforms quantum states. Every gate has a name and an arity (number of qubits it
 * acts upon).
 * </p>
 */
public interface Gate {
    /**
     * Get the name of this gate (e.g., "H", "X", "CNOT").
     *
     * @return the gate name
     */
    String name();

    /**
     * Get the arity (number of qubits) this gate acts upon.
     *
     * @return 1 for single-qubit gates, 2 for two-qubit gates, etc.
     */
    int arity(); // number of qubits this gate acts on
}


