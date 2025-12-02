package com.quantum4j.tests;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.transpile.PassManager;
import com.quantum4j.transpile.passes.CXCancellationPass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CXCancellationPassTest {

    private QuantumCircuit run(QuantumCircuit circuit) {
        return new PassManager()
                .addPass(new CXCancellationPass())
                .run(circuit);
    }

    @Test
    void doubleCxCancels() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .cx(0, 1)
                .cx(0, 1);

        QuantumCircuit out = run(circuit);
        assertTrue(out.getInstructions().isEmpty(), "Double CX should cancel completely");
    }

    @Test
    void tripleCxLeavesOne() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .cx(0, 1)
                .cx(0, 1)
                .cx(0, 1);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();
        assertEquals(1, inst.size(), "Triple CX should reduce to one CX");
        assertTrue(inst.get(0).getGate() instanceof StandardGates.CNOTGate);
        assertArrayEquals(new int[]{0, 1}, inst.get(0).getQubits());
    }

    @Test
    void mixedGatesPreserved() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .cx(0, 1)
                .z(1);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();
        assertEquals(2, inst.size(), "Non-CX gates should remain");
        assertTrue(inst.get(0).getGate() instanceof StandardGates.HGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.ZGate);
    }

    @Test
    void measurementPreserved() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .cx(0, 1)
                .cx(0, 1)
                .measure(1, 0);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();
        assertEquals(1, inst.size(), "CX pair cancels; measurement remains");
        assertEquals(Instruction.Type.MEASURE, inst.get(0).getType());
        assertEquals(1, inst.get(0).getQubits()[0]);
        assertEquals(0, inst.get(0).getClassicalBits()[0]);
    }

    @Test
    void noCrossPairCancellation() {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .cx(0, 1)
                .cx(1, 0);

        QuantumCircuit out = run(circuit);
        List<Instruction> inst = out.getInstructions();
        assertEquals(2, inst.size(), "Different qubit pairs must not cancel");
        assertArrayEquals(new int[]{0, 1}, inst.get(0).getQubits());
        assertArrayEquals(new int[]{1, 0}, inst.get(1).getQubits());
    }
}


