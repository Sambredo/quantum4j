package io.quantum4j.core.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;

public class ExampleU3Universal {

    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .u3(0, Math.PI/2, Math.PI/3, Math.PI)
                .measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(500));
        System.out.println("U3 universal rotation:");
        System.out.println(r.getCounts());
    }
}
