package com.quantum4j.examples;

import com.quantum4j.core.backend.BackendType;
import com.quantum4j.core.backend.Result;
import com.quantum4j.core.backend.RunOptions;
import com.quantum4j.core.circuit.QuantumCircuit;

/**
 * Two-qubit Grover search marking |11> with a single iteration.
 *
 * Expected counts (1000 shots, approximate):
 * 11: ~850-950
 * others: small
 */
public final class GroverExample {

    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(2)
                // initialize superposition
                .h(0).h(1)
                // oracle: phase flip on |11>
                .cz(0, 1)
                // diffusion operator
                .h(0).h(1)
                .x(0).x(1)
                .cz(0, 1)
                .x(0).x(1)
                .h(0).h(1)
                .measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(1000));
        System.out.println("Grover counts (1000 shots): " + r.getCounts());
    }
}


