package io.quantum4j.examples.qasm;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.qasm.QasmImporter;

/**
 * Imports a raw OpenQASM 2.0 string and prints the resulting instruction list.
 */
public final class QasmImportDemo {
    public static void main(String[] args) {
        String qasm = """
                OPENQASM 2.0;
                include "qelib1.inc";
                qreg q[2];
                creg c[2];
                h q[0];
                cx q[0], q[1];
                measure q[0] -> c[0];
                measure q[1] -> c[1];
                """;

        QuantumCircuit qc = QasmImporter.fromQasm(qasm);
        System.out.println("Imported instructions:");
        int idx = 0;
        for (Instruction inst : qc.getInstructions()) {
            System.out.println(" [" + idx++ + "] " + inst.getType() +
                    (inst.getType() == Instruction.Type.GATE ? " " + inst.getGate().name() : " measure"));
        }
    }
}

