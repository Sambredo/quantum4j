package io.quantum4j.core.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;

public class ExampleU2VsH {

    public static void main(String[] args) {

        QuantumCircuit qc = QuantumCircuit.create(1)
                .u2(0, 0, Math.PI)   // U2(0, π)
                .measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(500));

        System.out.println("U2(0,π) ≈ Hadamard:");
        System.out.println(r.getCounts());
    }
}
