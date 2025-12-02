package com.quantum4j.transpile;

import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.transpile.passes.CXCancellationPass;
import com.quantum4j.transpile.passes.RotationFusionPass;
import com.quantum4j.transpile.passes.SwapDecompositionPass;
import com.quantum4j.transpile.passes.U3DecompositionPass;

/**
 * Default transpiler pipeline for Quantum4J.
 * <p>
 * Applies a deterministic sequence of passes that are safe for round-tripping and further optimization:
 * <ol>
 *   <li>SwapDecompositionPass</li>
 *   <li>CXCancellationPass</li>
 *   <li>U3DecompositionPass</li>
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
                .addPass(new U3DecompositionPass())
                .addPass(new RotationFusionPass())
                .run(circuit);
    }
}

