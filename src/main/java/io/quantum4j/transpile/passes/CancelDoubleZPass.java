package io.quantum4j.transpile.passes;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.Gate;
import io.quantum4j.core.gates.StandardGates;
import io.quantum4j.transpile.TranspilerPass;

import java.util.List;

/**
 * Removes adjacent pairs of Z gates on the same qubit.
 */
public final class CancelDoubleZPass implements TranspilerPass {

    /**
     * Create the double-Z cancellation pass.
     */
    public CancelDoubleZPass() {
    }

    @Override
    public String name() {
        return "cancel_z_z";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        QuantumCircuit out = QuantumCircuit.create(circuit.getNumQubits());
        List<Instruction> insts = circuit.getInstructions();

        for (int i = 0; i < insts.size(); i++) {
            Instruction inst = insts.get(i);

            if (isZ(inst) && i + 1 < insts.size()) {
                Instruction next = insts.get(i + 1);
                if (isZ(next) && sameTargetQubit(inst, next)) {
                    i++; // skip both Z gates
                    continue;
                }
            }

            out.addInstruction(inst);
        }

        return out;
    }

    private boolean isZ(Instruction inst) {
        if (inst.getType() != Instruction.Type.GATE) return false;
        Gate g = inst.getGate();
        return g instanceof StandardGates.ZGate;
    }

    private boolean sameTargetQubit(Instruction a, Instruction b) {
        int[] qa = a.getQubits();
        int[] qb = b.getQubits();
        return qa.length == 1 && qb.length == 1 && qa[0] == qb[0];
    }
}
