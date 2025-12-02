package com.quantum4j.tests;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestISWAP {

    @Test
    public void testISwapSwapsPopulation() {
        QuantumCircuit qc = QuantumCircuit.create(2).x(0).iswap(0, 1).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));

        System.out.println("Counts = " + r.getCounts());

        // ISWAP |01> = i|10>; printed as LSB→MSB => "01"
        assertTrue(r.getCounts().containsKey("01"));
    }
}

