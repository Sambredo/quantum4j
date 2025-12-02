package com.quantum4j.tests;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.transpile.DefaultTranspiler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultTranspilerTest {

    @Test
    void testSwapDecomposesThenCancels() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .swap(0, 1)
                .swap(0, 1); // two swaps should cancel after decomposition + CX cancellation

        QuantumCircuit out = DefaultTranspiler.transpile(circuit);
        assertTrue(out.getInstructions().isEmpty(), "Two swaps should cancel after pipeline");
    }

    @Test
    void testRotationsFuseAfterPipeline() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .rz(0, 0.1)
                .rz(0, 0.2)
                .rz(0, -0.3);

        QuantumCircuit out = DefaultTranspiler.transpile(circuit);
        assertTrue(out.getInstructions().isEmpty(), "Rotations should fuse and cancel to zero");
    }

    @Test
    void testPassOrderingRespected() {
        // Swap first, so decomposition runs, then cancellation, then rotation fusion untouched
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .swap(0, 1)
                .cx(0, 1)
                .cx(0, 1)
                .rz(0, 0.1)
                .rz(0, 0.2);

        QuantumCircuit out = DefaultTranspiler.transpile(circuit);
        List<Instruction> inst = out.getInstructions();

        // swap -> 3 cx, plus 2 cx that cancel each other => net 3 cx
        // rotations fuse to single rz(0.3)
        assertEquals(4, inst.size(), "Expected 3 CX from swap + 1 fused RZ");

        assertTrue(inst.get(0).getGate() instanceof StandardGates.CNOTGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.CNOTGate);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.CNOTGate);
        assertTrue(inst.get(3).getGate() instanceof StandardGates.RZGate);
        assertEquals(0.3, ((StandardGates.RZGate) inst.get(3).getGate()).getTheta(), 1e-12);
    }

    @Test
    void testPipelineStableIdempotent() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .swap(0, 1)
                .cx(0, 1)
                .cx(0, 1)
                .rz(0, 0.1)
                .rz(0, 0.2);

        QuantumCircuit once = DefaultTranspiler.transpile(circuit);
        QuantumCircuit twice = DefaultTranspiler.transpile(once);

        assertEquals(once.getInstructions().size(), twice.getInstructions().size(), "Pipeline should be stable");
        for (int i = 0; i < once.getInstructions().size(); i++) {
            Instruction a = once.getInstructions().get(i);
            Instruction b = twice.getInstructions().get(i);
            assertEquals(a.getType(), b.getType());
            if (a.getType() == Instruction.Type.GATE) {
                assertEquals(a.getGate().name(), b.getGate().name());
                assertArrayEquals(a.getQubits(), b.getQubits());
            }
        }
    }
}


