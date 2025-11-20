package io.quantum4j.core.gates;

import io.quantum4j.core.math.Complex;

public final class CCXGate extends ThreeQubitGate {

    public CCXGate() {
        super(makeMatrix());
    }

    private static Complex C(double r) {
        return new Complex(r, 0);
    }

    private static Complex[][] makeMatrix() {
        Complex Z = Complex.ZERO;
        Complex O = Complex.ONE;

        return new Complex[][] {

                // local basis order: |q2 q1 q0>
                // 0: |000>
                { O, Z, Z, Z, Z, Z, Z, Z },

                // 1: |001>
                { Z, O, Z, Z, Z, Z, Z, Z },

                // 2: |010>
                { Z, Z, O, Z, Z, Z, Z, Z },

                // 3: |011> (q2=0, q1=1, q0=1) <-- swap with |111>
                { Z, Z, Z, Z, Z, Z, Z, O },

                // 4: |100>
                { Z, Z, Z, Z, O, Z, Z, Z },

                // 5: |101>
                { Z, Z, Z, Z, Z, O, Z, Z },

                // 6: |110>
                { Z, Z, Z, Z, Z, Z, O, Z },

                // 7: |111> (q2=1, q1=1, q0=1) <-- swap with |011>
                { Z, Z, Z, O, Z, Z, Z, Z } };
    }

    @Override
    public String name() {
        return "ccx";
    }
}
