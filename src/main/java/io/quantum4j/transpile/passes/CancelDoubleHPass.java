package io.quantum4j.transpile.passes;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.Gate;
import io.quantum4j.core.gates.StandardGates;
import io.quantum4j.transpile.TranspilerPass;

import java.util.List;

public final class CancelDoubleHPass implements TranspilerPass {

    @Override
    public String name() {
        return "cancel_h_h";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        QuantumCircuit out = QuantumCircuit.create(circuit.getNumQubits());
        List<Instruction> insts = circuit.getInstructions();

        for (int i = 0; i < insts.size(); i++) {
            Instruction inst = insts.get(i);

            if (isH(inst) && i + 1 < insts.size()) {
                Instruction next = insts.get(i + 1);
                if (isH(next) && sameTargetQubit(inst, next)) {
                    i++; // skip both H gates
                    continue;
                }
            }

            out.addInstruction(inst);
        }

        return out;
    }

    private boolean isH(Instruction inst) {
        if (inst.getType() != Instruction.Type.GATE) return false;
        Gate g = inst.getGate();
        return g instanceof StandardGates.HGate;
    }

    private boolean sameTargetQubit(Instruction a, Instruction b) {
        int[] qa = a.getQubits();
        int[] qb = b.getQubits();
        return qa.length == 1 && qb.length == 1 && qa[0] == qb[0];
    }
}
