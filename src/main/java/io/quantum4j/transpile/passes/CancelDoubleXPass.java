package com.quantum4j.transpile.passes;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.transpile.TranspilerPass;

/**
 * A transpiler pass that cancels consecutive pairs of X gates on the same qubit.
 */
public final class CancelDoubleXPass implements TranspilerPass {

    /**
     * Create the double-X cancellation pass.
     */
    public CancelDoubleXPass() {
    }

    @Override
    public String name() {
        return "cancel_x_x";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        QuantumCircuit out = QuantumCircuit.create(circuit.getNumQubits());

        var instructions = circuit.getInstructions();

        for (int i = 0; i < instructions.size(); i++) {
            Instruction inst = instructions.get(i);

            if (isSingleX(inst) && i + 1 < instructions.size()) {
                Instruction next = instructions.get(i + 1);

                if (isSingleX(next) && sameTargetQubit(inst, next)) {
                    // Skip both instructions X, X
                    i++; // consume next as well
                    continue;
                }
            }

            // Otherwise keep the instruction as-is
            out.addInstruction(inst);
        }

        return out;
    }

    private boolean isSingleX(Instruction inst) {
        if (inst.getType() != Instruction.Type.GATE) {
            return false;
        }
        Gate g = inst.getGate();
        return g instanceof StandardGates.XGate;
    }

    private boolean sameTargetQubit(Instruction a, Instruction b) {
        int[] qa = a.getQubits();
        int[] qb = b.getQubits();
        return qa.length == 1 && qb.length == 1 && qa[0] == qb[0];
    }
}


