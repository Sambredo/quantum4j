package com.quantum4j.visualization;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.StandardGates;

/**
 * SVG renderer with dynamic gate box widths and short labels (tooltips carry full params).
 */
public final class CircuitSvgRenderer {
    private static final int WIRE_SPACING = 40;
    private static final int COLUMN_SPACING = 70;
    private static final int MIN_GATE_W = 40;
    private static final int GATE_H = 30;
    private static final int MARGIN = 20;
    private static final int CHAR_W = 7; // crude text width approximation

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
                        filledCircle(sb, xCenter, yC);
                        drawX(sb, xCenter, yT);
                    } else if (g instanceof StandardGates.CZGate) {
                        filledCircle(sb, xCenter, yC);
                        drawGateBox(sb, xCenter, yT, "Z", null);
                    } else { // swap
                        drawSwapCircle(sb, xCenter, yC);
                        drawSwapCircle(sb, xCenter, yT);
                    }
                } else if (g instanceof StandardGates.CCXGate) {
                    int c1 = qs[0];
                    int c2 = qs[1];
                    int t = qs[2];
                    int y1 = MARGIN + c1 * WIRE_SPACING;
                    int y2 = MARGIN + c2 * WIRE_SPACING;
                    int yT = MARGIN + t * WIRE_SPACING;
                    int yMin = Math.min(Math.min(y1, y2), yT);
                    int yMax = Math.max(Math.max(y1, y2), yT);
                    sb.append("<line class=\"wire\" x1=\"").append(xCenter).append("\" y1=\"").append(yMin)
                            .append("\" x2=\"").append(xCenter).append("\" y2=\"").append(yMax).append("\" />");
                    filledCircle(sb, xCenter, y1);
                    filledCircle(sb, xCenter, y2);
                    drawX(sb, xCenter, yT);
                } else {
                    int q = qs[0];
                    int y = MARGIN + q * WIRE_SPACING;
                    String label = GateSymbol.shortLabel(g);
                    String tooltip = GateSymbol.fullLabel(g);
                    drawGateBox(sb, xCenter, y, label, tooltip);
                }
            } else if (inst.getType() == Instruction.Type.MEASURE) {
                int q = inst.getQubits()[0];
                int y = MARGIN + q * WIRE_SPACING;
                drawGateBox(sb, xCenter, y, "M", "Measurement");
            }
            colIdx++;
        }

        sb.append("</svg>");
        return sb.toString();
    }

    public static void writeToFile(QuantumCircuit circuit, java.nio.file.Path svgPath) throws java.io.IOException {
        java.nio.file.Files.createDirectories(svgPath.getParent());
        java.nio.file.Files.writeString(svgPath, render(circuit));
    }

    private static int computeGateWidth(String label) {
        int estimate = label.length() * CHAR_W + 20;
        return Math.max(MIN_GATE_W, estimate);
    }

    private static void drawGateBox(StringBuilder sb, int xCenter, int yCenter, String label, String tooltip) {
        int w = computeGateWidth(label);
        int x = xCenter - w / 2;
        int y = yCenter - GATE_H / 2;
        sb.append("<g>");
        if (tooltip != null) {
            sb.append("<title>").append(escape(tooltip)).append("</title>");
        }
        sb.append("<rect class=\"gate\" x=\"").append(x).append("\" y=\"").append(y).append("\" width=\"")
                .append(w).append("\" height=\"").append(GATE_H).append("\" rx=\"4\" />");
        sb.append("<text class=\"text\" x=\"").append(xCenter).append("\" y=\"").append(yCenter).append("\">")
                .append(escape(label)).append("</text>");
        sb.append("</g>");
    }

    private static void drawX(StringBuilder sb, int xCenter, int yCenter) {
        int r = 10;
        sb.append("<g>");
        sb.append("<circle cx=\"").append(xCenter).append("\" cy=\"").append(yCenter).append("\" r=\"").append(r)
                .append("\" stroke=\"black\" fill=\"white\" />");
        sb.append("<line class=\"wire\" x1=\"").append(xCenter).append("\" y1=\"").append(yCenter - r)
                .append("\" x2=\"").append(xCenter).append("\" y2=\"").append(yCenter + r).append("\" />");
        sb.append("<line class=\"wire\" x1=\"").append(xCenter - r).append("\" y1=\"").append(yCenter)
                .append("\" x2=\"").append(xCenter + r).append("\" y2=\"").append(yCenter).append("\" />");
        sb.append("</g>");
    }

    private static void drawSwapCircle(StringBuilder sb, int xCenter, int yCenter) {
        int r = 10;
        sb.append("<g>");
        sb.append("<circle cx=\"").append(xCenter).append("\" cy=\"").append(yCenter).append("\" r=\"").append(r)
                .append("\" stroke=\"black\" fill=\"white\" />");
        sb.append("<line class=\"wire\" x1=\"").append(xCenter - r / 2).append("\" y1=\"").append(yCenter - r / 2)
                .append("\" x2=\"").append(xCenter + r / 2).append("\" y2=\"").append(yCenter + r / 2).append("\" />");
        sb.append("<line class=\"wire\" x1=\"").append(xCenter - r / 2).append("\" y1=\"").append(yCenter + r / 2)
                .append("\" x2=\"").append(xCenter + r / 2).append("\" y2=\"").append(yCenter - r / 2).append("\" />");
        sb.append("</g>");
    }

    private static void filledCircle(StringBuilder sb, int x, int y) {
        sb.append("<circle cx=\"").append(x).append("\" cy=\"").append(y).append("\" r=\"5\" fill=\"black\" />");
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
