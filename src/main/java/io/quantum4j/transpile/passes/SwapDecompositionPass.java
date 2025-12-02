package com.quantum4j.transpile.passes;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.transpile.TranspilerPass;

import java.util.List;

/**
 * Decomposes SWAP gates into the canonical 3-CX sequence:
 * <pre>
 *     cx q[a], q[b]
 *     cx q[b], q[a]
 *     cx q[a], q[b]
 * </pre>
 */
public final class SwapDecompositionPass implements TranspilerPass {

    /**
     * Create the SWAP decomposition pass.
     */
    public SwapDecompositionPass() {
    }

    @Override
    public String name() {
        return "SwapDecompositionPass";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        if (circuit == null) {
            throw new IllegalArgumentException("circuit must not be null");
        }

        QuantumCircuit out = QuantumCircuit.create(circuit.getNumQubits());
        List<Instruction> instructions = circuit.getInstructions();

        for (Instruction inst : instructions) {
            if (inst.getType() == Instruction.Type.GATE && inst.getGate() instanceof StandardGates.SWAPGate) {
                int[] q = inst.getQubits();
                int a = q[0];
                int b = q[1];

                // SWAP(a,b) => CX(a,b); CX(b,a); CX(a,b)
                out.cx(a, b);
                out.cx(b, a);
                out.cx(a, b);
            } else {
                // Clone to honor instruction immutability across passes
                out.addInstruction(inst.copy());
            }
        }

        return out;
    }
}


