package com.quantum4j.visualization;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.StandardGates;

/**
 * SVG renderer for QuantumCircuit.
 */
public final class CircuitSvgRenderer {
    private static final int WIRE_SPACING = 40;
    private static final int COLUMN_SPACING = 70;
    private static final int GATE_W = 40;
    private static final int GATE_H = 30;
    private static final int MARGIN = 20;

    private CircuitSvgRenderer() {
    }

    public static String render(QuantumCircuit circuit) {
        int nQ = circuit.getNumQubits();
        int cols = circuit.getInstructions().size();
        int width = MARGIN * 2 + Math.max(1, cols) * COLUMN_SPACING;
        int height = MARGIN * 2 + Math.max(1, nQ - 1) * WIRE_SPACING + 40;

        StringBuilder sb = new StringBuilder();
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(width).append("\" height=\"")
                .append(height).append("\" font-family=\"Arial\" font-size=\"12\">");
        sb.append("<style>.wire{stroke:black;stroke-width:2;} .gate{fill:white;stroke:black;stroke-width:2;} ")
                .append(".text{dominant-baseline:middle;text-anchor:middle;}</style>");

        // wires
        for (int q = 0; q < nQ; q++) {
            int y = MARGIN + q * WIRE_SPACING;
            sb.append("<line class=\"wire\" x1=\"").append(MARGIN).append("\" y1=\"").append(y).append("\" x2=\"")
                    .append(width - MARGIN).append("\" y2=\"").append(y).append("\" />");
        }

        int colIdx = 0;
        for (Instruction inst : circuit.getInstructions()) {
            int xCenter = MARGIN + colIdx * COLUMN_SPACING;
            if (inst.getType() == Instruction.Type.GATE) {
                Gate g = inst.getGate();
                int[] qs = inst.getQubits();
                if (g instanceof StandardGates.CNOTGate || g instanceof StandardGates.CZGate
                        || g instanceof StandardGates.SWAPGate) {
                    int c = qs[0];
                    int t = qs[1];
                    int yC = MARGIN + c * WIRE_SPACING;
                    int yT = MARGIN + t * WIRE_SPACING;
                    int y1 = Math.min(yC, yT);
                    int y2 = Math.max(yC, yT);
                    sb.append("<line class=\"wire\" x1=\"").append(xCenter).append("\" y1=\"").append(y1)
                            .append("\" x2=\"").append(xCenter).append("\" y2=\"").append(y2).append("\" />");
                    if (g instanceof StandardGates.CNOTGate) {
                        sb.append("<circle cx=\"").append(xCenter).append("\" cy=\"").append(yC)
                                .append("\" r=\"5\" fill=\"black\" />");
                        drawX(sb, xCenter, yT);
                    } else if (g instanceof StandardGates.CZGate) {
                        sb.append("<circle cx=\"").append(xCenter).append("\" cy=\"").append(yC)
                                .append("\" r=\"5\" fill=\"black\" />");
                        drawGateBox(sb, xCenter, yT, "Z");
                    } else { // swap
                        drawSwap(sb, xCenter, yC);
                        drawSwap(sb, xCenter, yT);
                    }
                } else {
                    int q = qs[0];
                    int y = MARGIN + q * WIRE_SPACING;
                    String label = GateSymbol.label(g);
                    drawGateBox(sb, xCenter, y, label);
                }
            } else if (inst.getType() == Instruction.Type.MEASURE) {
                int q = inst.getQubits()[0];
                int y = MARGIN + q * WIRE_SPACING;
                drawGateBox(sb, xCenter, y, "M");
            }
            colIdx++;
        }

        sb.append("</svg>");
        return sb.toString();
    }

    private static void drawGateBox(StringBuilder sb, int xCenter, int yCenter, String label) {
        int x = xCenter - GATE_W / 2;
        int y = yCenter - GATE_H / 2;
        sb.append("<rect class=\"gate\" x=\"").append(x).append("\" y=\"").append(y).append("\" width=\"")
                .append(GATE_W).append("\" height=\"").append(GATE_H).append("\" rx=\"4\" />");
        sb.append("<text class=\"text\" x=\"").append(xCenter).append("\" y=\"").append(yCenter).append("\">")
                .append(escape(label)).append("</text>");
    }

    private static void drawX(StringBuilder sb, int xCenter, int yCenter) {
        int r = 10;
        sb.append("<circle cx=\"").append(xCenter).append("\" cy=\"").append(yCenter).append("\" r=\"").append(r)
                .append("\" stroke=\"black\" fill=\"white\" />");
        sb.append("<line class=\"wire\" x1=\"").append(xCenter).append("\" y1=\"").append(yCenter - r)
                .append("\" x2=\"").append(xCenter).append("\" y2=\"").append(yCenter + r).append("\" />");
        sb.append("<line class=\"wire\" x1=\"").append(xCenter - r).append("\" y1=\"").append(yCenter)
                .append("\" x2=\"").append(xCenter + r).append("\" y2=\"").append(yCenter).append("\" />");
    }

    private static void drawSwap(StringBuilder sb, int xCenter, int yCenter) {
        int r = 10;
        sb.append("<line class=\"wire\" x1=\"").append(xCenter - r).append("\" y1=\"").append(yCenter - r)
                .append("\" x2=\"").append(xCenter + r).append("\" y2=\"").append(yCenter + r).append("\" />");
        sb.append("<line class=\"wire\" x1=\"").append(xCenter - r).append("\" y1=\"").append(yCenter + r)
                .append("\" x2=\"").append(xCenter + r).append("\" y2=\"").append(yCenter - r).append("\" />");
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}


