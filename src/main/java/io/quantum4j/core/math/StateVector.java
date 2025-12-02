package com.quantum4j.core.math;

import java.util.Arrays;
import java.util.Random;

/**
 * Mutable representation of a quantum state vector.
 * <p>
 * \n * A state vector represents the amplitudes of all computational basis states for a system of n qubits. The
 * dimension is 2^n and amplitudes are stored as complex numbers. Initially, all states are in |00...0⟩.
 * </p>
 */
public final class StateVector {
    private final int numQubits;
    private final Complex[] amplitudes;
    private final Random random;

    /**
     * Create a new state vector with the specified number of qubits.
     * <p>
     * The state is initialized to |00...0⟩ (all zeros).
     * </p>
     *
     * @param numQubits
     *            number of qubits (must be ≥ 1)
     *
     * @throws IllegalArgumentException
     *             if numQubits ≤ 0
     */
    public StateVector(int numQubits) {
        if (numQubits <= 0)
            throw new IllegalArgumentException("numQubits must be > 0");
        this.numQubits = numQubits;
        int dim = 1 << numQubits;
        this.amplitudes = new Complex[dim];
        this.random = new Random();

        // start in |0...0⟩
        amplitudes[0] = Complex.ONE;
        for (int i = 1; i < dim; i++)
            amplitudes[i] = Complex.ZERO;
    }

    /**
     * Get the number of qubits.
     *
     * @return the qubit count
     */
    public int getNumQubits() {
        return numQubits;
    }

    /**
     * Get the dimension of the state vector (2^numQubits).
     *
     * @return the state vector dimension
     */
    public int dimension() {
        return amplitudes.length;
    }

    /**
     * Get the amplitude array (for direct manipulation by gates).
     *
     * @return the complex amplitude array
     */
    public Complex[] getAmplitudes() {
        return amplitudes;
    }

    /**
     * Create a deep copy of this state vector.
     *
     * @return a new StateVector with the same amplitudes
     */
    public StateVector copy() {
        StateVector sv = new StateVector(this.numQubits);
        System.arraycopy(this.amplitudes, 0, sv.amplitudes, 0, amplitudes.length);
        return sv;
    }

    /**
     * Measure all qubits in the computational basis.
     * <p>
     * Collapses the state to the measured outcome (all amplitudes except the chosen basis state are set to zero).
     * Returns the outcome as a bitstring.
     * </p>
     *
     * @return the measurement outcome as a bitstring (e.g., "110")
     *
     * @throws IllegalStateException
     *             if the state vector has zero norm
     */
    public String measureAll() {
        double[] probs = new double[amplitudes.length];
        double sum = 0.0;
        for (int i = 0; i < amplitudes.length; i++) {
            probs[i] = amplitudes[i].absSquared();
            sum += probs[i];
        }
        // normalize
        if (sum == 0.0) {
            throw new IllegalStateException("State vector has zero norm");
        }
        for (int i = 0; i < probs.length; i++) {
            probs[i] /= sum;
        }

        double r = random.nextDouble();
        double cumulative = 0.0;
        int chosen = 0;
        for (; chosen < probs.length; chosen++) {
            cumulative += probs[chosen];
            if (r < cumulative)
                break;
        }

        // collapse
        for (int i = 0; i < amplitudes.length; i++) {
            amplitudes[i] = (i == chosen) ? Complex.ONE : Complex.ZERO;
        }

        return indexToBitString(chosen, numQubits);
    }

    private static String indexToBitString(int index, int numQubits) {
        StringBuilder sb = new StringBuilder(numQubits);
        for (int q = 0; q < numQubits; q++) {
            int bit = (index >> q) & 1; // extract qubit q
            sb.append(bit);
        }
        return sb.toString();
    }

    /**
     * Measure a single qubit in the computational basis.
     * <p>
     * Collapses the qubit to the measured outcome (0 or 1) and updates the amplitudes accordingly. Other qubits are not
     * affected by the measurement.
     * </p>
     *
     * @param qubit
     *            the qubit index to measure
     *
     * @return 0 or 1, the measurement outcome
     *
     * @throws IllegalArgumentException
     *             if qubit index is out of range
     */
    public int measureOne(int qubit) {
        if (qubit < 0 || qubit >= numQubits) {
            throw new IllegalArgumentException("Invalid qubit index: " + qubit);
        }

        Complex[] amps = this.amplitudes;
        int dim = amps.length;
        int mask = 1 << qubit;

        // 1) Compute probabilities for this qubit being 0 or 1
        double p0 = 0.0;
        double p1 = 0.0;
        for (int i = 0; i < dim; i++) {
            double prob = amps[i].absSquared();
            if ((i & mask) == 0) {
                p0 += prob;
            } else {
                p1 += prob;
            }
        }

        double total = p0 + p1;
        if (total == 0.0) {
            throw new IllegalStateException("State has zero norm before measurement");
        }
        p0 /= total;
        p1 /= total;

        // 2) Sample measurement result
        double r = random.nextDouble();
        int result = (r < p0) ? 0 : 1;
        double pRes = (result == 0) ? p0 : p1;

        if (pRes == 0.0) {
            // numerically degenerate: just pick the other outcome
            result = 1 - result;
            pRes = (result == 0) ? p0 : p1;
            if (pRes == 0.0) {
                throw new IllegalStateException("Cannot normalize collapse: zero probability");
            }
        }

        double norm = 1.0 / Math.sqrt(pRes);

        // 3) Collapse + renormalize
        for (int i = 0; i < dim; i++) {
            boolean bitIs1 = (i & mask) != 0;
            if ((result == 0 && !bitIs1) || (result == 1 && bitIs1)) {
                amps[i] = amps[i].mul(norm);
            } else {
                amps[i] = Complex.ZERO;
            }
        }

        return result;
    }

}

