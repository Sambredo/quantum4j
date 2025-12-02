package io.quantum4j.tests.hardware;

import io.quantum4j.core.backend.BackendType;
import io.quantum4j.core.backend.Result;
import io.quantum4j.core.backend.RunOptions;
import io.quantum4j.core.backend.hardware.IonQBackend;
import io.quantum4j.core.backend.hardware.http.HardwareBackendHttpClient;
import io.quantum4j.core.circuit.QuantumCircuit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class IonQBackendTest {

    @AfterEach
    void tearDown() {
        HardwareBackendHttpClient.setMockResponder(null);
    }

    @Test
    void parsesCompletedResults() {
        AtomicInteger call = new AtomicInteger(0);
        HardwareBackendHttpClient.setMockResponder((url, headers) -> {
            if (call.getAndIncrement() == 0) {
                return "{\"id\":\"job1\",\"status\":\"running\"}";
            }
            return "{\"id\":\"job1\",\"status\":\"completed\",\"results\":{\"11\":97,\"00\":3}}";
        });

        QuantumCircuit qc = QuantumCircuit.create(1).h(0).measureAll();
        IonQBackend backend = new IonQBackend("dummy");
        Result r = backend.run(qc, RunOptions.withBackend(BackendType.HARDWARE).withShots(100));

        assertEquals(2, r.getCounts().size());
        assertEquals(97, r.getCounts().get("11"));
        assertEquals(3, r.getCounts().get("00"));
        assertEquals(BackendType.HARDWARE, r.getBackendType());
    }
}
