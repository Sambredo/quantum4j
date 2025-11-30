package io.quantum4j.examples;

import io.quantum4j.algorithms.QFT;
import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.Gate;
import io.quantum4j.core.gates.SingleQubitGate;
import io.quantum4j.core.math.StateVector;

/**
 * Demonstrates QFT on a few basis states and prints the resulting statevector amplitudes.
 */
public final class QFTDemo {
    public static void main(String[] args) {
        demoState("|001>", new int[]{0, 0, 1});
        demoState("|010>", new int[]{0, 1, 0});
        demoState("|111>", new int[]{1, 1, 1});
    }

    private static void demoState(String label, int[] bits) {
        QuantumCircuit prep = QuantumCircuit.create(3);
        for (int i = 0; i < bits.length; i++) {
            if (bits[i] == 1) prep.x(i);
        }

        QuantumCircuit circuit = QuantumCircuit.create(3);
        for (Instruction inst : prep.getInstructions()) {
            circuit.addInstruction(inst.copy());
        }
        for (Instruction inst : QFT.qft(3).getInstructions()) {
            circuit.addInstruction(inst.copy());
        }

        StateVector state = applyUnitary(circuit);
        System.out.println("QFT of " + label + " amplitudes:");
        printState(state);
        System.out.println();
    }

    // Minimal simulator (unitary only, no measurement)
    private static StateVector applyUnitary(QuantumCircuit qc) {
        StateVector sv = new StateVector(qc.getNumQubits());
        for (Instruction inst : qc.getInstructions()) {
            Gate g = inst.getGate();
            int[] q = inst.getQubits();
            if (g instanceof SingleQubitGate) {
                ((SingleQubitGate) g).apply(sv, q[0]);
            } else if (g instanceof io.quantum4j.core.gates.TwoQubitGate) {
                ((io.quantum4j.core.gates.TwoQubitGate) g).apply(sv, q[0], q[1]);
            } else if (g instanceof io.quantum4j.core.gates.ThreeQubitGate) {
                ((io.quantum4j.core.gates.ThreeQubitGate) g).apply(sv, q[0], q[1], q[2]);
            }
        }
        return sv;
    }

    private static void printState(StateVector sv) {
        for (int i = 0; i < sv.getAmplitudes().length; i++) {
            System.out.println(String.format("|%3s> : %s", Integer.toBinaryString(i | (1 << sv.getNumQubits())).substring(1), sv.getAmplitudes()[i]));
        }
    }
}
