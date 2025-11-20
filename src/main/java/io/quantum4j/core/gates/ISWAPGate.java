package io.quantum4j.core.gates;

import io.quantum4j.core.math.Complex;

public final class ISWAPGate extends TwoQubitGate {

    private static Complex C(double r) {
        return new Complex(r, 0);
    }

    private static Complex CI(double r) {
        return new Complex(0, r); // imaginary amplitude
    }

    public ISWAPGate() {
        super(new Complex[][] {

                // |00>, |01>, |10>, |11>
                { C(1), C(0), C(0), C(0) }, { C(0), C(0), CI(1), C(0) }, { C(0), CI(1), C(0), C(0) },
                { C(0), C(0), C(0), C(1) } });
    }

    @Override
    public String name() {
        return "iswap";
    }
}
