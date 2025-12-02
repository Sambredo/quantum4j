package com.quantum4j.tests;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.transpile.PassManager;
import com.quantum4j.transpile.passes.CancelDoubleXPass;
import com.quantum4j.transpile.passes.CancelDoubleHPass;
import com.quantum4j.transpile.passes.CancelDoubleZPass;
import com.quantum4j.transpile.passes.RemoveRedundantMeasurementPass;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TestTranspilerPipeline {

    @Test
    public void testFullTranspilePipeline() {

        // Input circuit with many redundant gates + redundant measurement
        QuantumCircuit qc = QuantumCircuit.create(1)
                .x(0).x(0)           // should cancel
                .h(0).h(0)           // should cancel
                .z(0).z(0)           // should cancel
                .measure(0, 0)
                .measure(0, 0);      // redundant, remove

        // Build transpiler pipeline
        PassManager pm = new PassManager()
                .addPass(new CancelDoubleXPass())
                .addPass(new CancelDoubleHPass())
                .addPass(new CancelDoubleZPass())
                .addPass(new RemoveRedundantMeasurementPass());

        QuantumCircuit out = pm.run(qc);

        List<Instruction> insts = out.getInstructions();

        // Expect ONLY ONE remaining instruction: measure(0 -> 0)
        assertEquals(1, insts.size(), "All redundant gates should be removed");

        Instruction only = insts.get(0);
        assertEquals(Instruction.Type.MEASURE, only.getType());
        assertArrayEquals(new int[]{0}, only.getQubits());
        assertArrayEquals(new int[]{0}, only.getClassicalBits());
    }
}


