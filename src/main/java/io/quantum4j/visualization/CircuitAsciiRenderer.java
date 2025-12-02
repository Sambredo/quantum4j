package com.quantum4j.visualization;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.StandardGates;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure ASCII renderer for QuantumCircuit. Uses simple characters (-, |, [, ]) to avoid encoding issues.
 */
public final class CircuitAsciiRenderer {

    private static final int CELL_WIDTH = 9; // consistent spacing per column

    private CircuitAsciiRenderer() {
    }

    public static String render(QuantumCircuit circuit) {
        int qCount = circuit.getNumQubits();
        List<StringBuilder> rows = new ArrayList<>(qCount);
        for (int q = 0; q < qCount; q++) {
            rows.add(new StringBuilder("q" + q + " "));
        }

        for (Instruction inst : circuit.getInstructions()) {
            List<String> col = new ArrayList<>(qCount);
            for (int q = 0; q < qCount; q++) {
                col.add(repeat('-', CELL_WIDTH));
            }

            if (inst.getType() == Instruction.Type.GATE) {
                Gate g = inst.getGate();
                int[] qs = inst.getQubits();

                if (g instanceof StandardGates.CNOTGate || g instanceof StandardGates.CZGate
                        || g instanceof StandardGates.SWAPGate) {
                    int c = qs[0];
                    int t = qs[1];
                    int min = Math.min(c, t);
                    int max = Math.max(c, t);
                    for (int q = min + 1; q < max; q++) {
                        col.set(q, centerWith('|'));
                    }
                    if (g instanceof StandardGates.CNOTGate) {
                        col.set(c, centerWith('o'));
                        col.set(t, gateBox("X"));
                    } else if (g instanceof StandardGates.CZGate) {
                        col.set(c, centerWith('o'));
                        col.set(t, centerWith('o'));
                    } else { // SWAP
                        col.set(c, centerWith('x'));
                        col.set(t, centerWith('x'));
                        for (int q = min + 1; q < max; q++) {
                            col.set(q, centerWith('|'));
                        }
                    }
                } else if (g instanceof StandardGates.CCXGate) {
                    int c1 = qs[0];
                    int c2 = qs[1];
                    int t = qs[2];
                    int min = Math.min(Math.min(c1, c2), t);
                    int max = Math.max(Math.max(c1, c2), t);
                    for (int q = min + 1; q < max; q++) {
                        col.set(q, centerWith('|'));
                    }
                    col.set(c1, centerWith('o'));
                    col.set(c2, centerWith('o'));
                    col.set(t, gateBox("X"));
                } else {
                    String label = GateSymbol.label(g);
                    col.set(qs[0], gateBox(label));
                }
            } else if (inst.getType() == Instruction.Type.MEASURE) {
                int q = inst.getQubits()[0];
                col.set(q, gateBox("M"));
            }

            for (int q = 0; q < qCount; q++) {
                rows.get(q).append(col.get(q));
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

    private static String centerWith(char c) {
        char[] arr = repeat('-', CELL_WIDTH).toCharArray();
        int idx = CELL_WIDTH / 2;
        arr[idx] = c;
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

