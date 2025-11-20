package io.quantum4j.core.gates;

import io.quantum4j.core.math.Complex;

public final class CZGate extends TwoQubitGate {

    public CZGate() {
        super(new Complex[][] { { Complex.ONE, Complex.ZERO, Complex.ZERO, Complex.ZERO },
                { Complex.ZERO, Complex.ONE, Complex.ZERO, Complex.ZERO },
                { Complex.ZERO, Complex.ZERO, Complex.ONE, Complex.ZERO },
                { Complex.ZERO, Complex.ZERO, Complex.ZERO, new Complex(-1, 0) } });
    }

    @Override
    public String name() {
        return "cz";
    }
}
