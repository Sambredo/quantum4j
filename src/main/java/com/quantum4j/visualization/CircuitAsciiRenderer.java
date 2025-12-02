package com.quantum4j.visualization;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.StandardGates;

import java.util.ArrayList;
import java.util.List;

/**
 * ASCII renderer using a simple grid-based layout. Only ASCII characters are used to avoid placeholder issues.
 */
public final class CircuitAsciiRenderer {

    private static final int CELL_WIDTH = 8; // fixed width per instruction column

    private CircuitAsciiRenderer() {
    }

    public static String render(QuantumCircuit circuit) {
        int qCount = circuit.getNumQubits();
        List<StringBuilder> rows = new ArrayList<>(qCount);
        for (int q = 0; q < qCount; q++) {
            rows.add(new StringBuilder("q" + q + " "));
        }

        for (Instruction inst : circuit.getInstructions()) {
            List<String> column = new ArrayList<>(qCount);
            for (int i = 0; i < qCount; i++) {
                column.add(repeat('-', CELL_WIDTH));
            }

            if (inst.getType() == Instruction.Type.GATE) {
                Gate g = inst.getGate();
                int[] qs = inst.getQubits();
                if (g instanceof StandardGates.CNOTGate || g instanceof StandardGates.CZGate
                        || g instanceof StandardGates.SWAPGate) {
                    drawTwoQubitGate(column, g, qs[0], qs[1]);
                } else if (g instanceof StandardGates.CCXGate) {
                    drawCCX(column, qs[0], qs[1], qs[2]);
                } else {
                    column.set(qs[0], gateBox(GateSymbol.label(g)));
                }
            } else if (inst.getType() == Instruction.Type.MEASURE) {
                int q = inst.getQubits()[0];
                column.set(q, gateBox("M"));
            }

            for (int q = 0; q < qCount; q++) {
                rows.get(q).append(column.get(q));
            }
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < rows.size(); i++) {
            out.append(rows.get(i));
            if (i < rows.size() - 1) {
                out.append(System.lineSeparator());
            }
        }
        return out.toString();
    }

    private static void drawTwoQubitGate(List<String> col, Gate g, int c, int t) {
        int min = Math.min(c, t);
        int max = Math.max(c, t);
        for (int q = min + 1; q < max; q++) {
            col.set(q, vertical());
        }
        if (g instanceof StandardGates.CNOTGate) {
            col.set(c, controlCell());
            col.set(t, gateBox("X"));
        } else if (g instanceof StandardGates.CZGate) {
            col.set(c, controlCell());
            col.set(t, gateBox("Z"));
        } else { // SWAP
            col.set(c, addVertical(swapCell()));
            col.set(t, addVertical(swapCell()));
            for (int q = min + 1; q < max; q++) {
                col.set(q, vertical());
            }
        }
        // ensure vertical bar visible even when adjacent
        if (max - min == 1) {
            if (!(g instanceof StandardGates.SWAPGate)) {
                col.set(min, addVertical(col.get(min)));
                col.set(max, addVertical(col.get(max)));
            }
        }
    }

    private static void drawCCX(List<String> col, int c1, int c2, int t) {
        int min = Math.min(Math.min(c1, c2), t);
        int max = Math.max(Math.max(c1, c2), t);
        for (int q = min + 1; q < max; q++) {
            col.set(q, vertical());
        }
        col.set(c1, controlCell());
        col.set(c2, controlCell());
        col.set(t, gateBox("X"));
        if (max - min == 2) {
            col.set(min, addVertical(col.get(min)));
            col.set(max, addVertical(col.get(max)));
        }
    }

    private static String gateBox(String label) {
        String inner = padCenter(label, CELL_WIDTH - 4);
        return "--[" + inner + "]";
    }

    private static String padCenter(String s, int len) {
        if (s.length() >= len) {
            return s.substring(0, len);
        }
        int left = (len - s.length()) / 2;
        int right = len - s.length() - left;
        return repeat(' ', left) + s + repeat(' ', right);
    }

    private static String vertical() {
        char[] arr = repeat('-', CELL_WIDTH).toCharArray();
        arr[CELL_WIDTH / 2] = '|';
        return new String(arr);
    }

    private static String controlCell() {
        char[] arr = repeat('-', CELL_WIDTH).toCharArray();
        int idx = CELL_WIDTH / 2;
        arr[idx] = 'o';
        if (idx > 0) arr[idx - 1] = '|';
        if (idx < CELL_WIDTH - 1) arr[idx + 1] = '|';
        return new String(arr);
    }

    private static String swapCell() {
        char[] arr = repeat('-', CELL_WIDTH).toCharArray();
        int idx = CELL_WIDTH / 2;
        arr[idx] = 'x';
        return new String(arr);
    }

    private static String addVertical(String s) {
        char[] arr = s.toCharArray();
        int idx = CELL_WIDTH / 2;
        if (arr[idx] == '-') {
            arr[idx] = '|';
        } else if (idx > 0 && arr[idx - 1] == '-') {
            arr[idx - 1] = '|';
        } else if (idx < arr.length - 1 && arr[idx + 1] == '-') {
            arr[idx + 1] = '|';
        }
        return new String(arr);
    }

    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
