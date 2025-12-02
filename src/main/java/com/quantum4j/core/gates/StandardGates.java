package com.quantum4j.core.gates;

import com.quantum4j.core.math.Complex;

/**
 * Factory class providing standard single- and two-qubit gates.
 *
 * <p>
 * StandardGates contains inner classes for commonly used quantum gates:
 * </p>
 *
 * <ul>
 * <li><b>Pauli gates:</b> X (NOT), Y, Z</li>
 * <li><b>Hadamard:</b> H (superposition)</li>
 * <li><b>Rotations:</b> RX, RY, RZ</li>
 * <li><b>Two-qubit gates:</b> CNOT, CZ, SWAP, ISWAP</li>
 * <li><b>Three-qubit gates:</b> CCX (Toffoli)</li>
 * </ul>
 */
public final class StandardGates {

    private StandardGates() {
    }

    // ----------------------------------------------------------------------
    // Pauli Gates
    // ----------------------------------------------------------------------

    public static final class XGate extends SingleQubitGate {
        public XGate() {
            super(GateMatrices.X);
        }

        @Override
        public String name() {
            return "x";
        }
    }

    public static final class YGate extends SingleQubitGate {
        public YGate() {
            super(GateMatrices.Y);
        }

        @Override
        public String name() {
            return "y";
        }
    }

    public static final class ZGate extends SingleQubitGate {
        public ZGate() {
            super(GateMatrices.Z);
        }

        @Override
        public String name() {
            return "z";
        }
    }

    // ----------------------------------------------------------------------
    // Clifford Gates
    // ----------------------------------------------------------------------

    public static final class HGate extends SingleQubitGate {
        public HGate() {
            super(GateMatrices.H);
        }

        @Override
        public String name() {
            return "h";
        }
    }

    public static final class SGate extends SingleQubitGate {
        public SGate() {
            super(GateMatrices.S);
        }

        @Override
        public String name() {
            return "s";
        }
    }

    public static final class TGate extends SingleQubitGate {
        public TGate() {
            super(GateMatrices.T);
        }

        @Override
        public String name() {
            return "t";
        }
    }

    // ----------------------------------------------------------------------
    // Parameterized Rotation Gates
    // ----------------------------------------------------------------------

    public static final class RXGate extends SingleQubitGate {
        private final double theta;

        public RXGate(double theta) {
            super(GateMatrices.RX(theta));
            this.theta = theta;
        }

        public double getTheta() {
            return theta;
        }

        @Override
        public String name() {
            return "rx";
        }
    }

    public static final class RYGate extends SingleQubitGate {
        private final double theta;

        public RYGate(double theta) {
            super(GateMatrices.RY(theta));
            this.theta = theta;
        }

        public double getTheta() {
            return theta;
        }

        @Override
        public String name() {
            return "ry";
        }
    }

    public static final class RZGate extends SingleQubitGate {
        private final double theta;

        public RZGate(double theta) {
            super(GateMatrices.RZ(theta));
            this.theta = theta;
        }

        public double getTheta() {
            return theta;
        }

        @Override
        public String name() {
            return "rz";
        }
    }

    // ----------------------------------------------------------------------
    // 2-qubit gates
    // ----------------------------------------------------------------------

    public static final class CNOTGate extends TwoQubitGate {
        public CNOTGate() {
            super(GateMatrices.CNOT);
        }

        @Override
        public String name() {
            return "cx";
        }
    }

    public static final class CZGate extends TwoQubitGate {
        public CZGate() {
            super(GateMatrices.CZ);
        }

        @Override
        public String name() {
            return "cz";
        }
    }

    public static final class SWAPGate extends TwoQubitGate {
        public SWAPGate() {
            super(GateMatrices.SWAP);
        }

        @Override
        public String name() {
            return "swap";
        }
    }

    public static final class ISWAPGate extends TwoQubitGate {
        public ISWAPGate() {
            super(GateMatrices.ISWAP);
        }

        @Override
        public String name() {
            return "iswap";
        }
    }

    public static final class CHGate extends TwoQubitGate {
        public CHGate() {
            super(GateMatrices.CH);
        }

        @Override
        public String name() {
            return "ch";
        }
    }

    // ----------------------------------------------------------------------
    // 3-qubit gates
    // ----------------------------------------------------------------------

    public static final class CCXGate extends ThreeQubitGate {
        public CCXGate() {
            super(GateMatrices.CCX);
        }

        @Override
        public String name() {
            return "ccx";
        }
    }

        // ----------------------------------------------------------------------
    //  U-Gates (U1, U2, U3)
    // ----------------------------------------------------------------------

    /**
     * U3(θ, φ, λ):
     * [  cos(θ/2)              , -e^{iλ} sin(θ/2) ]
     * [  e^{iφ} sin(θ/2)       ,  e^{i(φ+λ)} cos(θ/2) ]
     */
    public static final class U3Gate extends SingleQubitGate {

        private final double theta;
        private final double phi;
        private final double lambda;

        public U3Gate(double theta, double phi, double lambda) {
            super(build(theta, phi, lambda));
            this.theta = theta;
            this.phi = phi;
            this.lambda = lambda;
        }

        @Override
        public String name() {
            return "u3";
        }

        public double getTheta() { return theta; }
        public double getPhi() { return phi; }
        public double getLambda() { return lambda; }

        private static Complex[][] build(double theta, double phi, double lambda) {
            double half = theta / 2.0;
            double c = Math.cos(half);
            double s = Math.sin(half);

            Complex ePhi = expI(phi);
            Complex eLambda = expI(lambda);
            Complex ePhiLambda = expI(phi + lambda);

            return new Complex[][] {
                { new Complex(c,0),        eLambda.mul(new Complex(-s,0)) },
                { ePhi.mul(new Complex(s,0)), ePhiLambda.mul(new Complex(c,0)) }
            };
        }
    }


    /**
     * U2(φ, λ) = U3(π/2, φ, λ)
     */
    public static final class U2Gate extends SingleQubitGate {

        private final double phi;
        private final double lambda;

        public U2Gate(double phi, double lambda) {
            super(U3Gate.build(Math.PI/2, phi, lambda));
            this.phi = phi;
            this.lambda = lambda;
        }

        @Override
        public String name() {
            return "u2";
        }

        public double getPhi() { return phi; }
        public double getLambda() { return lambda; }
    }


    /**
     * U1(λ):
     * [[1, 0],
     *  [0, e^{iλ}]]
     */
    public static final class U1Gate extends SingleQubitGate {

        private final double lambda;

        public U1Gate(double lambda) {
            super(build(lambda));
            this.lambda = lambda;
        }

        @Override
        public String name() {
            return "u1";
        }

        public double getLambda() { return lambda; }

        private static Complex[][] build(double lambda) {
            Complex phase = expI(lambda);
            return new Complex[][] {
                { Complex.ONE,  Complex.ZERO },
                { Complex.ZERO, phase }
            };
        }
    }

    // ----------------------------------------------------------------------
    // Helper for exp(iθ)
    // ----------------------------------------------------------------------
    private static Complex expI(double angle) {
        return new Complex(Math.cos(angle), Math.sin(angle));
    }

}


