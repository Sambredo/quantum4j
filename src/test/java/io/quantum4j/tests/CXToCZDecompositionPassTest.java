package io.quantum4j.tests;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.StandardGates;
import io.quantum4j.transpile.PassManager;
import io.quantum4j.transpile.passes.CXToCZDecompositionPass;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CXToCZDecompositionPassTest {

    private QuantumCircuit run(QuantumCircuit circuit) {
        return new PassManager()
                .addPass(new CXToCZDecompositionPass())
                .run(circuit);
    }

    @Test
    void testSingleCXDecomposesToH_CZ_H() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .cx(0, 1);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        assertEquals(3, inst.size(), "CX should decompose into H, CZ, H");

        assertTrue(inst.get(0).getGate() instanceof StandardGates.HGate,
                "First should be H on target");
        assertTrue(inst.get(1).getGate() instanceof StandardGates.CZGate,
                "Second should be CZ");
        assertTrue(inst.get(2).getGate() instanceof StandardGates.HGate,
                "Third should be H on target");

        // Check control/target wiring is correct
        int[] czQubits = inst.get(1).getQubits();
        assertEquals(0, czQubits[0], "CZ control should be original CX control");
        assertEquals(1, czQubits[1], "CZ target should be original CX target");

        // Check H acts on target
        assertEquals(1, inst.get(0).getQubits()[0]);
        assertEquals(1, inst.get(2).getQubits()[0]);
    }

    @Test
    void testMultipleCXDecomposeIndependently() {
        QuantumCircuit circuit = QuantumCircuit.create(3)
                .cx(0, 1)
                .cx(1, 2);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        // Each CX expands to 3 gates → total 6
        assertEquals(6, inst.size());

        // First CX(0,1) → H1, CZ(0,1), H1
        assertTrue(inst.get(0).getGate() instanceof StandardGates.HGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.CZGate);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.HGate);
        assertEquals(1, inst.get(0).getQubits()[0]);
        assertEquals(0, inst.get(1).getQubits()[0]);
        assertEquals(1, inst.get(1).getQubits()[1]);
        assertEquals(1, inst.get(2).getQubits()[0]);

        // Second CX(1,2) → H2, CZ(1,2), H2
        assertTrue(inst.get(3).getGate() instanceof StandardGates.HGate);
        assertTrue(inst.get(4).getGate() instanceof StandardGates.CZGate);
        assertTrue(inst.get(5).getGate() instanceof StandardGates.HGate);
        assertEquals(2, inst.get(3).getQubits()[0]);
        assertEquals(1, inst.get(4).getQubits()[0]);
        assertEquals(2, inst.get(4).getQubits()[1]);
        assertEquals(2, inst.get(5).getQubits()[0]);
    }

    @Test
    void testMeasurementPreserved() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .cx(0, 1)
                .measure(1, 0);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        // CX → H, CZ, H + measure
        assertEquals(4, inst.size());
        assertEquals(Instruction.Type.MEASURE, inst.get(3).getType());
        assertEquals(1, inst.get(3).getQubits()[0]);
        assertEquals(0, inst.get(3).getClassicalBits()[0]);
    }

    @Test
    void testOtherGatesAreUnchanged() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .z(1);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();

        // h, (cx→h,cz,h), z → 5 instructions
        assertEquals(5, inst.size());

        assertTrue(inst.get(0).getGate() instanceof StandardGates.HGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.HGate);
        assertTrue(inst.get(2).getGate() instanceof StandardGates.CZGate);
        assertTrue(inst.get(3).getGate() instanceof StandardGates.HGate);
        assertTrue(inst.get(4).getGate() instanceof StandardGates.ZGate);
    }
}
