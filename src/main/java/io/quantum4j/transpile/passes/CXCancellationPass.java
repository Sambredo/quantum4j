package io.quantum4j.transpile.passes;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.StandardGates;
import io.quantum4j.transpile.TranspilerPass;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes consecutive pairs of identical CX gates (same control and target).
 * Even counts are eliminated; odd counts leave a single CX.
 */
/**
 * Peephole optimization:
 *
 *   CX(a,b); CX(a,b)   ==>   (removed)
 *
 * Used to reduce circuit depth and simplify transpilation before routing.
 * This pass is idempotent and preserves semantic equivalence.
 */

public final class CXCancellationPass implements TranspilerPass {

    /**
     * Create the CX cancellation pass.
     */
    public CXCancellationPass() {
    }

    @Override
    public String name() {
        return "CXCancellationPass";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        if (circuit == null) {
            throw new IllegalArgumentException("circuit must not be null");
        }

        List<Instruction> out = new ArrayList<>();

        for (Instruction inst : circuit.getInstructions()) {
            if (isCX(inst)) {
                int[] q = inst.getQubits();
                if (!out.isEmpty()) {
                    Instruction prev = out.get(out.size() - 1);
                    if (isCX(prev)) {
                        int[] pq = prev.getQubits();
                        if (pq[0] == q[0] && pq[1] == q[1]) {
                            // cancel the pair
                            out.remove(out.size() - 1);
                            continue;
                        }
                    }
                }
                out.add(inst.copy());
            } else {
                out.add(inst.copy());
            }
        }

        QuantumCircuit result = QuantumCircuit.create(circuit.getNumQubits());
        for (Instruction inst : out) {
            result.addInstruction(inst);
        }
        return result;
    }

    private boolean isCX(Instruction inst) {
        return inst.getType() == Instruction.Type.GATE
                && inst.getGate() instanceof StandardGates.CNOTGate;
    }
}

