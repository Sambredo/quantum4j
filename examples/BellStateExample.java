package io.quantum4j.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;

public class BellStateExample {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0,1)
                .measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(1000));
        System.out.println("Bell State Counts = " + r.getCounts());
    }
}
