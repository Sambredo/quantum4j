package com.quantum4j.visualization;

import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.StandardGates;

/**
 * Maps gates to plain ASCII labels for rendering.
 */
public final class GateSymbol {
    private GateSymbol() {
    }

    public static String label(Gate g) {
        if (g instanceof StandardGates.HGate) return "H";
        if (g instanceof StandardGates.XGate) return "X";
        if (g instanceof StandardGates.YGate) return "Y";
        if (g instanceof StandardGates.ZGate) return "Z";
        if (g instanceof StandardGates.RZGate) {
            double theta = ((StandardGates.RZGate) g).getTheta();
            if (Math.abs(theta - Math.PI / 4) < 1e-9) return "RZ(pi/4)";
            if (Math.abs(theta - Math.PI / 2) < 1e-9) return "RZ(pi/2)";
            return "RZ(" + theta + ")";
        }
        if (g instanceof StandardGates.RXGate) return "RX";
        if (g instanceof StandardGates.RYGate) return "RY";
        if (g instanceof StandardGates.CNOTGate) return "CX";
        if (g instanceof StandardGates.CZGate) return "CZ";
        if (g instanceof StandardGates.SWAPGate) return "SWAP";
        return g.name();
    }
}

