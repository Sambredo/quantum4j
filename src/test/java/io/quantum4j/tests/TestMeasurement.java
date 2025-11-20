package io.quantum4j.tests;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestMeasurement {

    @Test
    public void testMeasureOne() {
        QuantumCircuit qc = QuantumCircuit.create(1).x(0).measure(0, 0);

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(200));

        assertTrue(r.getCounts().containsKey("1"));
    }

    @Test
    public void testMeasureAll() {
        QuantumCircuit qc = QuantumCircuit.create(2).x(1).measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(200));

        assertTrue(r.getCounts().containsKey("01"));
    }
}
