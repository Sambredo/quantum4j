package com.quantum4j.tests;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.transpile.PassManager;
import com.quantum4j.transpile.passes.CancelDoubleXPass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCancelDoubleXPass {

    @Test
    public void testDoubleXIsRemoved() {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .x(0)
                .x(0)
                .h(0);

        PassManager pm = new PassManager().addPass(new CancelDoubleXPass());
        QuantumCircuit optimized = pm.run(qc);

        // Original had 3 instructions; new one should have 1 (only H).
        assertEquals(1, optimized.getInstructions().size());
    }

    @Test
    public void testSingleXRemains() {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .x(0)
                .h(0);

        PassManager pm = new PassManager().addPass(new CancelDoubleXPass());
        QuantumCircuit optimized = pm.run(qc);

        // No X-X pattern → unchanged
        assertEquals(qc.getInstructions().size(), optimized.getInstructions().size());
    }
}

