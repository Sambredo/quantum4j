package com.quantum4j.examples;

import com.quantum4j.algorithms.QFT;
import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.SingleQubitGate;
import com.quantum4j.core.gates.TwoQubitGate;
import com.quantum4j.core.gates.ThreeQubitGate;
import com.quantum4j.core.math.StateVector;

/**
 * QFT(3) amplitudes for basis states |001>, |010>, |111>.
 *
 * Expected (magnitudes):
 * - Uniform magnitude 1/sqrt(8) across all basis states for each input, but with input-dependent phases.
 */
public final class QFTExample {

    public static void main(String[] args) {
        runExample("001", new int[]{0});
        runExample("010", new int[]{1});
        runExample("111", new int[]{0, 1, 2});
    }

    private static void runExample(String label, int[] ones) {
        QuantumCircuit base = QuantumCircuit.create(3);
        for (int q : ones) {
            base.x(q);
        }
        QuantumCircuit full = QuantumCircuit.create(3);
        for (Instruction inst : base.getInstructions()) {
            full.addInstruction(inst.copy());
        }
        for (Instruction inst : QFT.qft(3).getInstructions()) {
            full.addInstruction(inst.copy());
        }

        StateVector sv = apply(full);
        System.out.println("QFT(3) applied to |" + label + ">:");
        for (int i = 0; i < sv.dimension(); i++) {
            double mag = Math.sqrt(sv.getAmplitudes()[i].absSquared());
            if (mag > 1e-4) {
                System.out.printf("|%3s>: %.8f%n", Integer.toBinaryString(8 + i).substring(1), mag);
            }
        }
        System.out.println();
    }

    private static StateVector apply(QuantumCircuit qc) {
        StateVector sv = new StateVector(qc.getNumQubits());
        for (Instruction inst : qc.getInstructions()) {
            if (inst.getType() != Instruction.Type.GATE) {
                continue;
            }
            if (inst.getGate() instanceof SingleQubitGate g1) {
                g1.apply(sv, inst.getQubits()[0]);
            } else if (inst.getGate() instanceof TwoQubitGate g2) {
                g2.apply(sv, inst.getQubits()[0], inst.getQubits()[1]);
            } else if (inst.getGate() instanceof ThreeQubitGate g3) {
                g3.apply(sv, inst.getQubits()[0], inst.getQubits()[1], inst.getQubits()[2]);
            }
        }
        return sv;
    }
}


