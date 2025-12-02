package com.quantum4j.tests;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.transpile.PassManager;
import com.quantum4j.transpile.passes.RotationFusionPass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RotationFusionPassTest {

    private QuantumCircuit run(QuantumCircuit circuit) {
        return new PassManager()
                .addPass(new RotationFusionPass())
                .run(circuit);
    }

    @Test
    void testFuseTwoRZ() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .rz(0, 0.1)
                .rz(0, 0.2);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(1, inst.size());
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);
        assertEquals(0.3, ((StandardGates.RZGate) inst.get(0).getGate()).getTheta(), 1e-12);
    }

    @Test
    void testCancelOppositeRZ() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .rz(0, 0.5)
                .rz(0, -0.5);

        QuantumCircuit out = run(circuit);
        assertTrue(out.getInstructions().isEmpty(), "Opposite rotations should cancel");
    }

    @Test
    void testDoNotFuseAcrossDifferentAxes() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .rz(0, 0.1)
                .rx(0, 0.2)
                .rz(0, 0.3);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();
        assertEquals(3, inst.size(), "Should not fuse across different axes");
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.RXGate);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.RZGate);
    }

    @Test
    void testFusesMultipleInSequence() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .ry(0, 0.1)
                .ry(0, 0.2)
                .ry(0, 0.3)
                .ry(0, -0.1); // net 0.5

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();
        assertEquals(1, inst.size(), "All RY should fuse to single");
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RYGate);
        assertEquals(0.5, ((StandardGates.RYGate) inst.get(0).getGate()).getTheta(), 1e-12);
    }

    @Test
    void testNonRotationGatesPreserved() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .rz(0, 0.1)
                .rz(0, 0.2)
                .h(1)
                .rx(1, 0.3)
                .rx(1, -0.3)
                .z(1);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        // rz fused -> one, h preserved, rx cancels (removed), z preserved => total 3
        assertEquals(3, inst.size());
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);
        assertEquals(0.3, ((StandardGates.RZGate) inst.get(0).getGate()).getTheta(), 1e-12);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.HGate);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.ZGate);
    }
}



