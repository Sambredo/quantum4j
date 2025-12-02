package com.quantum4j.core.gates;

import com.quantum4j.core.math.Complex;
import com.quantum4j.core.math.StateVector;

/**
 * Abstract base class for two-qubit gates.
 *
 * A two-qubit gate is represented by a 4×4 complex matrix in the local basis:
 *
 *   |q0 q1⟩ = |00⟩, |01⟩, |10⟩, |11⟩
 *
 * where q0 is the FIRST qubit argument passed to {@link #apply(StateVector, int, int)}
 * and q1 is the SECOND.
 *
 * Quantum4J uses LSB = qubit 0 in the global state index, but this class
 * does not sort or reorder the qubit indices – the matrix is always applied
 * with respect to the (q0, q1) order given by the caller.
 *
 * This fixes:
 *  - CNOT semantics: control = first argument, target = second
 *  - CH / other asymmetric 2-qubit gates
 *  - QASM Bell state tests
 */
public abstract class TwoQubitGate implements Gate {

    /** 4×4 unitary matrix in local ordering |q0 q1⟩ = |00⟩,|01⟩,|10⟩,|11⟩. */
    protected final Complex[][] matrix;

    /**
     * Construct a two-qubit gate with the given 4×4 matrix.
     *
     * @param matrix a 4×4 complex matrix
     */
    protected TwoQubitGate(Complex[][] matrix) {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Two-qubit matrix must be 4×4");
        }
        this.matrix = matrix;
    }

    @Override
    public int arity() {
        return 2;
    }

    /**
     * Apply this gate to qubits (q0, q1) in that logical order.
     *
     * Local basis ordering:
     *   index 0 → |q0=0, q1=0⟩
     *   index 1 → |q0=0, q1=1⟩
     *   index 2 → |q0=1, q1=0⟩
     *   index 3 → |q0=1, q1=1⟩
     */
    public void apply(StateVector state, int q0, int q1) {
        if (q0 == q1) {
            throw new IllegalArgumentException("Two-qubit gate requires distinct qubits.");
        }

        Complex[] amps = state.getAmplitudes();
        int dim = amps.length;

        int mask0 = 1 << q0;
        int mask1 = 1 << q1;

        // Iterate over all basis states; only operate on the "base" where both bits are 0.
        for (int idx = 0; idx < dim; idx++) {

            if ((idx & mask0) == 0 && (idx & mask1) == 0) {

                // Indices of the 4 basis states for (q0, q1)
                int i00 = idx;                // q0=0, q1=0
                int i01 = idx | mask1;        // q0=0, q1=1
                int i10 = idx | mask0;        // q0=1, q1=0
                int i11 = idx | mask0 | mask1; // q0=1, q1=1

                // Original amplitudes
                Complex a00 = amps[i00];
                Complex a01 = amps[i01];
                Complex a10 = amps[i10];
                Complex a11 = amps[i11];

                // Matrix-vector multiply: new = M * old
                amps[i00] = matrix[0][0].mul(a00)
                             .add(matrix[0][1].mul(a01))
                             .add(matrix[0][2].mul(a10))
                             .add(matrix[0][3].mul(a11));

                amps[i01] = matrix[1][0].mul(a00)
                             .add(matrix[1][1].mul(a01))
                             .add(matrix[1][2].mul(a10))
                             .add(matrix[1][3].mul(a11));

                amps[i10] = matrix[2][0].mul(a00)
                             .add(matrix[2][1].mul(a01))
                             .add(matrix[2][2].mul(a10))
                             .add(matrix[2][3].mul(a11));

                amps[i11] = matrix[3][0].mul(a00)
                             .add(matrix[3][1].mul(a01))
                             .add(matrix[3][2].mul(a10))
                             .add(matrix[3][3].mul(a11));
            }
        }
    }
}

