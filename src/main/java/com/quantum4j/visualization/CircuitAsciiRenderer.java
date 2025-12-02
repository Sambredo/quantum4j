package com.quantum4j.visualization;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.StandardGates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic ASCII renderer with raw (ASCII-safe) and pretty (Unicode) modes.
 */
public final class CircuitAsciiRenderer {
    private static final int MIN_CELL_WIDTH = 7;

    private CircuitAsciiRenderer() {
    }

    public static String render(QuantumCircuit circuit) {
        return render(circuit, false);
    }

    public static String render(QuantumCircuit circuit, boolean pretty) {
        int qCount = circuit.getNumQubits();
        List<Instruction> instructions = circuit.getInstructions();
        List<StringBuilder> rows = new ArrayList<>(qCount);
        for (int q = 0; q < qCount; q++) {
            rows.add(new StringBuilder("q" + q + " "));
        }

        char wire = pretty ? '─' : '-';
        char vert = pretty ? '│' : '|';
        char ctrl = pretty ? '●' : '@';
        char targ = pretty ? '⊕' : '+';
        char swap = pretty ? '×' : 'x';

        for (Instruction inst : instructions) {
            int width = columnWidth(inst);
            char[][] col = new char[qCount][width];
            for (int r = 0; r < qCount; r++) {
                for (int c = 0; c < width; c++) col[r][c] = wire;
            }

            if (inst.getType() == Instruction.Type.GATE) {
                Gate g = inst.getGate();
                if (g instanceof StandardGates.CNOTGate) {
                    int c = inst.getQubits()[0], t = inst.getQubits()[1];
                    placeControlTarget(col, c, t, ctrl, targ, vert);
                } else if (g instanceof StandardGates.CZGate) {
                    int c = inst.getQubits()[0], t = inst.getQubits()[1];
                    placeControlBox(col, c, t, "Z", ctrl, vert);
                } else if (g instanceof StandardGates.SWAPGate) {
                    int q1 = inst.getQubits()[0], q2 = inst.getQubits()[1];
                    placeSwap(col, q1, q2, swap, vert);
                } else if (g instanceof StandardGates.CCXGate) {
                    int c1 = inst.getQubits()[0], c2 = inst.getQubits()[1], t = inst.getQubits()[2];
                    placeToffoli(col, c1, c2, t, ctrl, targ, vert);
                } else {
                    placeBox(col, inst.getQubits()[0], GateSymbol.shortLabel(g));
                }
            } else if (inst.getType() == Instruction.Type.MEASURE) {
                placeBox(col, inst.getQubits()[0], "M");
            }

            for (int r = 0; r < qCount; r++) rows.get(r).append(col[r]);
        }

        StringBuilder out = new StringBuilder();
        for (int r = 0; r < rows.size(); r++) {
            out.append(rows.get(r));
            if (r < rows.size() - 1) out.append(System.lineSeparator());
        }
        return out.toString();
    }

    public static void writeToFile(QuantumCircuit circuit, Path asciiPath, boolean pretty) throws IOException {
        Files.createDirectories(asciiPath.getParent());
        Files.writeString(asciiPath, render(circuit, pretty));
    }

    private static int columnWidth(Instruction inst) {
        if (inst.getType() == Instruction.Type.GATE) {
            Gate g = inst.getGate();
            if (g instanceof StandardGates.CNOTGate || g instanceof StandardGates.CZGate
                    || g instanceof StandardGates.SWAPGate || g instanceof StandardGates.CCXGate) {
                return MIN_CELL_WIDTH;
            }
            String label = GateSymbol.shortLabel(g);
            return Math.max(MIN_CELL_WIDTH, label.length() + 4);
        } else if (inst.getType() == Instruction.Type.MEASURE) {
            return MIN_CELL_WIDTH;
        }
        return MIN_CELL_WIDTH;
    }

    private static void placeBox(char[][] col, int qubit, String label) {
        String box = "[" + label + "]";
        int width = col[0].length;
        int start = Math.max(0, (width - box.length()) / 2);
        for (int i = 0; i < box.length() && start + i < width; i++) {
            col[qubit][start + i] = box.charAt(i);
        }
    }

    private static void placeControlTarget(char[][] col, int control, int target, char ctrl, char targ, char vert) {
        int center = col[0].length / 2;
        col[control][center] = ctrl;
        col[target][center] = targ;
        connect(col, control, target, center, vert);
    }

    private static void placeControlBox(char[][] col, int control, int target, String label, char ctrl, char vert) {
        int center = col[0].length / 2;
        col[control][center] = ctrl;
        placeBox(col, target, label);
        connect(col, control, target, center, vert);
    }

    private static void placeSwap(char[][] col, int q1, int q2, char swapChar, char vert) {
        int center = col[0].length / 2;
        col[q1][center] = swapChar;
        col[q2][center] = swapChar;
        connect(col, q1, q2, center, vert);
    }

    private static void placeToffoli(char[][] col, int c1, int c2, int target, char ctrl, char targ, char vert) {
        int center = col[0].length / 2;
        col[c1][center] = ctrl;
        col[c2][center] = ctrl;
        col[target][center] = targ;
        connect(col, c1, target, center, vert);
        connect(col, c2, target, center, vert);
    }

    private static void connect(char[][] col, int q1, int q2, int center, char vert) {
        int min = Math.min(q1, q2);
        int max = Math.max(q1, q2);
        for (int q = min + 1; q < max; q++) {
            col[q][center] = vert;
        }
    }
}
