package io.quantum4j.algorithms;

import io.quantum4j.core.circuit.QuantumCircuit;

/**
 * Quantum Fourier Transform (QFT) and inverse QFT (IQFT) circuit builders.
 * Uses only standard gates (H, CX, RZ) with CRZ decomposed as CX-RZ-CX.
 */
public final class QFT {

    private QFT() {
    }

    /**
     * Construct the n-qubit QFT circuit.
     *
     * @param n number of qubits (&gt;=1)
     * @return QFT circuit on n qubits
     */
    public static QuantumCircuit qft(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be > 0");
        QuantumCircuit qc = QuantumCircuit.create(n);

        for (int target = 0; target < n; target++) {
            qc.h(target);
            for (int control = target + 1; control < n; control++) {
                double theta = Math.PI / (1 << (control - target));
                applyCRZ(qc, control, target, theta);
            }
        }

        // Swap to reverse order
        for (int i = 0; i < n / 2; i++) {
            qc.swap(i, n - 1 - i);
        }
        return qc;
    }

    /**
     * Construct the n-qubit inverse QFT circuit.
     *
     * @param n number of qubits (&gt;=1)
     * @return inverse QFT circuit on n qubits
     */
    public static QuantumCircuit inverseQft(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be > 0");
        QuantumCircuit qc = QuantumCircuit.create(n);

        // Reverse swaps first
        for (int i = 0; i < n / 2; i++) {
            qc.swap(i, n - 1 - i);
        }

        for (int target = n - 1; target >= 0; target--) {
            for (int control = target - 1; control >= 0; control--) {
                double theta = -Math.PI / (1 << (target - control));
                applyCRZ(qc, control, target, theta);
            }
            qc.h(target);
        }
        return qc;
    }

    private static void applyCRZ(QuantumCircuit qc, int control, int target, double theta) {
        double half = theta / 2.0;
        qc.u1(control, half);
        qc.cx(control, target);
        qc.u1(target, -half);
        qc.cx(control, target);
    }
}
