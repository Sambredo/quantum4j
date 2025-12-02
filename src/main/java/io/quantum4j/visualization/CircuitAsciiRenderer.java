package io.quantum4j.visualization;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.Gate;
import io.quantum4j.core.gates.StandardGates;

import java.util.ArrayList;
import java.util.List;

/**
 * ASCII renderer for QuantumCircuit.
 */
public final class CircuitAsciiRenderer {

    private CircuitAsciiRenderer() {
    }

    public static String render(QuantumCircuit circuit) {
        int qCount = circuit.getNumQubits();
        List<StringBuilder> rows = new ArrayList<>(qCount);
        for (int q = 0; q < qCount; q++) {
            rows.add(new StringBuilder("q" + q + " "));
        }

        for (Instruction inst : circuit.getInstructions()) {
            int width = 7;
            String label = "";
            if (inst.getType() == Instruction.Type.GATE) {
                label = GateSymbol.label(inst.getGate());
                width = Math.max(7, label.length() + 4);
            }
            List<String> column = new ArrayList<>(qCount);
            for (int q = 0; q < qCount; q++) {
                column.add(repeat('─', width));
            }

            switch (inst.getType()) {
            case GATE:
                Gate g = inst.getGate();
                int[] qs = inst.getQubits();
                if (g instanceof StandardGates.CNOTGate || g instanceof StandardGates.CZGate
                        || g instanceof StandardGates.SWAPGate) {
                    int c = qs[0];
                    int t = qs[1];
                    int min = Math.min(c, t);
                    int max = Math.max(c, t);
                    // draw vertical connectors
                    for (int q = min + 1; q < max; q++) {
                        column.set(q, centerWith('│', width));
                    }
                    if (g instanceof StandardGates.CNOTGate) {
                        column.set(c, centerWith('●', width));
                        column.set(t, gateBox("X", width));
                    } else if (g instanceof StandardGates.CZGate) {
                        column.set(c, centerWith('●', width));
                        column.set(t, gateBox("Z", width));
                    } else { // SWAP
                        column.set(c, centerWith('x', width));
                        column.set(t, centerWith('x', width));
                        // add visible vertical connection
                        for (int q = min; q <= max; q++) {
                            if (q != c && q != t) {
                                column.set(q, centerWith('│', width));
                            }
                        }
                        column.set(c, column.get(c) + "│");
                        column.set(t, column.get(t) + "│");
                    }
                } else {
                    column.set(qs[0], gateBox(label, width));
                }
                break;
            case MEASURE:
                int mq = inst.getQubits()[0];
                column.set(mq, gateBox("M", width));
                break;
            default:
                break;
            }

            for (int q = 0; q < qCount; q++) {
                rows.get(q).append(column.get(q));
            }
        }

        StringBuilder out = new StringBuilder();
        for (int q = 0; q < rows.size(); q++) {
            out.append(rows.get(q));
            if (q < rows.size() - 1)
                out.append(System.lineSeparator());
        }
        return out.toString();
    }

    private static String gateBox(String label, int width) {
        String inner = padCenter(label, width - 4);
        return "──┤" + inner + "├";
    }

    private static String padCenter(String s, int len) {
        if (s.length() >= len)
            return s.substring(0, len);
        int left = (len - s.length()) / 2;
        int right = len - s.length() - left;
        return repeat(' ', left) + s + repeat(' ', right);
    }

    private static String centerWith(char c, int width) {
        char[] arr = repeat('─', width).toCharArray();
        arr[width / 2] = c;
        return new String(arr);
    }

    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++)
            sb.append(c);
        return sb.toString();
    }
}
