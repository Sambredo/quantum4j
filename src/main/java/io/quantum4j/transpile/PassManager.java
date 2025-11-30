package io.quantum4j.transpile;

import io.quantum4j.core.circuit.QuantumCircuit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages and applies a sequence of transpiler passes to a quantum circuit.
 */
public final class PassManager {

    private final List<TranspilerPass> passes = new ArrayList<>();

    /**
     * Append a transpiler pass to the pipeline.
     *
     * @param pass pass to add
     * @return this manager for chaining
     */
    public PassManager addPass(TranspilerPass pass) {
        passes.add(pass);
        return this;
    }

    /**
     * Get the configured passes in insertion order.
     *
     * @return unmodifiable list of passes
     */
    public List<TranspilerPass> getPasses() {
        return Collections.unmodifiableList(passes);
    }

    /**
     * Run all passes sequentially on the given circuit.
     *
     * @param circuit input circuit (not mutated)
     * @return transformed circuit after all passes
     */
    public QuantumCircuit run(QuantumCircuit circuit) {
        QuantumCircuit current = circuit;
        for (TranspilerPass pass : passes) {
            current = pass.apply(current);
        }
        return current;
    }
}
