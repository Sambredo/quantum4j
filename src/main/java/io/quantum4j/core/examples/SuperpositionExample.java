package io.quantum4j.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;

public class SuperpositionExample {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(3).h(0).h(1).h(2).measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(2000));
        System.out.println("Superposition = " + r.getCounts());
    }
}
