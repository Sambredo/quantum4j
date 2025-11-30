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

    public PassManager addPass(TranspilerPass pass) {
        passes.add(pass);
        return this;
    }

    public List<TranspilerPass> getPasses() {
        return Collections.unmodifiableList(passes);
    }

    public QuantumCircuit run(QuantumCircuit circuit) {
        QuantumCircuit current = circuit;
        for (TranspilerPass pass : passes) {
            current = pass.apply(current);
        }
        return current;
    }
}
