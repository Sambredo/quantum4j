package io.quantum4j.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;

public class PhaseShiftExample {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .h(0)
                .s(0)
                .t(0)
                .rz(0, Math.PI / 4)
                .measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(1000));
        System.out.println("Phase Shift Example = " + r.getCounts());
    }
}
