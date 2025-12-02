package com.quantum4j.tests;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.qasm.QasmExporter;
import com.quantum4j.qasm.QasmImporter;
import com.quantum4j.qasm.QasmImporter.QasmParseException;
import com.quantum4j.transpile.PassManager;
import com.quantum4j.transpile.passes.CXCancellationPass;
import com.quantum4j.transpile.passes.RotationFusionPass;
import com.quantum4j.transpile.passes.SwapDecompositionPass;
import com.quantum4j.transpile.passes.U3DecompositionPass;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Compliance tests for OpenQASM 2.0 import/export round-trip behavior.
 */
class QasmComplianceV20Test {

    private QuantumCircuit roundTrip(QuantumCircuit circuit) {
        String qasm = QasmExporter.toQasm(circuit);
        return QasmImporter.fromQasm(qasm);
    }

    private String exportCompact(QuantumCircuit circuit) {
        return QasmExporter.toQasm(circuit)
                .replace("\r", "")
                .replace("\n", "")
                .replace(" ", "")
                .trim();
    }

    private void assertInstructionListsEqual(QuantumCircuit a, QuantumCircuit b) {
        List<Instruction> ia = a.getInstructions();
        List<Instruction> ib = b.getInstructions();
        assertEquals(ia.size(), ib.size(), "Instruction size mismatch");
        for (int i = 0; i < ia.size(); i++) {
            Instruction x = ia.get(i);
            Instruction y = ib.get(i);
            assertEquals(x.getType(), y.getType(), "Type mismatch at " + i);
            if (x.getType() == Instruction.Type.GATE) {
                assertEquals(x.getGate().name(), y.getGate().name(), "Gate name mismatch at " + i);
                assertArrayEquals(x.getQubits(), y.getQubits(), "Qubit mismatch at " + i);
            } else {
                assertArrayEquals(x.getQubits(), y.getQubits(), "Measure qubit mismatch at " + i);
                assertArrayEquals(x.getClassicalBits(), y.getClassicalBits(), "Classical bits mismatch at " + i);
            }
        }
    }

    // (A) Header & include compliance
    @Test
    void headerAndIncludeRoundTrip() {
        QuantumCircuit qc = QuantumCircuit.create(1).h(0).measureAll();
        QuantumCircuit rt = roundTrip(qc);
        assertInstructionListsEqual(qc, rt);
    }

    @Test
    void importIgnoresCaseWhitespaceAndComments() {
        String qasm = "OPENQASM 2.0;\n" +
                "include \"qelib1.inc\";\n" +
                "// comment\n" +
                "qreg q[1];\n" +
                "creg c[1];\n" +
                "  H   q[0];\n" +
                "// after gate\n" +
                "measure q[0] -> c[0];\n";
        QuantumCircuit qc = QasmImporter.fromQasm(qasm);
        assertEquals(2, qc.getInstructions().size());
    }

    @Test
    void exporterFormatCompliance() {
        QuantumCircuit qc = QuantumCircuit.create(1).h(0).measure(0, 0);

        String qasm = QasmExporter.toQasm(qc).trim();

        assertTrue(qasm.startsWith("OPENQASM 2.0;"), "Must start with version header");
        assertTrue(qasm.contains("include \"qelib1.inc\";"), "Must include standard library");
        assertTrue(qasm.contains("qreg q[1];"), "qreg must be emitted");
        assertTrue(qasm.contains("creg c[1];"), "creg must be emitted");
    }

    // (B) qreg / creg tests
    @Test
    void validRegisterDeclarations() {
        String qasm = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[5];\ncreg c[5];\nh q[0];\n";
        QuantumCircuit qc = QasmImporter.fromQasm(qasm);
        assertEquals(1, qc.getInstructions().size());
    }

    @Test
    void invalidRegisterNameThrows() {
        String bad = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg 1q[2];\n";
        assertThrows(QasmParseException.class, () -> QasmImporter.fromQasm(bad));
    }

    @Test
    void invalidRegisterSizeThrows() {
        String bad = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[-1];\n";
        assertThrows(QasmParseException.class, () -> QasmImporter.fromQasm(bad));
    }

    // (C) Gate syntax compliance
    @Test
    void validGateSyntaxVariants() {
        String qasm = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[2];\n" +
                "h q[0];\n" +
                "x q[1];\n" +
                "cx q[0],q[1];\n" +
                "u1(0.1) q[0];\n" +
                "u2(0.2,0.3) q[0];\n" +
                "u3(0.4,0.5,0.6) q[0];\n";
        QuantumCircuit qc = QasmImporter.fromQasm(qasm);
        assertEquals(6, qc.getInstructions().size());
    }

