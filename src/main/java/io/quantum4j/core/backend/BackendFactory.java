package io.quantum4j.core.backend;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry and factory for backend instances.
 */
public final class BackendFactory {
    private static final Map<BackendType, Backend> registry = new ConcurrentHashMap<>();

    static {
        registry.put(BackendType.STATEVECTOR, new StateVectorBackend());
    }

    private BackendFactory() {
    }

    /**
     * Get a backend by type.
     *
     * @param type backend type
     * @return backend instance or null if not registered
     */
    public static Backend get(BackendType type) {
        Backend backend = registry.get(type);
        if (backend == null && type == BackendType.HARDWARE) {
            throw new IllegalStateException("No hardware backend registered. Please call BackendFactory.register().");
        }
        return backend;
    }

    /**
     * Register a backend for a given type.
     *
     * @param type    backend type
     * @param backend backend instance
     */
    public static void register(BackendType type, Backend backend) {
        registry.put(type, backend);
    }
}
