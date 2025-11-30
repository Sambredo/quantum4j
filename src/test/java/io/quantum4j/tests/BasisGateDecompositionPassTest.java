package io.quantum4j.tests;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.StandardGates;
import io.quantum4j.transpile.PassManager;
import io.quantum4j.transpile.passes.BasisGateDecompositionPass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BasisGateDecompositionPassTest {

    private QuantumCircuit run(QuantumCircuit circuit) {
        return new PassManager()
                .addPass(new BasisGateDecompositionPass())
                .run(circuit);
    }

    // ───────────────────────────────────────────────────────────────────────
    // U1 TEST
    // ───────────────────────────────────────────────────────────────────────
    @Test
    void testU1_ExpandsToSingleRZ() {
        double lambda = Math.PI / 3;

        QuantumCircuit circuit = QuantumCircuit.create(1)
                .u1(0, lambda);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(1, inst.size());
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);

        double rzTheta = ((StandardGates.RZGate) inst.get(0).getGate()).getTheta();
        assertEquals(lambda, rzTheta, 1e-12);
    }

    // ───────────────────────────────────────────────────────────────────────
    // U2 TEST
    // ───────────────────────────────────────────────────────────────────────
    @Test
    void testU2_ExpandsToRz_Ry_Rz() {
        double phi = 0.5;
        double lambda = 1.2;

        QuantumCircuit circuit = QuantumCircuit.create(1)
                .u2(0, phi, lambda);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(3, inst.size());

        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.RYGate);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.RZGate);

        double rz1 = ((StandardGates.RZGate) inst.get(0).getGate()).getTheta();
        double ry = ((StandardGates.RYGate) inst.get(1).getGate()).getTheta();
        double rz2 = ((StandardGates.RZGate) inst.get(2).getGate()).getTheta();

        assertEquals(phi, rz1, 1e-12);
        assertEquals(Math.PI / 2, ry, 1e-12);
        assertEquals(lambda, rz2, 1e-12);
    }

    // ───────────────────────────────────────────────────────────────────────
    // U3 TEST
    // ───────────────────────────────────────────────────────────────────────
    @Test
    void testU3_ExpandsToRz_Ry_Rz() {
        double theta = 1.1;
        double phi = 0.4;
        double lambda = 0.8;

        QuantumCircuit circuit = QuantumCircuit.create(1)
                .u3(0, theta, phi, lambda);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(3, inst.size());

        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.RYGate);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.RZGate);

        double rz1 = ((StandardGates.RZGate) inst.get(0).getGate()).getTheta();
        double ry = ((StandardGates.RYGate) inst.get(1).getGate()).getTheta();
        double rz2 = ((StandardGates.RZGate) inst.get(2).getGate()).getTheta();

        assertEquals(phi, rz1, 1e-12);
        assertEquals(theta, ry, 1e-12);
        assertEquals(lambda, rz2, 1e-12);
    }

    // ───────────────────────────────────────────────────────────────────────
    // MEASUREMENT PRESERVATION
    // ───────────────────────────────────────────────────────────────────────
    @Test
    void testMeasurementIsPreserved() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .u3(0, 1.0, 2.0, 3.0)
                .measure(0, 0);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        // U3 expands to 3 gates + 1 measure
        assertEquals(4, inst.size());
        assertEquals(Instruction.Type.MEASURE, inst.get(3).getType());
        assertEquals(0, inst.get(3).getQubits()[0]);
        assertEquals(0, inst.get(3).getClassicalBits()[0]);
    }

    // ───────────────────────────────────────────────────────────────────────
    // NON-U GATES SHOULD BE COPIED THROUGH
    // ───────────────────────────────────────────────────────────────────────
    @Test
    void testOtherGatesAreCopied() {
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .h(0)
                .u1(0, 0.3)
                .x(0);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        // h → unchanged
        assertTrue(inst.get(0).getGate() instanceof StandardGates.HGate);

        // u1 → rz
        assertTrue(inst.get(1).getGate() instanceof StandardGates.RZGate);

        // x → unchanged
        assertTrue(inst.get(2).getGate() instanceof StandardGates.XGate);
    }
}
