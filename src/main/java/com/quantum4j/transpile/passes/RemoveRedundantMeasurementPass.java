package com.quantum4j.transpile.passes;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.transpile.TranspilerPass;

import java.util.List;

public final class RemoveRedundantMeasurementPass implements TranspilerPass {

    @Override
    public String name() {
        return "remove_redundant_measure";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        QuantumCircuit out = QuantumCircuit.create(circuit.getNumQubits());
        List<Instruction> insts = circuit.getInstructions();

        // track which (qubit, classicalBit) pairs have already been measured
        boolean[][] seen = new boolean[circuit.getNumQubits()][circuit.getNumQubits()];

        for (Instruction inst : insts) {
            if (inst.getType() == Instruction.Type.MEASURE) {
                int q = inst.getQubits()[0];
                int c = inst.getClassicalBits()[0];

                if (seen[q][c]) {
                    // redundant measure → skip
                    continue;
                }
                seen[q][c] = true;
            }

            out.addInstruction(inst);
        }

        return out;
    }
}


