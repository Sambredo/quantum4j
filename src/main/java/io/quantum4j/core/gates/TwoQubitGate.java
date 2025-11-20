package io.quantum4j.core.gates;

import io.quantum4j.core.math.Complex;
import io.quantum4j.core.math.StateVector;

/**
 * Abstract base class for two-qubit gates.
 * <p>
 * A two-qubit gate is represented by a 4×4 complex matrix. Subclasses provide specific gates (e.g., CNOT, CZ, SWAP,
 * ISWAP).
 * </p>
 */
public abstract class TwoQubitGate implements Gate {

    /** 4x4 unitary matrix representing this gate */
    protected final Complex[][] matrix;

    /**
     * Construct a two-qubit gate with the given 4×4 matrix.
     *
     * @param matrix
     *            a 4×4 complex matrix
     *
     * @throws IllegalArgumentException
     *             if the matrix is not 4×4
     */
    protected TwoQubitGate(Complex[][] matrix) {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Two-qubit gate must be 4x4");
        }
        this.matrix = matrix;
    }

    @Override
    public int arity() {
        return 2;
    }

    /**
     * Apply this two-qubit gate to specified control and target qubits.
     * <p>
     * This implementation uses sparse matrix-vector multiplication by processing only the 4-amplitude blocks affected
     * by the control and target qubits. Basis ordering: |00⟩, |01⟩, |10⟩, |11⟩ where control is LSB.
     * </p>
     *
     * @param state
     *            the quantum state to modify
     * @param control
     *            the control qubit index
     * @param target
     *            the target qubit index
     *
     * @throws IllegalArgumentException
     *             if control and target are equal
     */
    public void apply(StateVector state, int control, int target) {
        if (control == target)
            throw new IllegalArgumentException("Two-qubit gate requires distinct qubits.");

        Complex[] amps = state.getAmplitudes();
        int dim = amps.length;

        int maskControl = 1 << control;
        int maskTarget = 1 << target;

        // Loop through full state vector
        for (int idx = 0; idx < dim; idx++) {

            // Only process the "base" state where both bits are 0
            if ((idx & maskControl) == 0 && (idx & maskTarget) == 0) {

                // Calculate indices of the 4 basis combinations
                int i00 = idx;
                int i01 = idx | maskControl;
                int i10 = idx | maskTarget;
                int i11 = idx | maskControl | maskTarget;

                // Original amplitudes
                Complex a00 = amps[i00];
                Complex a01 = amps[i01];
                Complex a10 = amps[i10];
                Complex a11 = amps[i11];

                // New amplitudes after applying the 4x4 gate matrix
                amps[i00] = matrix[0][0].mul(a00).add(matrix[0][1].mul(a01)).add(matrix[0][2].mul(a10))
                        .add(matrix[0][3].mul(a11));

                amps[i01] = matrix[1][0].mul(a00).add(matrix[1][1].mul(a01)).add(matrix[1][2].mul(a10))
                        .add(matrix[1][3].mul(a11));

                amps[i10] = matrix[2][0].mul(a00).add(matrix[2][1].mul(a01)).add(matrix[2][2].mul(a10))
                        .add(matrix[2][3].mul(a11));

                amps[i11] = matrix[3][0].mul(a00).add(matrix[3][1].mul(a01)).add(matrix[3][2].mul(a10))
                        .add(matrix[3][3].mul(a11));
            }
        }
    }
}
