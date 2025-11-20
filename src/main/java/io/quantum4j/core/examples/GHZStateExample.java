package io.quantum4j.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;

public class GHZStateExample {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(3).h(0).cx(0, 1).cx(1, 2).measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(1000));
        System.out.println("GHZ Counts = " + r.getCounts());
    }
}
