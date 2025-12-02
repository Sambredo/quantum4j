package com.quantum4j.tests;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.transpile.PassManager;
import com.quantum4j.transpile.passes.RotationFusionPass;
import com.quantum4j.transpile.passes.U3DecompositionPass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class U3DecompositionPassTest {

    private QuantumCircuit run(QuantumCircuit circuit) {
        return new PassManager()
                .addPass(new U3DecompositionPass())
                .run(circuit);
    }

    @Test
    void testU3DecomposesToRZ_RX_RZ_RX_RZ() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .u3(0, 1.0, 0.5, -0.25);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(5, inst.size(), "U3 should expand to 5 instructions");
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.RXGate);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.RZGate);
        assertTrue(inst.get(3).getGate() instanceof StandardGates.RXGate);
        assertTrue(inst.get(4).getGate() instanceof StandardGates.RZGate);

        assertEquals(0.5, ((StandardGates.RZGate) inst.get(0).getGate()).getTheta(), 1e-12);
        assertEquals(Math.PI / 2, ((StandardGates.RXGate) inst.get(1).getGate()).getTheta(), 1e-12);
        assertEquals(1.0, ((StandardGates.RZGate) inst.get(2).getGate()).getTheta(), 1e-12);
        assertEquals(-Math.PI / 2, ((StandardGates.RXGate) inst.get(3).getGate()).getTheta(), 1e-12);
        assertEquals(-0.25, ((StandardGates.RZGate) inst.get(4).getGate()).getTheta(), 1e-12);
    }

    @Test
    void testU2DecomposesAsU3HalfPi() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .u2(0, 0.1, 0.2);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(5, inst.size(), "U2 should expand to 5 instructions via U3(π/2,phi,lambda)");

        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);
        assertEquals(0.1, ((StandardGates.RZGate) inst.get(0).getGate()).getTheta(), 1e-12);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.RXGate);
        assertEquals(Math.PI / 2, ((StandardGates.RXGate) inst.get(1).getGate()).getTheta(), 1e-12);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.RZGate);
        assertEquals(Math.PI / 2, ((StandardGates.RZGate) inst.get(2).getGate()).getTheta(), 1e-12);
        assertTrue(inst.get(3).getGate() instanceof StandardGates.RXGate);
        assertEquals(-Math.PI / 2, ((StandardGates.RXGate) inst.get(3).getGate()).getTheta(), 1e-12);
        assertTrue(inst.get(4).getGate() instanceof StandardGates.RZGate);
        assertEquals(0.2, ((StandardGates.RZGate) inst.get(4).getGate()).getTheta(), 1e-12);
    }

    @Test
    void testU1BecomesSingleRZ() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .u1(0, 0.3);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(1, inst.size(), "U1 should reduce to single RZ");
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);
        assertEquals(0.3, ((StandardGates.RZGate) inst.get(0).getGate()).getTheta(), 1e-12);
    }

    @Test
    void testDecompositionBeforeRotationFusion() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .u3(0, 0.1, 0.2, 0.3)
                .u1(0, -0.3); // should fuse with last RZ

        QuantumCircuit out = new PassManager()
                .addPass(new U3DecompositionPass())
                .addPass(new RotationFusionPass())
                .run(circuit);

        // U3 -> 5 gates, then fuse final RZ with U1(-0.3) -> last RZ cancels to zero leaving 4 gates
        List<Instruction> inst = out.getInstructions();
        assertEquals(4, inst.size(), "Final RZ should fuse and cancel with U1(-0.3)");
    }

    @Test
    void testIdempotentAndInputNotMutated() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .u3(0, 0.1, 0.2, 0.3);

        QuantumCircuit first = run(circuit);
        QuantumCircuit second = run(first);

        assertEquals(first.getInstructions().size(), second.getInstructions().size());
        for (int i = 0; i < first.getInstructions().size(); i++) {
            Instruction a = first.getInstructions().get(i);
            Instruction b = second.getInstructions().get(i);
            assertEquals(a.getGate().name(), b.getGate().name());
            assertArrayEquals(a.getQubits(), b.getQubits());
        }

        // Original circuit remains U3
        assertEquals(1, circuit.getInstructions().size(), "Original circuit should remain unchanged");
        assertTrue(circuit.getInstructions().get(0).getGate() instanceof StandardGates.U3Gate);
    }
}



