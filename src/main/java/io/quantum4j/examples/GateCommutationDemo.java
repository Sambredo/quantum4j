package com.quantum4j.examples;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.transpile.PassManager;
import com.quantum4j.transpile.passes.GateCommutationPass;

/**
 * Demonstrates the GateCommutationPass by showing before/after instruction order for commuting gates.
 */
public final class GateCommutationDemo {
    public static void main(String[] args) {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .rz(0, 0.1)
                .rx(1, 0.2)
                .cx(0, 1)
                .h(0) // does not commute with cx on control
                .measureAll();

        System.out.println("Original order:");
        printInstructions(circuit);

        QuantumCircuit optimized = new PassManager()
                .addPass(new GateCommutationPass())
                .run(circuit);

        System.out.println("\nAfter GateCommutationPass:");
        printInstructions(optimized);
    }

    private static void printInstructions(QuantumCircuit qc) {
        int idx = 0;
        for (Instruction inst : qc.getInstructions()) {
            if (inst.getType() == Instruction.Type.GATE) {
                System.out.println(" [" + idx + "] GATE " + inst.getGate().name() + " q" + formatQubits(inst.getQubits()));
            } else {
                System.out.println(" [" + idx + "] MEASURE q" + inst.getQubits()[0] + " -> c" + inst.getClassicalBits()[0]);
            }
            idx++;
        }
    }

    private static String formatQubits(int[] qs) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < qs.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(qs[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}


