package com.quantum4j.examples.qasm;

import com.quantum4j.qasm.QasmImporter;
import com.quantum4j.qasm.QasmImporter.QasmParseException;

/**
 * Shows how invalid QASM inputs are rejected with clear exceptions.
 */
public final class QasmErrorHandlingDemo {
    public static void main(String[] args) {
        String[] badSamples = new String[] {
                "OPENQASM 2.0; qreg q[1]; h q[0] // missing semicolon",
                "OPENQASM 2.0; include \"qelib1.inc\"; qreg q[1]; u1() q[0];",
                "OPENQASM 2.0; include \"qelib1.inc\"; qreg q[1]; cx q[0] -] q[1];",
                "OPENQASM 2.0; include \"qelib1.inc\"; qreg 1q[2];"
        };

        for (String bad : badSamples) {
            try {
                QasmImporter.fromQasm(bad);
                System.out.println("Unexpectedly succeeded: " + bad);
            } catch (QasmParseException | IllegalArgumentException ex) {
                System.out.println("Caught expected error: " + ex.getMessage());
            }
        }
    }
}


