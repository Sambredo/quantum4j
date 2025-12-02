package com.quantum4j.examples;

import com.quantum4j.core.backend.BackendFactory;
import com.quantum4j.core.backend.BackendType;
import com.quantum4j.core.backend.Result;
import com.quantum4j.core.backend.RunOptions;
import com.quantum4j.core.backend.hardware.IonQBackend;
import com.quantum4j.core.circuit.QuantumCircuit;

/**
 * Submit a small 2-qubit Grover circuit to a hardware backend (IonQ).
 * <p>
 * NOTE: Requires environment variable IONQ_API_KEY to be set. Running this example may incur cloud costs.
 * </p>
 */
public final class GroverHardwareExample {

    public static void main(String[] args) {
        String apiKey = System.getenv("IONQ_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("IONQ_API_KEY not set in environment");
        }

        BackendFactory.register(BackendType.HARDWARE, new IonQBackend(apiKey));

        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0).h(1)
                .cz(0, 1)
                .h(0).h(1)
                .x(0).x(1)
                .cz(0, 1)
                .x(0).x(1)
                .h(0).h(1)
                .measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.HARDWARE).withShots(500));
        System.out.println("Hardware result: " + r.getCounts());
    }
}

