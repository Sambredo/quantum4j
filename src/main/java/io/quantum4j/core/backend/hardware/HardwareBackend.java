package io.quantum4j.core.backend.hardware;

import io.quantum4j.core.backend.Backend;
import io.quantum4j.core.backend.Result;
import io.quantum4j.core.backend.RunOptions;

/**
 * Marker interface for hardware backends.
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
