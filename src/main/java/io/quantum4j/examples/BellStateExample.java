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
 * Bell state demonstration.
 *
 * Expected output (approx):
 * Statevector:
 * |00>: 0.70710678
 * |11>: 0.70710678
 *
 * Counts (1000 shots):
 * 00: ~500
 * 11: ~500
 */
public final class BellStateExample {

    public static void main(String[] args) {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();

        // Show statevector before measurement
        StateVector sv = simulateStatevector(circuit);
        System.out.println("Statevector (before measurement):");
        for (int i = 0; i < sv.dimension(); i++) {
            double mag = Math.sqrt(sv.getAmplitudes()[i].absSquared());
            if (mag > 1e-9) {
                System.out.printf("|%2s>: %.8f%n", Integer.toBinaryString(4 + i).substring(1), mag);
            }
        }

        // Sample measurements
        Result result = circuit.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(1000));
        System.out.println("\nCounts (1000 shots): " + result.getCounts());

        // Export QASM
        String qasm = io.quantum4j.qasm.QasmExporter.toQasm(circuit);
        System.out.println("\nQASM:\n" + qasm);
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
