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
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QasmRoundTripRobustnessTest {

    private QuantumCircuit roundTrip(QuantumCircuit circuit) {
        String qasm = QasmExporter.toQasm(circuit);
        return QasmImporter.fromQasm(qasm);
    }

    private void assertCircuitsEqual(QuantumCircuit a, QuantumCircuit b) {
        List<Instruction> ia = a.getInstructions();
        List<Instruction> ib = b.getInstructions();
        assertEquals(ia.size(), ib.size(), "Instruction list size mismatch");
        for (int i = 0; i < ia.size(); i++) {
            Instruction x = ia.get(i);
            Instruction y = ib.get(i);
            assertEquals(x.getType(), y.getType(), "Type mismatch at " + i);
            if (x.getType() == Instruction.Type.GATE) {
                assertEquals(x.getGate().name(), y.getGate().name(), "Gate name mismatch at " + i);
                assertArrayEquals(x.getQubits(), y.getQubits(), "Qubits mismatch at " + i);
            } else {
                assertArrayEquals(x.getQubits(), y.getQubits(), "Measure qubit mismatch at " + i);
                assertArrayEquals(x.getClassicalBits(), y.getClassicalBits(), "Classical bits mismatch at " + i);
            }
        }
    }

    @Test
    void basicGatesRoundTrip() {
        QuantumCircuit qc = QuantumCircuit.create(3)
                .h(0).x(1).y(2).z(0).s(1).t(2)
                .rx(0, 0.1).ry(1, 0.2).rz(2, 0.3)
                .cx(0, 1).cz(1, 2).swap(0, 2).ch(1, 0).ccx(0, 1, 2)
                .measureAll();

        int origSize = qc.getInstructions().size();
        QuantumCircuit rt = roundTrip(qc);
        assertEquals(origSize, qc.getInstructions().size(), "Original circuit mutated");
        assertCircuitsEqual(qc, rt);
    }

    @Test
    void uGatesRoundTrip() {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .u1(0, 0.1)
                .u2(0, 0.2, 0.3)
                .u3(0, 0.4, 0.5, 0.6)
                .measureAll();

        QuantumCircuit rt = roundTrip(qc);
        assertCircuitsEqual(qc, rt);
    }

    @Test
    void compositeCircuitsRoundTrip() {
        // Bell
        QuantumCircuit bell = QuantumCircuit.create(2).h(0).cx(0, 1).measureAll();
        assertCircuitsEqual(bell, roundTrip(bell));

        // GHZ
        QuantumCircuit ghz = QuantumCircuit.create(3).h(0).cx(0, 1).cx(0, 2).measureAll();
        assertCircuitsEqual(ghz, roundTrip(ghz));

        // Toffoli demo
        QuantumCircuit tof = QuantumCircuit.create(3).x(0).x(1).ccx(0, 1, 2).measureAll();
        assertCircuitsEqual(tof, roundTrip(tof));

        // Teleportation mini (3 qubits)
        QuantumCircuit tele = QuantumCircuit.create(3)
                .h(1).cx(1, 2)        // create Bell on 1-2
                .cx(0, 1).h(0)        // entangle qubit0 with Bell pair
                .measure(0, 0).measure(1, 1)
                .cx(1, 2).cz(0, 2)    // corrections
                .measure(2, 2);
        assertCircuitsEqual(tele, roundTrip(tele));
    }

    @Test
    void multiMeasurementScenarios() {
        QuantumCircuit all = QuantumCircuit.create(2).h(0).cx(0, 1).measureAll();
        assertCircuitsEqual(all, roundTrip(all));

        QuantumCircuit interleaved = QuantumCircuit.create(1)
                .h(0)
                .measure(0, 0)
                .rz(0, 0.1)
                .measure(0, 0);
        assertCircuitsEqual(interleaved, roundTrip(interleaved));
    }

    @Test
    void transpiledCircuitsRoundTrip() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .swap(0, 1)
                .cx(0, 1)
                .cx(0, 1)
                .u3(0, 0.1, 0.2, 0.3)
                .measureAll();

        int origSize = circuit.getInstructions().size();
        QuantumCircuit transpiled = new PassManager()
                .addPass(new SwapDecompositionPass())
                .addPass(new CXCancellationPass())
                .addPass(new U3DecompositionPass())
                .addPass(new RotationFusionPass())
                .run(circuit);

        QuantumCircuit rt = roundTrip(transpiled);
        assertCircuitsEqual(transpiled, rt);
        // original remains unmodified
        assertEquals(origSize, circuit.getInstructions().size());
    }

    @Test
    void invalidQasmThrows() {
        String bad = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[2];\ncreg c[2];\ncx q[0] -] q[1];\n";
        assertThrows(QasmParseException.class, () -> QasmImporter.fromQasm(bad));
    }
}

