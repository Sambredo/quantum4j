package com.quantum4j.visualization;

import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.StandardGates;

/**
 * Maps gates to plain ASCII labels for rendering.
 */
public final class GateSymbol {
    private GateSymbol() {
    }

    /**
        * Short label for rendering inside a gate box.
        */
    public static String shortLabel(Gate g) {
        if (g instanceof StandardGates.HGate) return "H";
        if (g instanceof StandardGates.XGate) return "X";
        if (g instanceof StandardGates.YGate) return "Y";
        if (g instanceof StandardGates.ZGate) return "Z";
        if (g instanceof StandardGates.RZGate) return "RZ";
        if (g instanceof StandardGates.RXGate) return "RX";
        if (g instanceof StandardGates.RYGate) return "RY";
        if (g instanceof StandardGates.U1Gate) return "U1";
        if (g instanceof StandardGates.U2Gate) return "U2";
        if (g instanceof StandardGates.U3Gate) return "U3";
        if (g instanceof StandardGates.CNOTGate) return "CX";
        if (g instanceof StandardGates.CZGate) return "CZ";
        if (g instanceof StandardGates.SWAPGate) return "SWAP";
        return g.name();
    }

    /**
     * Full detail label for tooltips.
     */
    public static String fullLabel(Gate g) {
        if (g instanceof StandardGates.RZGate) {
            double theta = ((StandardGates.RZGate) g).getTheta();
            return "RZ(theta=" + theta + ")";
        }
        if (g instanceof StandardGates.RXGate) {
            double theta = ((StandardGates.RXGate) g).getTheta();
            return "RX(theta=" + theta + ")";
        }
        if (g instanceof StandardGates.RYGate) {
            double theta = ((StandardGates.RYGate) g).getTheta();
            return "RY(theta=" + theta + ")";
        }
        if (g instanceof StandardGates.U1Gate) {
            double l = ((StandardGates.U1Gate) g).getLambda();
            return "U1(lambda=" + l + ")";
        }
        if (g instanceof StandardGates.U2Gate) {
            StandardGates.U2Gate u2 = (StandardGates.U2Gate) g;
            return "U2(phi=" + u2.getPhi() + ",lambda=" + u2.getLambda() + ")";
        }
        if (g instanceof StandardGates.U3Gate) {
            StandardGates.U3Gate u3 = (StandardGates.U3Gate) g;
            return "U3(theta=" + u3.getTheta() + ",phi=" + u3.getPhi() + ",lambda=" + u3.getLambda() + ")";
        }
        return shortLabel(g);
    }
}


