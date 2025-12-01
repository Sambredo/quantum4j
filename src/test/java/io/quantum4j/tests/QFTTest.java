package io.quantum4j.tests;

import io.quantum4j.algorithms.QFT;
import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.StandardGates;
import io.quantum4j.core.math.StateVector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QFTTest {

    @Test
    void qftOneQubitIsHadamard() {
        QuantumCircuit qc = QFT.qft(1);
        List<Instruction> inst = qc.getInstructions();

        assertEquals(1, inst.size());
        assertTrue(inst.get(0).getGate() instanceof StandardGates.HGate);
        assertArrayEquals(new int[]{0}, inst.get(0).getQubits());
    }

    @Test
    void qftTwoQubitsSequence() {
        QuantumCircuit qc = QFT.qft(2);
        List<Instruction> inst = qc.getInstructions();

        // Your implementation emits exactly 8 instructions
        assertEquals(8, inst.size(), "QFT(2) should produce 8 instructions");

        // 0: H(0)
        assertTrue(inst.get(0).getGate() instanceof StandardGates.HGate);
        assertArrayEquals(new int[]{0}, inst.get(0).getQubits());

        // 1: RZ(0, +π/4)
        assertTrue(inst.get(1).getGate() instanceof StandardGates.RZGate);
        assertEquals(Math.PI / 4,
                ((StandardGates.RZGate) inst.get(1).getGate()).getTheta(),
                1e-12);
        assertArrayEquals(new int[]{0}, inst.get(1).getQubits());

        // 2: CX(1,0)
        assertTrue(inst.get(2).getGate() instanceof StandardGates.CNOTGate);
        assertArrayEquals(new int[]{1, 0}, inst.get(2).getQubits());

        // 3: RZ(0, -π/4)
        assertTrue(inst.get(3).getGate() instanceof StandardGates.RZGate);
        assertEquals(-Math.PI / 4,
                ((StandardGates.RZGate) inst.get(3).getGate()).getTheta(),
                1e-12);
        assertArrayEquals(new int[]{0}, inst.get(3).getQubits());

        // 4: CX(1,0)
        assertTrue(inst.get(4).getGate() instanceof StandardGates.CNOTGate);
        assertArrayEquals(new int[]{1, 0}, inst.get(4).getQubits());

        // 5: RZ(1, +π/4)  (control-side RZ)
        assertTrue(inst.get(5).getGate() instanceof StandardGates.RZGate);
        assertEquals(Math.PI / 4,
                ((StandardGates.RZGate) inst.get(5).getGate()).getTheta(),
                1e-12);
        assertArrayEquals(new int[]{1}, inst.get(5).getQubits());

        // 6: H(1)
        assertTrue(inst.get(6).getGate() instanceof StandardGates.HGate);
        assertArrayEquals(new int[]{1}, inst.get(6).getQubits());

        // 7: SWAP(0,1)
        assertTrue(inst.get(7).getGate() instanceof StandardGates.SWAPGate);
        assertArrayEquals(new int[]{0, 1}, inst.get(7).getQubits());
    }

    @Test
    void inverseQftRestoresBasisStates() {
        for (int x = 0; x < 8; x++) {

            // Prepare |x⟩
            QuantumCircuit qc = QuantumCircuit.create(3);
            if ((x & 1) != 0) qc.x(0);
            if ((x & 2) != 0) qc.x(1);
            if ((x & 4) != 0) qc.x(2);

            // Append QFT then iQFT
            for (Instruction inst : QFT.qft(3).getInstructions())
                qc.addInstruction(inst.copy());
            for (Instruction inst : QFT.inverseQft(3).getInstructions())
                qc.addInstruction(inst.copy());

            // Simulate manually
            StateVector sv = new StateVector(3);
            for (Instruction inst : qc.getInstructions()) {
                if (inst.getType() == Instruction.Type.GATE) {
                    if (inst.getGate() instanceof io.quantum4j.core.gates.SingleQubitGate g1) {
                        g1.apply(sv, inst.getQubits()[0]);
                    } else if (inst.getGate() instanceof io.quantum4j.core.gates.TwoQubitGate g2) {
                        g2.apply(sv, inst.getQubits()[0], inst.getQubits()[1]);
                    } else if (inst.getGate() instanceof io.quantum4j.core.gates.ThreeQubitGate g3) {
                        g3.apply(sv, inst.getQubits()[0], inst.getQubits()[1], inst.getQubits()[2]);
                    }
                }
            }

            // Basis amplitude match
            double prob = sv.getAmplitudes()[x].absSquared();
            assertEquals(1.0, prob, 1e-6,
                    "Inverse QFT should perfectly restore basis state |" + x + ">");
        }
    }

    @Test
    void deterministicInstructionSequence() {
        QuantumCircuit a = QFT.qft(3);
        QuantumCircuit b = QFT.qft(3);

        assertEquals(a.getInstructions().size(), b.getInstructions().size(),
                "QFT must produce deterministic size");

        for (int i = 0; i < a.getInstructions().size(); i++) {
            Instruction i1 = a.getInstructions().get(i);
            Instruction i2 = b.getInstructions().get(i);

            assertEquals(i1.getGate().name(), i2.getGate().name(),
                    "Gate mismatch at index " + i);

            assertArrayEquals(i1.getQubits(), i2.getQubits(),
                    "Qubit operand mismatch at index " + i);
        }
    }
}
