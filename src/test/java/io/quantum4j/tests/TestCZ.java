package io.quantum4j.tests;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestCZ {

    @Test
    public void testCZPhase() {
        QuantumCircuit qc = QuantumCircuit.create(2).x(0).x(1).cz(0, 1).measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));
        assertTrue(r.getCounts().containsKey("11"));
    }
}
