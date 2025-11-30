package io.quantum4j.transpile;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.circuit.Instruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages and applies a sequence of transpiler passes to a quantum circuit.
 * <p>
 * Architecture rules enforced:
 * <ul>
 *   <li>Input circuits are deep-cloned before any pass runs.</li>
 *   <li>Passes are executed sequentially in insertion order.</li>
 *   <li>Pass names must be non-null/non-empty for traceability.</li>
 *   <li>Pass outputs are fed as inputs to the next pass.</li>
 * </ul>
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
        if (pass == null) {
            throw new IllegalArgumentException("pass must not be null");
        }
        String name = pass.name();
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("pass name must not be null or empty");
        }
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
     * Run all passes sequentially on the given circuit. The input circuit is deep-cloned before any transformation to
     * preserve immutability guarantees.
     *
     * @param circuit input circuit (not mutated)
     * @return transformed circuit after all passes
     */
    public QuantumCircuit run(QuantumCircuit circuit) {
        if (circuit == null) {
            throw new IllegalArgumentException("circuit must not be null");
        }
        QuantumCircuit current = cloneCircuit(circuit);
        for (TranspilerPass pass : passes) {
            current = pass.apply(current);
        }
        return current;
    }

    /**
     * Deep clone a circuit by copying all instructions.
     */
    private QuantumCircuit cloneCircuit(QuantumCircuit circuit) {
        QuantumCircuit copy = QuantumCircuit.create(circuit.getNumQubits());
        for (Instruction inst : circuit.getInstructions()) {
            copy.addInstruction(inst.copy());
        }
        return copy;
    }
}
