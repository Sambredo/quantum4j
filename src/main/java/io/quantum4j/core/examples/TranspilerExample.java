package io.quantum4j.core.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.transpile.PassManager;
import io.quantum4j.transpile.passes.CancelDoubleXPass;

public class TranspilerExample {

    public static void main(String[] args) {
        // Circuit: X(0); X(0); H(0); measureAll();
        QuantumCircuit qc = QuantumCircuit.create(1)
                .x(0)
                .x(0)
                .h(0)
                .measureAll();

        System.out.println("Original instructions: " + qc.getInstructions().size());

        PassManager pm = new PassManager()
                .addPass(new CancelDoubleXPass());

        QuantumCircuit optimized = pm.run(qc);

        System.out.println("Optimized instructions: " + optimized.getInstructions().size());

        Backend backend = new StateVectorBackend();
        Result r = backend.run(optimized, RunOptions.shots(1000));

        System.out.println("Counts (should still be 50/50 |0>, |1>): " + r.getCounts());
    }
}
