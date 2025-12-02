package com.quantum4j.examples;

import com.quantum4j.core.backend.BackendType;
import com.quantum4j.core.backend.Result;
import com.quantum4j.core.backend.RunOptions;
import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.SingleQubitGate;
import com.quantum4j.core.gates.TwoQubitGate;
import com.quantum4j.core.math.StateVector;

/**
 * GHZ state demonstration on 3 qubits.
 *
 * Expected output (approx):
 * Statevector:
 * |000>: 0.70710678
 * |111>: 0.70710678
 *
 * Counts (1000 shots):
 * 000: ~500
 * 111: ~500
 */
public final class GHZExample {

    public static void main(String[] args) {
        QuantumCircuit circuit = QuantumCircuit.create(3)
                .h(0)
                .cx(0, 1)
                .cx(0, 2)
                .measureAll();

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


