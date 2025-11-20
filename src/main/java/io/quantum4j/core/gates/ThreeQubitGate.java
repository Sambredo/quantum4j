package io.quantum4j.core.gates;

import io.quantum4j.core.math.Complex;
import io.quantum4j.core.math.StateVector;

/**
 * Abstract base class for three-qubit gates.
 * <p>
 * A three-qubit gate is represented by an 8×8 complex matrix. Subclasses provide specific gates (e.g., Toffoli/CCX).
 * </p>
 */
public abstract class ThreeQubitGate implements Gate {

    /** 8x8 unitary matrix representing this gate */
    protected final Complex[][] matrix;

    /**
     * Construct a three-qubit gate with the given 8×8 matrix.
     *
     * @param matrix
     *            an 8×8 complex matrix
     *
     * @throws IllegalArgumentException
     *             if the matrix is not 8×8
     */
    protected ThreeQubitGate(Complex[][] matrix) {
        if (matrix.length != 8 || matrix[0].length != 8) {
            throw new IllegalArgumentException("Three-qubit gate must be 8x8");
        }
        this.matrix = matrix;
    }

    @Override
    public int arity() {
        return 3;
    }

    /**
     * Correct 3-qubit gate application.
     *
     * Uses: - sorted qubits to determine bit significance - local index (qH qM qL) from 0..7 - dest[] buffer to avoid
     * in-place corruption
     */
    public void apply(StateVector state, int q0, int q1, int q2) {

        // Sort qubits to determine LSB/MID/MSB in the local 3-qubit space
        int[] qs = { q0, q1, q2 };
        java.util.Arrays.sort(qs);

        int qL = qs[0]; // least significant qubit
        int qM = qs[1];
        int qH = qs[2]; // most significant qubit

        int mL = 1 << qL;
        int mM = 1 << qM;
        int mH = 1 << qH;

        int maskAll = mL | mM | mH;

        Complex[] src = state.getAmplitudes();
        int dim = src.length;
        Complex[] dest = new Complex[dim];

        // Initialize dest to ZERO
        for (int i = 0; i < dim; i++) {
            dest[i] = Complex.ZERO;
        }

        // For each global basis index, compute the new amplitude
        for (int globalIdx = 0; globalIdx < dim; globalIdx++) {

            // Extract the three bits of this triple (qH qM qL)
            int bitL = ((globalIdx & mL) != 0) ? 1 : 0;
            int bitM = ((globalIdx & mM) != 0) ? 1 : 0;
            int bitH = ((globalIdx & mH) != 0) ? 1 : 0;

            // local index 0..7, ordering: |qH qM qL>
            int localK = (bitH << 2) | (bitM << 1) | bitL;

            // For matrix multiplication:
            // base = globalIdx with the three gate bits cleared
            int base = globalIdx & ~maskAll;

            // Compute new amplitude via ∑_c U[k][c] * oldAmp(base + pattern c)
            Complex sum = Complex.ZERO;
            for (int c = 0; c < 8; c++) {

                int cL = c & 1;
                int cM = (c >> 1) & 1;
                int cH = (c >> 2) & 1;

                int srcIdx = base | (cL == 1 ? mL : 0) | (cM == 1 ? mM : 0) | (cH == 1 ? mH : 0);

                sum = sum.add(matrix[localK][c].mul(src[srcIdx]));
            }

            dest[globalIdx] = sum;
        }

        // Write results back to state
        System.arraycopy(dest, 0, src, 0, dim);
    }
}
