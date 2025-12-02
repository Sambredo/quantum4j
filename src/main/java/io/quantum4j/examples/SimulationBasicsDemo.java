package com.quantum4j.examples;

import com.quantum4j.core.backend.BackendType;
import com.quantum4j.core.backend.Result;
import com.quantum4j.core.backend.RunOptions;
import com.quantum4j.core.circuit.QuantumCircuit;

/**
 * Demonstrates running a simple circuit, collecting counts, and inspecting the state before measurement.
 */
public final class SimulationBasicsDemo {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();

        Result result = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));
        System.out.println("Counts for Bell state (approx. 50/50): " + result.getCounts());
    }
}


