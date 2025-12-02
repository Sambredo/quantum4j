package com.quantum4j.examples.qasm;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.qasm.QasmExporter;

/**
 * Exports a small circuit (Bell + U3) to OpenQASM 2.0 and prints the result.
 */
public final class QasmExportDemo {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .u3(1, Math.PI / 3, Math.PI / 5, Math.PI / 7)
                .measureAll();

        String qasm = QasmExporter.toQasm(qc);
        System.out.println("Exported QASM:\n" + qasm);
    }
}



