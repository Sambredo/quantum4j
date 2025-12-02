package com.quantum4j.core.backend.hardware;

import com.quantum4j.core.backend.Backend;
import com.quantum4j.core.backend.Result;
import com.quantum4j.core.backend.RunOptions;

/**
 * Hardware backend abstraction. Implementations submit circuits to vendor hardware services using OpenQASM 2.0.
 * <p>
 * Implementations must not mutate the provided {@link io.quantum4j.core.circuit.QuantumCircuit}; if transformation is
 * required, clone the circuit first. Each backend is responsible for packaging QASM into a vendor payload, submitting
 * jobs, polling results, and parsing vendor JSON into a {@link Result}.
 * </p>
 */
public interface HardwareBackend extends Backend {
    /**
     * Vendor name (e.g., "IonQ", "IBM").
     */
    String vendor();

    /**
     * Convert a QASM program into a vendor-specific payload.
     *
     * @param qasm    QASM string
     * @param options run options
     * @return JSON payload
     */
    String toVendorPayload(String qasm, RunOptions options);

    /**
     * Parse a vendor JSON response into a Result.
     *
     * @param json vendor JSON
     * @return Result
     */
    Result parseVendorResult(String json);
}

