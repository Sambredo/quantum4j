package io.quantum4j.examples;

import io.quantum4j.core.backend.BackendType;
import io.quantum4j.core.backend.Result;
import io.quantum4j.core.backend.RunOptions;
import io.quantum4j.core.circuit.QuantumCircuit;

/**
 * Deutsch's algorithm demonstrating constant vs balanced oracle.
 *
 * Expected output (1000 shots):
 * Constant oracle -> counts on c0: {0=1000}
 * Balanced oracle -> counts on c0: {1=1000}
 */
public final class DeutschAlgorithmExample {

    public static void main(String[] args) {
        System.out.println("Constant oracle:");
        runOracle(false);
        System.out.println("\nBalanced oracle:");
        runOracle(true);
    }

    private static void runOracle(boolean balanced) {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .x(1)      // set |1> on target
                .h(0).h(1) // superposition
                .cx(0, 1)  // balanced oracle (CNOT) - remove later if constant
                .h(0)
                .measure(0, 0);

        if (!balanced) {
            // remove the oracle effect for constant function by not applying extra gates
            // In this simple form, leaving the CX has no effect if control is 0 after H? Actually balanced uses CX, constant does nothing.
            // To emulate constant, rebuild without the CX
            qc = QuantumCircuit.create(2)
                    .x(1)
                    .h(0).h(1)
                    .h(0)
                    .measure(0, 0);
        }

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(1000));
        System.out.println("Counts: " + r.getCounts());
    }
}
