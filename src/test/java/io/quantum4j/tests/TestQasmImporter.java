package io.quantum4j.tests;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.qasm.QasmExporter;
import io.quantum4j.qasm.QasmImporter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestQasmImporter {

    // ---------------------------------------------------------
    // 1. Basic import for simple 1-qubit gates
    // ---------------------------------------------------------
    @Test
    public void testImportBasicGates() {
        String qasm =
                "OPENQASM 2.0;\n" +
                "include \"qelib1.inc\";\n" +
                "qreg q[1];\n" +
                "creg c[1];\n" +
                "x q[0];\n" +
                "h q[0];\n" +
                "measure q[0] -> c[0];\n";

        QuantumCircuit qc = QasmImporter.fromQasm(qasm);

        assertEquals(1, qc.getNumQubits(), "Should have 1 qubit");
        assertEquals(3, qc.getInstructions().size(), "Should parse 3 operations");
    }

    // ---------------------------------------------------------
    // 2. Test U1/U2/U3 parameter parsing
    // ---------------------------------------------------------
    @Test
    public void testImportU1U2U3() {
        String qasm =
                "OPENQASM 2.0;\n" +
                "include \"qelib1.inc\";\n" +
                "qreg q[1];\n" +
                "creg c[1];\n" +
                "u1(1.5708) q[0];\n" +
                "u2(0.0, 3.14159) q[0];\n" +
                "u3(1.0, 2.0, 3.0) q[0];\n" +
                "measure q[0] -> c[0];\n";

        QuantumCircuit qc = QasmImporter.fromQasm(qasm);

        assertEquals(4, qc.getInstructions().size());
    }

    // ---------------------------------------------------------
    // 3. 2-qubit gates and 3-qubit gate
    // ---------------------------------------------------------
    @Test
    public void testTwoAndThreeQubitGates() {
        String qasm =
                "OPENQASM 2.0;\n" +
                "include \"qelib1.inc\";\n" +
                "qreg q[3];\n" +
                "creg c[3];\n" +
                "cx q[0], q[1];\n" +
                "cz q[1], q[2];\n" +
                "swap q[0], q[2];\n" +
                "ccx q[0], q[1], q[2];\n" +
                "measure q[0] -> c[0];\n";

        QuantumCircuit qc = QasmImporter.fromQasm(qasm);

        assertEquals(5, qc.getInstructions().size(), "Parsed gate count mismatch");
    }

    // ---------------------------------------------------------
    // 4. Round-trip test: Export → Import → Simulate
    // ---------------------------------------------------------
    @Test
    public void testQasmRoundTripBellState() {
        QuantumCircuit original = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();

        String qasm = QasmExporter.toQasm(original);
        QuantumCircuit imported = QasmImporter.fromQasm(qasm);

        Result r = imported.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(500));

        // Expect |00> and |11> only
        assertTrue(r.getCounts().containsKey("00"));
        assertTrue(r.getCounts().containsKey("11"));
        assertEquals(0, r.getCounts().keySet().stream().filter(k -> !k.equals("00") && !k.equals("11")).count());
    }

    // ---------------------------------------------------------
    // 5. Invalid QASM should throw error
    // ---------------------------------------------------------
    @Test
    public void testInvalidQasmThrows() {
        String badQasm =
                "OPENQASM 2.0;\n" +
                "include \"qelib1.inc\";\n" +
                "qreg q[1]\n" + // MISSING SEMICOLON
                "x q[0];\n";

        assertThrows(QasmImporter.QasmParseException.class,
                () -> QasmImporter.fromQasm(badQasm));
    }

}
