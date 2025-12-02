package com.quantum4j.examples;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.qasm.QasmExporter;
import com.quantum4j.qasm.QasmImporter;

/**
 * Demonstrates exporting a circuit to OpenQASM 2.0 and importing it back.
 */
public final class QasmRoundTripDemo {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();

        String qasm = QasmExporter.toQasm(qc);
        System.out.println("Exported QASM:\n" + qasm);

        QuantumCircuit roundTrip = QasmImporter.fromQasm(qasm);
        System.out.println("Round-trip instructions: " + roundTrip.getInstructions().size());
    }
}


