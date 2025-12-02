package com.quantum4j.examples;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.transpile.PassManager;
import com.quantum4j.transpile.passes.CXCancellationPass;
import com.quantum4j.transpile.passes.RotationFusionPass;
import com.quantum4j.transpile.passes.SwapDecompositionPass;
import com.quantum4j.transpile.passes.U3DecompositionPass;

/**
 * Demonstrates a simple transpiler pipeline.
 *
 * Passes: SwapDecompositionPass -> RotationFusionPass -> CXCancellationPass -> U3DecompositionPass
 */
public final class TranspilerPipelineExample {

    public static void main(String[] args) {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .swap(0, 1)
                .cx(0, 1)
                .cx(0, 1)
                .rz(0, 0.1)
                .rz(0, 0.2)
                .u3(1, 0.3, 0.4, 0.5)
                .measureAll();

        System.out.println("Original instructions:");
        print(circuit);

        QuantumCircuit out = new PassManager()
                .addPass(new SwapDecompositionPass())
                .addPass(new RotationFusionPass())
                .addPass(new CXCancellationPass())
                .addPass(new U3DecompositionPass())
                .run(circuit);

        System.out.println("\nAfter transpiler pipeline:");
        print(out);
    }

    private static void print(QuantumCircuit qc) {
        int idx = 0;
        for (Instruction inst : qc.getInstructions()) {
            System.out.printf("%2d: %s%n", idx++, describe(inst));
        }
    }

    private static String describe(Instruction inst) {
        if (inst.getType() == Instruction.Type.MEASURE) {
            return "MEASURE q[" + inst.getQubits()[0] + "] -> c[" + inst.getClassicalBits()[0] + "]";
        }
        return inst.getGate().name() + " " + formatQubits(inst.getQubits());
    }

    private static String formatQubits(int[] qs) {
        if (qs.length == 1) {
            return "q[" + qs[0] + "]";
        }
        if (qs.length == 2) {
            return "q[" + qs[0] + "], q[" + qs[1] + "]";
        }
        return "";
    }
}

