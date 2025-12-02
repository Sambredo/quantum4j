package com.quantum4j.examples;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.circuit.Instruction;

/**
 * Basic circuit construction: create a circuit, add gates/measurements, and print instructions.
 */
public final class CircuitBasicsDemo {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();

        System.out.println("Circuit has " + qc.getNumQubits() + " qubits");
        System.out.println("Instructions:");
        for (Instruction inst : qc.getInstructions()) {
            System.out.println(" - " + inst.getType() + " " +
                    (inst.getType() == Instruction.Type.GATE ? inst.getGate().name() : "measure"));
        }
    }
}


