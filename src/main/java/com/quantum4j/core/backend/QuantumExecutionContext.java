package com.quantum4j.core.backend;

import com.quantum4j.core.circuit.QuantumCircuit;

/**
 * Lightweight container describing an execution request.
 */
public final class QuantumExecutionContext {
    private final QuantumCircuit circuit;
    private final RunOptions options;

    public QuantumExecutionContext(QuantumCircuit circuit, RunOptions options) {
        this.circuit = circuit;
        this.options = options;
    }

    public QuantumCircuit getCircuit() {
        return circuit;
    }

    public RunOptions getOptions() {
        return options;
    }
}


