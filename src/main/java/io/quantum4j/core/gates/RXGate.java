package io.quantum4j.core.gates;

import io.quantum4j.core.math.Complex;

public final class RXGate extends SingleQubitGate {

    private final double theta;

    public RXGate(double theta) {
        super(createMatrix(theta));
        this.theta = theta;
    }

    public double getTheta() {
        return theta;
    }

    @Override
    public String name() {
        return "rx";
    }

    private static Complex[][] createMatrix(double theta) {
        double ct = Math.cos(theta / 2);
        double st = Math.sin(theta / 2);

        return new Complex[][] { { new Complex(ct, 0), new Complex(0, -st) },
                { new Complex(0, -st), new Complex(ct, 0) } };
    }
}
