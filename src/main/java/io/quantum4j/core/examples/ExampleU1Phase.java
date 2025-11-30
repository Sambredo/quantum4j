package io.quantum4j.core.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;

public class ExampleU1Phase {

    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .h(0)
                .u1(0, Math.PI/2)
                .measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(500));

        System.out.println("U1 phase shift:");
        System.out.println(r.getCounts());
    }
}
