package io.quantum4j.transpile;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.transpile.passes.CXCancellationPass;
import io.quantum4j.transpile.passes.RotationFusionPass;
import io.quantum4j.transpile.passes.SwapDecompositionPass;

/**
 * Default transpiler pipeline for Quantum4J.
 * <p>
 * Applies a deterministic sequence of passes that are safe for round-tripping and further optimization:
 * <ol>
 *   <li>SwapDecompositionPass</li>
 *   <li>CXCancellationPass</li>
 *   <li>RotationFusionPass</li>
 * </ol>
 * <p>
 * Follows the Quantum4J transpiler architecture rules: no mutation of the input circuit, sequential pass execution,
 * instruction immutability, and pass-chain safety.
 */
public final class DefaultTranspiler {

    private DefaultTranspiler() {
    }

    /**
     * Run the default transpilation pipeline on the given circuit.
     *
     * @param circuit input circuit (not mutated)
     * @return transformed circuit after default passes
     */
    public static QuantumCircuit transpile(QuantumCircuit circuit) {
        return new PassManager()
                .addPass(new SwapDecompositionPass())
                .addPass(new CXCancellationPass())
                .addPass(new RotationFusionPass())
                .run(circuit);
    }
}

