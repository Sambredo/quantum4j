package io.quantum4j.core.examples;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.qasm.QasmExporter;

public class U1U2U3Example {

    public static void main(String[] args) {

        QuantumCircuit qc = QuantumCircuit.create(1)
                .u1(0, Math.PI / 2)
                .u2(0, 0, Math.PI)
                .u3(0, Math.PI / 2, Math.PI / 2, Math.PI);

        String qasm = QasmExporter.toQasm(qc);

        System.out.println("=== U1/U2/U3 QASM ===");
        System.out.println(qasm);
    }
}
