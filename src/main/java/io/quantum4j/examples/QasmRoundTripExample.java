package io.quantum4j.examples;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.qasm.QasmExporter;
import io.quantum4j.qasm.QasmImporter;

/**
 * Demonstrates QASM export/import round-trip.
 */
public final class QasmRoundTripExample {

    public static void main(String[] args) {
        QuantumCircuit original = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();

        String qasm = QasmExporter.toQasm(original);
        QuantumCircuit imported = QasmImporter.fromQasm(qasm);

        boolean same = instructionsEqual(original, imported);
        System.out.println("Round-trip successful: " + same);
        System.out.println("\nExported QASM:\n" + qasm);
    }

    private static boolean instructionsEqual(QuantumCircuit a, QuantumCircuit b) {
        if (a.getInstructions().size() != b.getInstructions().size()) {
            return false;
        }
        for (int i = 0; i < a.getInstructions().size(); i++) {
            Instruction x = a.getInstructions().get(i);
            Instruction y = b.getInstructions().get(i);
            if (x.getType() != y.getType()) return false;
            if (x.getType() == Instruction.Type.GATE) {
                if (!x.getGate().name().equals(y.getGate().name())) return false;
            }
            int[] qx = x.getQubits();
            int[] qy = y.getQubits();
            if (qx.length != qy.length) return false;
            for (int k = 0; k < qx.length; k++) {
                if (qx[k] != qy[k]) return false;
            }
        }
        return true;
    }
}
