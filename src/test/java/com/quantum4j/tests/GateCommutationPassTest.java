package com.quantum4j.tests;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.transpile.PassManager;
import com.quantum4j.transpile.passes.GateCommutationPass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GateCommutationPassTest {

    private QuantumCircuit run(QuantumCircuit circuit) {
        return new PassManager()
                .addPass(new GateCommutationPass())
                .run(circuit);
    }

    @Test
    void singleQubitGatesCommuteAcrossDifferentQubits() {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .rz(0, 0.1)
                .rx(1, 0.2);

        QuantumCircuit out = run(qc);
        List<Instruction> inst = out.getInstructions();
        assertEquals("rx", inst.get(0).getGate().name().toLowerCase());
        assertEquals("rz", inst.get(1).getGate().name().toLowerCase());
    }

    @Test
    void measurementIsBarrier() {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .h(0)
                .measure(0, 0)
                .rz(0, 0.2);

        QuantumCircuit out = run(qc);
        List<Instruction> inst = out.getInstructions();
        // h should not cross measure
        assertEquals(Instruction.Type.GATE, inst.get(0).getType());
        assertEquals(Instruction.Type.MEASURE, inst.get(1).getType());
        assertEquals(Instruction.Type.GATE, inst.get(2).getType());
    }

    @Test
    void rzOnControlMovesBeforeCX() {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .cx(0, 1)
                .rz(0, 0.5);

        QuantumCircuit out = run(qc);
        List<Instruction> inst = out.getInstructions();
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RZGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.CNOTGate);
    }

    @Test
    void rxOnTargetMovesBeforeCX() {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .cx(0, 1)
                .rx(1, 0.5);

        QuantumCircuit out = run(qc);
        List<Instruction> inst = out.getInstructions();
        assertTrue(inst.get(0).getGate() instanceof StandardGates.RXGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.CNOTGate);
    }

    @Test
    void nonCommutingGatesStayPut() {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .cx(0, 1)
                .x(0);

        QuantumCircuit out = run(qc);
        List<Instruction> inst = out.getInstructions();
        // x on control does NOT commute with cx
        assertTrue(inst.get(0).getGate() instanceof StandardGates.CNOTGate);
        assertTrue(inst.get(1).getGate() instanceof StandardGates.XGate);
    }

    @Test
    void idempotentOnSecondRun() {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .rz(0, 0.1)
                .rx(1, 0.2)
                .cx(0, 1);

        QuantumCircuit once = run(qc);
        QuantumCircuit twice = run(once);

        assertEquals(once.getInstructions().size(), twice.getInstructions().size());
        for (int i = 0; i < once.getInstructions().size(); i++) {
            Instruction a = once.getInstructions().get(i);
            Instruction b = twice.getInstructions().get(i);
            assertEquals(a.getType(), b.getType());
            assertEquals(a.getGate().name(), b.getGate().name());
            assertArrayEquals(a.getQubits(), b.getQubits());
        }
    }
}



