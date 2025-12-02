package com.quantum4j.core.gates;

import com.quantum4j.core.math.Complex;
import com.quantum4j.core.math.StateVector;

/**
 * Abstract base class for three-qubit gates.
 *
 * A three-qubit gate is represented by an 8×8 complex matrix in the local basis:
 *
 *   |q0 q1 q2⟩ = |000⟩, |001⟩, ..., |111⟩
 *
 * where q0, q1, q2 are exactly the three qubit indices passed to
 * {@link #apply(StateVector, int, int, int)} in that order.
 *
 * This avoids internal reordering and preserves correct semantics for
 * asymmetric gates like CCX (Toffoli) where the position of controls and
 * target matters.
 */
public abstract class ThreeQubitGate implements Gate {

    /** 8×8 unitary matrix in local ordering |q0 q1 q2⟩. */
    protected final Complex[][] matrix;

    /**
     * Construct a three-qubit gate with the given 8×8 matrix.
     *
     * @param matrix an 8×8 complex matrix
     */
    protected ThreeQubitGate(Complex[][] matrix) {
        if (matrix.length != 8 || matrix[0].length != 8) {
            throw new IllegalArgumentException("Three-qubit gate must be 8×8");
        }
        this.matrix = matrix;
    }

    @Override
    public int arity() {
        return 3;
    }

    /**
     * Apply this gate to qubits (q0, q1, q2) in that logical order.
     *
     * Local basis index mapping:
     *
     *   local index k = (b0 &lt;&lt; 2) | (b1 &lt;&lt; 1) | b2
     *
     * where b0, b1, b2 are the bits of (q0, q1, q2) respectively.
     */
    public void apply(StateVector state, int q0, int q1, int q2) {
        if (q0 == q1 || q0 == q2 || q1 == q2) {
            throw new IllegalArgumentException("Three-qubit gate requires three distinct qubits.");
        }

        Complex[] src = state.getAmplitudes();
        int dim = src.length;
        Complex[] dest = new Complex[dim];

        int m0 = 1 << q0;
        int m1 = 1 << q1;
        int m2 = 1 << q2;
        int maskAll = m0 | m1 | m2;

        // Initialize dest to zero
        for (int i = 0; i < dim; i++) {
            dest[i] = Complex.ZERO;
        }

        // For each global basis index, compute the new amplitude
        for (int globalIdx = 0; globalIdx < dim; globalIdx++) {

            // Extract the three bits in the logical order (q0, q1, q2)
            int b0 = ((globalIdx & m0) != 0) ? 1 : 0;
            int b1 = ((globalIdx & m1) != 0) ? 1 : 0;
            int b2 = ((globalIdx & m2) != 0) ? 1 : 0;

            // local row index: |q0 q1 q2⟩
            int localK = (b0 << 2) | (b1 << 1) | b2;

            // Clear the three bits to get the base index
            int base = globalIdx & ~maskAll;

            // Sum over all columns c (local basis states) for matrix multiplication
            Complex sum = Complex.ZERO;
            for (int c = 0; c < 8; c++) {
                int c0 = (c >> 2) & 1;
                int c1 = (c >> 1) & 1;
                int c2 = c & 1;

                int srcIdx = base
                        | (c0 == 1 ? m0 : 0)
                        | (c1 == 1 ? m1 : 0)
                        | (c2 == 1 ? m2 : 0);

                sum = sum.add(matrix[localK][c].mul(src[srcIdx]));
            }

            dest[globalIdx] = sum;
        }

        // Copy back into the state
        System.arraycopy(dest, 0, src, 0, dim);
    }
}

