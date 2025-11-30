package io.quantum4j.transpile.passes;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.transpile.TranspilerPass;

import java.util.List;

/**
 * Transpiler pass that decomposes CX/CNOT into:
 *
 * H(target) + CZ(control, target) + H(target)
 *
 * This pass is intentionally robust: it detects CNOT even if the gate did not originate from
 * {@link io.quantum4j.core.gates.StandardGates.CNOTGate}.
 */
public final class CXToCZDecompositionPass implements TranspilerPass {

    /**
     * Create the CX-to-CZ decomposition pass.
     */
    public CXToCZDecompositionPass() {
    }

    @Override
    public String name() {
        return "CXToCZDecompositionPass";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        if (circuit == null) {
            throw new IllegalArgumentException("circuit must not be null");
        }

        QuantumCircuit out = QuantumCircuit.create(circuit.getNumQubits());
        List<Instruction> instructions = circuit.getInstructions();

        for (Instruction inst : instructions) {

            // Preserve measurement instructions exactly
            if (inst.getType() == Instruction.Type.MEASURE) {
                out.measure(inst.getQubits()[0], inst.getClassicalBits()[0]);
                continue;
            }

            // Gate instructions — detect CNOT robustly
            if (inst.getType() == Instruction.Type.GATE && inst.getGate() != null) {

                String gateName = inst.getGate().name().toLowerCase();

                boolean isCX =
                        gateName.equals("cx")  ||
                        gateName.equals("cnot");   // robust detection for imported circuits

                if (isCX) {
                    int[] q = inst.getQubits();
                    int control = q[0];
                    int target = q[1];

                    // CX(c,t) => H(t), CZ(c,t), H(t)
                    out.h(target);
                    out.cz(control, target);
                    out.h(target);
                    continue;
                }
            }

            // Copy all other instructions unchanged
            out.addInstruction(inst);
        }

        return out;
    }
}

