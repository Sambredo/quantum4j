package io.quantum4j.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;

public class RotationExample {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(1).rx(0, Math.PI / 3).ry(0, Math.PI / 4).rz(0, Math.PI / 5)
                .measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(500));
        System.out.println("Rotation Example = " + r.getCounts());
    }
}
