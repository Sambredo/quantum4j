package com.quantum4j.examples;

import com.quantum4j.algorithms.QFT;
import com.quantum4j.core.backend.BackendType;
import com.quantum4j.core.backend.Result;
import com.quantum4j.core.backend.RunOptions;
import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;

/**
 * Shows that applying QFT followed by inverse QFT returns the original basis state.
 */
public final class InverseQFTDemo {
    public static void main(String[] args) {
        QuantumCircuit circuit = QuantumCircuit.create(3);
        // prepare |101>
        circuit.x(0).x(2);
        for (Instruction inst : QFT.qft(3).getInstructions()) {
            circuit.addInstruction(inst.copy());
        }
        for (Instruction inst : QFT.inverseQft(3).getInstructions()) {
            circuit.addInstruction(inst.copy());
        }
        circuit.measureAll();

        Result r = circuit.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));
        System.out.println("Counts after QFT + IQFT on |101>: " + r.getCounts());
    }
}

