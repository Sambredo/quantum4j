package com.quantum4j.examples;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.transpile.PassManager;
import com.quantum4j.transpile.passes.CXCancellationPass;
import com.quantum4j.transpile.passes.GateInversionPass;
import com.quantum4j.transpile.passes.RotationFusionPass;
import com.quantum4j.transpile.passes.SwapDecompositionPass;
import com.quantum4j.transpile.passes.U3DecompositionPass;

/**
 * Demonstrates running individual transpiler passes and printing before/after instruction counts.
 */
public final class TranspilerPassesDemo {
    public static void main(String[] args) {
        QuantumCircuit original = QuantumCircuit.create(2)
                .swap(0, 1)
                .cx(0, 1)
                .cx(0, 1)
                .rz(0, 0.1)
                .rz(0, 0.2)
                .u3(1, 0.3, 0.4, 0.5)
                .measureAll();

        System.out.println("Original instruction count: " + original.getInstructions().size());

        QuantumCircuit decomposed = new PassManager()
                .addPass(new SwapDecompositionPass())
                .addPass(new CXCancellationPass())
                .addPass(new U3DecompositionPass())
                .addPass(new RotationFusionPass())
                .run(original);
        System.out.println("After decomposition/optimization: " + decomposed.getInstructions().size());

        QuantumCircuit inverted = new PassManager()
                .addPass(new GateInversionPass())
                .run(original);
        System.out.println("Inverted (unitary prefix reversed) count: " + inverted.getInstructions().size());

        System.out.println("Decomposed instruction sequence:");
        for (Instruction i : decomposed.getInstructions()) {
            System.out.println(" - " + i.getType() + " " + (i.getType() == Instruction.Type.GATE ? i.getGate().name() : "measure"));
        }
    }
}



