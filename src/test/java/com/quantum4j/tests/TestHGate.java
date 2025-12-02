package com.quantum4j.tests;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestHGate {

    @Test
    public void testHadamardCreatesSuperposition() {
        QuantumCircuit qc = QuantumCircuit.create(1).h(0).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(400));

        assertTrue(r.getCounts().containsKey("0"));
        assertTrue(r.getCounts().containsKey("1"));

        double p0 = r.getCounts().get("0") / 400.0;
        double p1 = r.getCounts().get("1") / 400.0;

        assertTrue(Math.abs(p0 - 0.5) < 0.15);
        assertTrue(Math.abs(p1 - 0.5) < 0.15);
    }
}


