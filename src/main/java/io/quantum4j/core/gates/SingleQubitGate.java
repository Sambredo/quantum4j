package io.quantum4j.core.gates;

import io.quantum4j.core.math.Complex;
import io.quantum4j.core.math.StateVector;

/**
 * Abstract base class for single-qubit gates.
 * <p>
 * A single-qubit gate is represented by a 2×2 complex matrix. Subclasses provide specific gate matrices (e.g.,
 * Hadamard, Pauli X, Y, Z).
 * </p>
 */
public abstract class SingleQubitGate implements Gate {

    /** 2x2 unitary matrix representing this gate */
    protected final Complex[][] matrix;

    /**
     * Construct a single-qubit gate with the given 2×2 matrix.
     *
     * @param matrix
     *            a 2×2 complex matrix
     *
     * @throws IllegalArgumentException
     *             if the matrix is not 2×2
     */
    protected SingleQubitGate(Complex[][] matrix) {
        if (matrix.length != 2 || matrix[0].length != 2 || matrix[1].length != 2) {
            throw new IllegalArgumentException("Single qubit gate must be 2x2");
        }
        this.matrix = matrix;
    }

    @Override
    public int arity() {
        return 1;
    }

    /**
     * Apply this single-qubit gate to the specified target qubit in the given state.
     * <p>
     * This implementation performs sparse matrix-vector multiplication by updating only the amplitude pairs affected by
     * the target qubit.
     * </p>
     *
     * @param state
     *            the quantum state to modify
     * @param targetQubit
     *            the index of the qubit to apply the gate to (0 = LSB)
     *
     * @throws IllegalArgumentException
     *             if targetQubit is out of range
     */
    public void apply(StateVector state, int targetQubit) {
        int nQubits = state.getNumQubits();
        if (targetQubit < 0 || targetQubit >= nQubits) {
            throw new IllegalArgumentException("Invalid target qubit: " + targetQubit);
        }

        Complex[] amps = state.getAmplitudes();
        int dim = amps.length;

        int bitMask = 1 << targetQubit;

        for (int i = 0; i < dim; i++) {
            // only process pairs where target bit is 0; partner index has bit set to 1
            if ((i & bitMask) == 0) {
                int j = i | bitMask;

                Complex a0 = amps[i];
                Complex a1 = amps[j];

                // new amplitudes = U * [a0, a1]^T
                Complex new0 = matrix[0][0].mul(a0).add(matrix[0][1].mul(a1));
                Complex new1 = matrix[1][0].mul(a0).add(matrix[1][1].mul(a1));

                amps[i] = new0;
                amps[j] = new1;
            }
        }
    }
}
