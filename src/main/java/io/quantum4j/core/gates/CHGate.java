package io.quantum4j.core.gates;

import io.quantum4j.core.math.Complex;

public final class CHGate extends TwoQubitGate {

    public CHGate() {
        super(new Complex[][] { { Complex.ONE, Complex.ZERO, Complex.ZERO, Complex.ZERO },
                { Complex.ZERO, Complex.ONE, Complex.ZERO, Complex.ZERO },
                { Complex.ZERO, Complex.ZERO, GateMatrices.H[0][0], GateMatrices.H[0][1] },
                { Complex.ZERO, Complex.ZERO, GateMatrices.H[1][0], GateMatrices.H[1][1] } });
    }

    @Override
    public String name() {
        return "ch";
    }
}
