package com.quantum4j.tests;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestXGate {

    @Test
    public void testXonZero() {
        QuantumCircuit qc = QuantumCircuit.create(1).x(0).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));
        assertTrue(r.getCounts().containsKey("1"));
    }

    @Test
    public void testXonOne() {
        QuantumCircuit qc = QuantumCircuit.create(1).x(0).x(0).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));
        assertTrue(r.getCounts().containsKey("0"));
    }
}


