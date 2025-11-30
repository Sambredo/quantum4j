package io.quantum4j.core.examples;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.qasm.QasmExporter;
import io.quantum4j.qasm.QasmImporter;

public class QasmRoundTripExample {

    public static void main(String[] args) {

        QuantumCircuit original = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();

        String qasm = QasmExporter.toQasm(original);

        System.out.println("=== EXPORTED QASM ===");
        System.out.println(qasm);

        QuantumCircuit imported = QasmImporter.fromQasm(qasm);

        Backend backend = new StateVectorBackend();
        Result r = backend.run(imported, RunOptions.shots(1000));

        System.out.println("=== IMPORTED CIRCUIT RESULT ===");
        System.out.println(r.getCounts());
    }
}
