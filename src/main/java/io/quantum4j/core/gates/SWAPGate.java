package io.quantum4j.core.gates;

import io.quantum4j.core.math.Complex;

public final class SWAPGate extends TwoQubitGate {

    public SWAPGate() {
        super(new Complex[][] { { Complex.ONE, Complex.ZERO, Complex.ZERO, Complex.ZERO },
                { Complex.ZERO, Complex.ZERO, Complex.ONE, Complex.ZERO },
                { Complex.ZERO, Complex.ONE, Complex.ZERO, Complex.ZERO },
                { Complex.ZERO, Complex.ZERO, Complex.ZERO, Complex.ONE } });
    }

    @Override
    public String name() {
        return "swap";
    }
}