    @Test
    void gateSyntaxErrorsThrow() {
        String missingParam = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[1];\nu1() q[0];\n";
        String wrongParen = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[1];\nu2(0.1,0.2 q[0];\n";
        String wrongArity = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[2];\ncx q[0];\n";
        assertThrows(QasmParseException.class, () -> QasmImporter.fromQasm(missingParam));
        assertThrows(QasmParseException.class, () -> QasmImporter.fromQasm(wrongParen));
        assertThrows(QasmParseException.class, () -> QasmImporter.fromQasm(wrongArity));
    }

    // (D) Measurement semantics
    @Test
    void measurementRoundTrip() {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .measure(0, 1)
                .rz(1, 0.2)
                .measure(1, 0);
        assertInstructionListsEqual(qc, roundTrip(qc));
    }

    @Test
    void invalidMeasureThrows() {
        String bad = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[1];\ncreg c[1];\nmeasure q[0] -> c[2];\n";
        assertThrows(QasmParseException.class, () -> QasmImporter.fromQasm(bad));
    }

    // (E) Barrier support (currently unsupported)
    @Disabled("Barrier not yet supported")
    @Test
    void barrierRoundTrip() {
        String qasm = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[2];\ncreg c[2];\nbarrier q[0],q[1];\n";
        QuantumCircuit qc = QasmImporter.fromQasm(qasm);
        assertNotNull(qc);
    }

    // (F) Unsupported classical expressions
    @Test
    void classicalExpressionsThrow() {
        String bad1 = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[1];\ncreg c[1];\nif (c==1) x q[0];\n";
        String bad2 = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[1];\ncreg c[1];\nc[0] = 1;\n";
        assertThrows(QasmParseException.class, () -> QasmImporter.fromQasm(bad1));
        assertThrows(QasmParseException.class, () -> QasmImporter.fromQasm(bad2));
    }

    // (G) Round-trip determinism
    @Test
    void roundTripDeterminismBell() {
        QuantumCircuit qc = QuantumCircuit.create(2).h(0).cx(0, 1).measureAll();
        String e1 = exportCompact(qc);
        String e2 = exportCompact(QasmImporter.fromQasm(QasmExporter.toQasm(qc)));
        assertEquals(e1, e2);
    }

    @Test
    void roundTripDeterminismUAndRotations() {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .u3(0, 0.1, 0.2, 0.3)
                .rx(0, 0.4)
                .rz(0, -0.5)
                .measureAll();
        String e1 = exportCompact(qc);
        String e2 = exportCompact(QasmImporter.fromQasm(QasmExporter.toQasm(qc)));
        assertEquals(e1, e2);
    }

    @Test
    void roundTripDeterminismTranspiled() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .swap(0, 1)
                .cx(0, 1)
                .u3(0, 0.1, 0.2, 0.3)
                .measureAll();
        QuantumCircuit transpiled = new PassManager()
                .addPass(new SwapDecompositionPass())
                .addPass(new CXCancellationPass())
                .addPass(new U3DecompositionPass())
                .addPass(new RotationFusionPass())
                .run(circuit);
        String e1 = exportCompact(transpiled);
        String e2 = exportCompact(QasmImporter.fromQasm(QasmExporter.toQasm(transpiled)));
        assertEquals(e1, e2);
    }

    // (H) Stress test (lightweight)
    @Test
    void randomCircuitRoundTrip() {
        Random rnd = new Random(42);
        QuantumCircuit qc = QuantumCircuit.create(3);
        for (int i = 0; i < 30; i++) {
            int t = rnd.nextInt(6);
            switch (t) {
                case 0: qc.h(rnd.nextInt(3)); break;
                case 1: qc.rx(rnd.nextInt(3), rnd.nextDouble()); break;
                case 2: qc.rz(rnd.nextInt(3), rnd.nextDouble()); break;
                case 3: {
                    int control = rnd.nextInt(3);
                    int target = rnd.nextInt(3);
                    if (control != target) {
                        qc.cx(control, target);
                    }
                    break;
                }
                case 4: qc.u1(rnd.nextInt(3), rnd.nextDouble()); break;
                case 5: qc.cz(0, 1); break;
                default: break;
            }
        }
        qc.measureAll();
        assertInstructionListsEqual(qc, roundTrip(qc));
    }
}

