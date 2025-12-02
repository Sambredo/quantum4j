package com.quantum4j.tests;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestCNOT {

    @Test
    public void testCNOTFlipsTargetWhenControlIs1() {
        QuantumCircuit qc = QuantumCircuit.create(2).x(0) // control = 1
                .cx(0, 1).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));

        assertTrue(r.getCounts().containsKey("11"));
    }

    @Test
    public void testCNODOESNOTflipWhenControlZero() {
        QuantumCircuit qc = QuantumCircuit.create(2).cx(0, 1).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));

        assertTrue(r.getCounts().containsKey("00"));
    }
}


