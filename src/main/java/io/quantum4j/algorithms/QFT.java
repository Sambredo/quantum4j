package io.quantum4j.algorithms;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.transpile.PassManager;
import io.quantum4j.transpile.passes.GateInversionPass;

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
     * @param n number of qubits (>=1)
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
     * Construct the n-qubit inverse QFT circuit as the exact adjoint
     * of {@link #qft(int)} using {@link GateInversionPass}.
     *
     * @param n number of qubits (>=1)
     * @return inverse QFT circuit on n qubits
     */
    public static QuantumCircuit inverseQft(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be > 0");
        QuantumCircuit forward = qft(n);
        return new PassManager()
                .addPass(new GateInversionPass())
                .run(forward);
    }

    private static void applyCRZ(QuantumCircuit qc, int control, int target, double theta) {
        double half = theta / 2.0;
        // Decomposition up to global phase (CU1-style):
        // RZ(target, half); CX; RZ(target, -half); CX; RZ(control, half)
        qc.rz(target, half);
        qc.cx(control, target);
        qc.rz(target, -half);
        qc.cx(control, target);
        qc.rz(control, half);
    }
}
