package io.quantum4j.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;

public class ToffoliExample {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(3)
                .x(0)
                .x(1)
                .ccx(0,1,2)
                .measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(500));
        System.out.println("Toffoli Counts = " + r.getCounts());
    }
}
