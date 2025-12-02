package com.quantum4j.examples.qasm;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.qasm.QasmExporter;
import com.quantum4j.qasm.QasmImporter;
import com.quantum4j.transpile.DefaultTranspiler;

/**
 * Imports QASM, runs the default transpiler, and exports back to QASM.
 */
public final class QasmTranspilerDemo {
    public static void main(String[] args) {
        String qasm = """
                OPENQASM 2.0;
                include "qelib1.inc";
                qreg q[2];
                creg c[2];
                swap q[0], q[1];
                u3(0.1, 0.2, 0.3) q[0];
                measure q[0] -> c[0];
                measure q[1] -> c[1];
                """;

        QuantumCircuit imported = QasmImporter.fromQasm(qasm);
        System.out.println("Imported circuit instructions: " + imported.getInstructions().size());

        QuantumCircuit transpiled = DefaultTranspiler.transpile(imported);
        String exported = QasmExporter.toQasm(transpiled);
        System.out.println("Transpiled and exported QASM:\n" + exported);
    }
}



