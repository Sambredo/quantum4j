package io.quantum4j.tests;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.StandardGates;
import io.quantum4j.transpile.PassManager;
import io.quantum4j.transpile.passes.GateInversionPass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GateInversionPassTest {

    private QuantumCircuit run(QuantumCircuit circuit) {
        return new PassManager()
                .addPass(new GateInversionPass())
                .run(circuit);
    }

    @Test
    void selfInverseGatesReverseOrder() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .h(0)
                .x(0)
                .z(0);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(3, inst.size());
        assertTrue(inst.get(0).getGate() instanceof StandardGates.ZGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.XGate);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.HGate);
    }

    @Test
    void rotationAnglesNegatedAndOrderReversed() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .rz(0, 0.3)
                .ry(0, -0.7)
                .rx(0, 1.2);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(3, inst.size());
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RXGate);
        assertEquals(-1.2, ((StandardGates.RXGate) inst.get(0).getGate()).getTheta(), 1e-12);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.RYGate);
        assertEquals(0.7, ((StandardGates.RYGate) inst.get(1).getGate()).getTheta(), 1e-12);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.RZGate);
        assertEquals(-0.3, ((StandardGates.RZGate) inst.get(2).getGate()).getTheta(), 1e-12);
    }

    @Test
    void cnotReversesOrder() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .cx(0, 1)
                .cx(0, 1);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();
        assertEquals(2, inst.size());
        assertArrayEquals(new int[]{0, 1}, inst.get(0).getQubits());
        assertArrayEquals(new int[]{0, 1}, inst.get(1).getQubits());
        assertTrue(inst.get(0).getGate() instanceof StandardGates.CNOTGate);
    }

    @Test
    void measurementStopsInversionAndIsPreserved() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .h(0)
                .rz(0, 0.1)
                .measure(0, 0);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(3, inst.size());
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);
        assertEquals(-0.1, ((StandardGates.RZGate) inst.get(0).getGate()).getTheta(), 1e-12);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.HGate);
        assertEquals(Instruction.Type.MEASURE, inst.get(2).getType());
    }

    @Test
    void inverseOfInverseReturnsOriginal() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .h(0)
                .rz(0, 0.2)
                .cx(0, 1);

        QuantumCircuit inv = run(circuit);
        QuantumCircuit back = run(inv);

        List<Instruction> orig = circuit.getInstructions();
        List<Instruction> twice = back.getInstructions();

        assertEquals(orig.size(), twice.size(), "Double inversion should restore length");
        for (int i = 0; i < orig.size(); i++) {
            Instruction a = orig.get(i);
            Instruction b = twice.get(i);
            assertEquals(a.getType(), b.getType());
            assertArrayEquals(a.getQubits(), b.getQubits());
            assertEquals(a.getGate().name(), b.getGate().name());
            if (a.getGate() instanceof StandardGates.RZGate) {
                assertEquals(((StandardGates.RZGate) a.getGate()).getTheta(),
                        ((StandardGates.RZGate) b.getGate()).getTheta(), 1e-12);
            }
        }
        // Ensure original circuit not mutated
        assertEquals(3, circuit.getInstructions().size());
        assertTrue(circuit.getInstructions().get(2).getGate() instanceof StandardGates.CNOTGate);
    }
}

