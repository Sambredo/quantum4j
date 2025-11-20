package io.quantum4j.examples;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.qasm.QasmExporter;

public class QasmExportExample {
    public static void main(String[] args) {

        QuantumCircuit qc = QuantumCircuit.create(2).h(0).cx(0, 1).measureAll();

        String qasm = QasmExporter.toQasm(qc);
        System.out.println("Generated QASM:\n" + qasm);
    }
}
