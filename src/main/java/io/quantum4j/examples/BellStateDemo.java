package com.quantum4j.examples;

import com.quantum4j.core.backend.BackendType;
import com.quantum4j.core.backend.Result;
import com.quantum4j.core.backend.RunOptions;
import com.quantum4j.core.circuit.QuantumCircuit;

/**
 * Creates a Bell state |Φ+> and measures it to demonstrate entanglement.
 */
public final class BellStateDemo {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(400));
        System.out.println("Bell state counts (approx 50/50 00 and 11): " + r.getCounts());
    }
}


