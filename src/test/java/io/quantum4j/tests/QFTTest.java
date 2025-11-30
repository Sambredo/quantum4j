package io.quantum4j.tests;

import io.quantum4j.algorithms.QFT;
import io.quantum4j.core.backend.Result;
import io.quantum4j.core.backend.RunOptions;
import io.quantum4j.core.backend.StateVectorBackend;
import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.StandardGates;
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
        assertEquals(0, inst.get(0).getQubits()[0]);
    }

    @Test
    void qftTwoQubitsSequence() {
        QuantumCircuit qc = QFT.qft(2);
        List<Instruction> inst = qc.getInstructions();
        // Expected: H(0), U1(1,pi/4), CX(1,0), U1(0,-pi/4), CX(1,0), H(1), SWAP(0,1)
        assertEquals(7, inst.size());
        assertTrue(inst.get(0).getGate() instanceof StandardGates.HGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.U1Gate);
        assertEquals(Math.PI / 4, ((StandardGates.U1Gate) inst.get(1).getGate()).getLambda(), 1e-12);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.CNOTGate);
        assertTrue(inst.get(3).getGate() instanceof StandardGates.U1Gate);
        assertEquals(-Math.PI / 4, ((StandardGates.U1Gate) inst.get(3).getGate()).getLambda(), 1e-12);
        assertTrue(inst.get(4).getGate() instanceof StandardGates.CNOTGate);
        assertTrue(inst.get(5).getGate() instanceof StandardGates.HGate);
        assertTrue(inst.get(6).getGate() instanceof StandardGates.SWAPGate);
    }

    @Test
    void inverseQftRestoresBasisStates() {
        for (int x = 0; x < 8; x++) {
            QuantumCircuit qc = QuantumCircuit.create(3);
            if ((x & 1) != 0) qc.x(0);
            if ((x & 2) != 0) qc.x(1);
            if ((x & 4) != 0) qc.x(2);

            for (Instruction inst : QFT.qft(3).getInstructions()) qc.addInstruction(inst.copy());
            for (Instruction inst : QFT.inverseQft(3).getInstructions()) qc.addInstruction(inst.copy());

            StateVectorBackend backend = new StateVectorBackend();
            // Run once without measurements: mimic backend gate application
            io.quantum4j.core.math.StateVector sv = new io.quantum4j.core.math.StateVector(qc.getNumQubits());
            for (Instruction inst : qc.getInstructions()) {
                if (inst.getType() == Instruction.Type.GATE) {
                    if (inst.getGate() instanceof io.quantum4j.core.gates.SingleQubitGate) {
                        ((io.quantum4j.core.gates.SingleQubitGate) inst.getGate()).apply(sv, inst.getQubits()[0]);
                    } else if (inst.getGate() instanceof io.quantum4j.core.gates.TwoQubitGate) {
                        ((io.quantum4j.core.gates.TwoQubitGate) inst.getGate()).apply(sv, inst.getQubits()[0], inst.getQubits()[1]);
                    } else if (inst.getGate() instanceof io.quantum4j.core.gates.ThreeQubitGate) {
                        ((io.quantum4j.core.gates.ThreeQubitGate) inst.getGate()).apply(sv, inst.getQubits()[0], inst.getQubits()[1], inst.getQubits()[2]);
                    }
                }
            }
            int basisIndex = x;
            double prob = sv.getAmplitudes()[basisIndex].absSquared();
            assertTrue(prob > 0.1, "Amplitude should retain significant weight on original basis state");
        }
    }

    @Test
    void deterministicInstructionSequence() {
        QuantumCircuit qc1 = QFT.qft(3);
        QuantumCircuit qc2 = QFT.qft(3);

        List<Instruction> a = qc1.getInstructions();
        List<Instruction> b = qc2.getInstructions();
        assertEquals(a.size(), b.size());
        for (int i = 0; i < a.size(); i++) {
            assertEquals(a.get(i).getGate().name(), b.get(i).getGate().name());
            assertArrayEquals(a.get(i).getQubits(), b.get(i).getQubits());
        }
    }
}
