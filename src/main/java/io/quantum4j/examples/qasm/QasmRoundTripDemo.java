package com.quantum4j.examples.qasm;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.qasm.QasmExporter;
import com.quantum4j.qasm.QasmImporter;

/**
 * Demonstrates deterministic export/import/export round-trip of a circuit.
 */
public final class QasmRoundTripDemo {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(3)
                .h(0)
                .cx(0, 1)
                .u2(2, Math.PI / 4, Math.PI / 6)
                .measureAll();

        String qasm1 = QasmExporter.toQasm(qc);
        QuantumCircuit rt = QasmImporter.fromQasm(qasm1);
        String qasm2 = QasmExporter.toQasm(rt);

        System.out.println("First export:\n" + qasm1);
        System.out.println("Round-trip export:\n" + qasm2);
    }
}



