package com.quantum4j.core.gates;

import com.quantum4j.core.math.Complex;

/**
 * Pre-computed matrix representations for standard quantum gates.
 *
 * This class contains unitary matrices for:
 *  - Single-qubit gates
 *  - Parametric rotation gates
 *  - 2-qubit gates (CNOT, CZ, SWAP, ISWAP, CH)
 *  - 3-qubit CCX (Toffoli)
 *
 * NOTE: CNOT HAS BEEN FIXED to match Quantum4J's TwoQubitGate.apply()
 * qubit-bit ordering (control=first argument, target=second).
 */
public final class GateMatrices {

    private GateMatrices() {}

    private static final double INV_SQRT2 = 1.0 / Math.sqrt(2.0);

    // ----------------------------------------------------------------------
    // 1-QUBIT FIXED MATRICES
    // ----------------------------------------------------------------------

    public static final Complex[][] X = {
            { c(0), c(1) },
            { c(1), c(0) }
    };

    public static final Complex[][] Y = {
            { c(0), ci(-1) },
            { ci(1), c(0) }
    };

    public static final Complex[][] Z = {
            { c(1), c(0) },
            { c(0), c(-1) }
    };

    public static final Complex[][] H = {
            { c(INV_SQRT2), c(INV_SQRT2) },
            { c(INV_SQRT2), c(-INV_SQRT2) }
    };

    public static final Complex[][] S = {
            { c(1), c(0) },
            { c(0), ci(1) }
    };

    public static final Complex[][] T = {
            { c(1), c(0) },
            { c(0), new Complex(Math.cos(Math.PI / 4), Math.sin(Math.PI / 4)) }
    };

    // ----------------------------------------------------------------------
    // PARAMETERIZED ROTATIONS
    // ----------------------------------------------------------------------

    public static Complex[][] RX(double theta) {
        double ct = Math.cos(theta / 2);
        double st = Math.sin(theta / 2);
        return new Complex[][] {
                { c(ct), ci(-st) },
                { ci(-st), c(ct) }
        };
    }

    public static Complex[][] RY(double theta) {
        double ct = Math.cos(theta / 2);
        double st = Math.sin(theta / 2);
        return new Complex[][] {
                { c(ct), c(-st) },
                { c(st), c(ct) }
        };
    }

    public static Complex[][] RZ(double theta) {
        double ct = Math.cos(theta / 2);
        double st = Math.sin(theta / 2);
        return new Complex[][] {
                { new Complex(ct, -st), c(0) },
                { c(0), new Complex(ct, st) }
        };
    }

    // ----------------------------------------------------------------------
    // 2-QUBIT GATES (4x4)
    // ----------------------------------------------------------------------
    //
    // FIXED CNOT to match TwoQubitGate.apply()'s basis layout:
    //
    //   TwoQubitGate uses indexing where the *first qubit* argument
    //   is the LESS significant bit (LSB) in basis ordering.
    //
    // Basis order inside apply():
    //   |00>, |01>, |10>, |11>
    //   where (control = q0), (target = q1).
    //
    // CNOT transform:
    //   |00> -> |00>
    //   |01> -> |01>
    //   |10> -> |11>
    //   |11> -> |10>
    //
    // ----------------------------------------------------------------------

    public static final Complex[][] CNOT = {
            { c(1), c(0), c(0), c(0) }, // |00> -> |00>
            { c(0), c(1), c(0), c(0) }, // |01> -> |01>
            { c(0), c(0), c(0), c(1) }, // |10> -> |11>
            { c(0), c(0), c(1), c(0) }  // |11> -> |10>
    };

    public static final Complex[][] CZ = {
            { c(1), c(0), c(0), c(0) },
            { c(0), c(1), c(0), c(0) },
            { c(0), c(0), c(1), c(0) },
            { c(0), c(0), c(0), c(-1) }
    };

    public static final Complex[][] SWAP = {
            { c(1), c(0), c(0), c(0) },
            { c(0), c(0), c(1), c(0) },
            { c(0), c(1), c(0), c(0) },
            { c(0), c(0), c(0), c(1) }
    };

    public static final Complex[][] ISWAP = {
            { c(1), c(0), c(0), c(0) },
            { c(0), c(0), ci(1), c(0) },
            { c(0), ci(1), c(0), c(0) },
            { c(0), c(0), c(0), c(1) }
    };

    public static final Complex[][] CH = {
            { c(1), c(0), c(0), c(0) },
            { c(0), c(1), c(0), c(0) },
            { c(0), c(0), c(INV_SQRT2), c(INV_SQRT2) },
            { c(0), c(0), c(INV_SQRT2), c(-INV_SQRT2) }
    };

    // ----------------------------------------------------------------------
    // 3-QUBIT CCX (Toffoli)
    // ----------------------------------------------------------------------

    public static final Complex[][] CCX = {
            { c(1), c(0), c(0), c(0), c(0), c(0), c(0), c(0) }, // 000
            { c(0), c(1), c(0), c(0), c(0), c(0), c(0), c(0) }, // 001
            { c(0), c(0), c(1), c(0), c(0), c(0), c(0), c(0) }, // 010
            { c(0), c(0), c(0), c(1), c(0), c(0), c(0), c(0) }, // 011
            { c(0), c(0), c(0), c(0), c(1), c(0), c(0), c(0) }, // 100
            { c(0), c(0), c(0), c(0), c(0), c(1), c(0), c(0) }, // 101
            { c(0), c(0), c(0), c(0), c(0), c(0), c(0), c(1) }, // 110 -> 111
            { c(0), c(0), c(0), c(0), c(0), c(0), c(1), c(0) }  // 111 -> 110
    };

    // ----------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------

    private static Complex c(double r) {
        return new Complex(r, 0);
    }

    private static Complex ci(double i) {
        return new Complex(0, i);
    }
}


