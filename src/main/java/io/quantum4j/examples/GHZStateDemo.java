package io.quantum4j.examples;

import io.quantum4j.core.backend.BackendType;
import io.quantum4j.core.backend.Result;
import io.quantum4j.core.backend.RunOptions;
import io.quantum4j.core.circuit.QuantumCircuit;

/**
 * Builds a 3-qubit GHZ state and measures it.
 */
public final class GHZStateDemo {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(3)
                .h(0)
                .cx(0, 1)
                .cx(0, 2)
                .measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(400));
        System.out.println("GHZ counts (approx 50/50 000 and 111): " + r.getCounts());
    }
}

