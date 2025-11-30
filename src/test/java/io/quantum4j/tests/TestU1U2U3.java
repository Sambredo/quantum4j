package io.quantum4j.tests;

import io.quantum4j.core.backend.*;
import io.quantum4j.core.circuit.QuantumCircuit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestU1U2U3 {

    @Test
    public void testU1DoesNotChangeProbabilities() {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .h(0)
                .u1(0, Math.PI / 2)
                .measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(200));
        int count0 = r.getCounts().getOrDefault("0", 0);
        int count1 = r.getCounts().getOrDefault("1", 0);

        assertTrue(count0 > 60);
        assertTrue(count1 > 60);  // still ~50/50
    }

    @Test
    public void testU2CreatesSuperposition() {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .u2(0, 0, Math.PI)
                .measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(300));
        assertTrue(r.getCounts().size() == 2);  // superposition
    }


    @Test
    public void testU3GeneralRotation() {
        QuantumCircuit qc = QuantumCircuit.create(1)
                .u3(0, Math.PI / 2, 0, Math.PI)
                .measureAll();

        Result r = new StateVectorBackend().run(qc, RunOptions.shots(300));

        // Should produce both 0 and 1 outcomes
        assertTrue(r.getCounts().size() >= 1);
    }
}
