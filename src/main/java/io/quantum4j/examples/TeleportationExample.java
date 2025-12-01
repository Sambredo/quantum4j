package io.quantum4j.examples;

import io.quantum4j.core.backend.BackendType;
import io.quantum4j.core.backend.Result;
import io.quantum4j.core.backend.RunOptions;
import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.SingleQubitGate;
import io.quantum4j.core.gates.TwoQubitGate;
import io.quantum4j.core.math.StateVector;

/**
 * Teleportation of |psi> = H|0> to qubit 2 using coherent corrections.
 *
 * Expected output (approx):
 * Target qubit amplitudes ~ |+> on qubit2
 * Counts (1000 shots): target bit mostly 0 and 1 equally after measurement.
 */
public final class TeleportationExample {

    public static void main(String[] args) {
        // Prepare |psi> on qubit0 = |+>
        QuantumCircuit circuit = QuantumCircuit.create(3)
                .h(0)
                // Create Bell pair between qubit1 and qubit2
                .h(1)
                .cx(1, 2)
                // Entangle message with Bell pair
                .cx(0, 1)
                .h(0)
                // Coherent corrections (controlled by qubits 0 and 1)
                .cz(0, 2)
                .cx(1, 2)
                // Optional measurements of sender qubits
                .measure(0, 0)
                .measure(1, 1)
                .measure(2, 2);

        StateVector sv = simulateStatevector(circuit);
        System.out.println("Statevector (before measurement):");
        for (int i = 0; i < sv.dimension(); i++) {
            double mag = Math.sqrt(sv.getAmplitudes()[i].absSquared());
            if (mag > 1e-9) {
                System.out.printf("|%3s>: %.8f%n", Integer.toBinaryString(8 + i).substring(1), mag);
            }
        }

        Result result = circuit.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(1000));
        System.out.println("\nCounts (1000 shots): " + result.getCounts());
    }

    private static StateVector simulateStatevector(QuantumCircuit circuit) {
        StateVector sv = new StateVector(circuit.getNumQubits());
        for (Instruction inst : circuit.getInstructions()) {
            if (inst.getType() != Instruction.Type.GATE) {
                continue;
            }
            if (inst.getGate() instanceof SingleQubitGate g1) {
                g1.apply(sv, inst.getQubits()[0]);
            } else if (inst.getGate() instanceof TwoQubitGate g2) {
                g2.apply(sv, inst.getQubits()[0], inst.getQubits()[1]);
            }
        }
        return sv;
    }
}
