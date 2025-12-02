package com.quantum4j.tests;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestCCX {

    @Test
    public void testCCXTriggersOnlyWhenBothControlsOne() {
        QuantumCircuit qc = QuantumCircuit.create(3).x(0).x(1).ccx(0, 1, 2).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));

        assertTrue(r.getCounts().containsKey("111"));
    }

    @Test
    public void testCCXDoesNotTriggerOtherwise() {
        QuantumCircuit qc = QuantumCircuit.create(3).x(0).ccx(0, 1, 2).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));

        assertTrue(r.getCounts().containsKey("100"));
    }
}

