package com.quantum4j.tests;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestSWAP {

    @Test
    public void testSwap_01_to_10() {
        // Prepare |01> => x(0)
        QuantumCircuit qc = QuantumCircuit.create(2).x(0).swap(0, 1).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));

        System.out.println("SWAP 01->10 Counts = " + r.getCounts());

        // After swap(0,1), |01> becomes |10>
        // Printed as [q0][q1] = LSB→MSB = "01" is |10⟩ ?
        // NO → |10⟩ prints as "01"? Actually:
        //
        // |10⟩ (q1=1, q0=0) prints as "01"
        assertTrue(r.getCounts().containsKey("01"));
    }

    @Test
    public void testSwap_10_to_01() {
        // Prepare |10> => x(1)
        QuantumCircuit qc = QuantumCircuit.create(2).x(1).swap(0, 1).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));

        System.out.println("SWAP 10->01 Counts = " + r.getCounts());

        // After swap, |10> becomes |01>
        // |01⟩ prints as LSB→MSB = "10"
        assertTrue(r.getCounts().containsKey("10"));
    }
}

