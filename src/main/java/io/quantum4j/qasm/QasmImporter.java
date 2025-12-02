package com.quantum4j.qasm;

import com.quantum4j.core.circuit.QuantumCircuit;

/**
 * Strict OpenQASM 2.0 importer for Quantum4J.
 *
 * Supports:
 *  - Header: OPENQASM, include
 *  - qreg/creg
 *  - Single-qubit gates: x,y,z,h,s,t,u1,u2,u3,rx,ry,rz
 *  - Two-qubit: cx,cz,swap,iswap,ch
 *  - Three-qubit: ccx
 *  - measure q[i] -> c[j]
 *
 * Rules:
 *  - Every logical statement must end with a semicolon.
 *  - No trailing garbage allowed (e.g. "h q[0] xyz" is rejected).
 *  - Strict register syntax: q[0], c[1], etc.
 */
public final class QasmImporter {

    private QasmImporter() {}

    // =========================================================================
    // Public API
    // =========================================================================
    public static QuantumCircuit fromQasm(String qasm) {
        if (qasm == null)
            throw new IllegalArgumentException("qasm must not be null");

        String[] lines = stripComments(qasm).split("\n");

        QuantumCircuit circuit = null;
        int numQubits = -1;
        int cregSize = -1;

        StringBuilder buffer = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            buffer.append(trimmed).append(" ");

            // Wait until we see a semicolon
            if (!trimmed.endsWith(";"))
                continue;

            // Full statement acquired
            String stmt = buffer.toString().trim();
            buffer.setLength(0);

            // Drop final semicolon
            stmt = stmt.substring(0, stmt.length() - 1).trim();

            // -------------------------------------------------------------
            // HEADER
            // -------------------------------------------------------------
            if (stmt.startsWith("OPENQASM") || stmt.startsWith("include"))
                continue;

            // -------------------------------------------------------------
            // qreg
            // -------------------------------------------------------------
            if (stmt.startsWith("qreg ")) {
                parseRegisterDecl(stmt, "qreg");
                numQubits = extractRegisterSize(stmt, "qreg");
                circuit = QuantumCircuit.create(numQubits);
                continue;
            }

            // -------------------------------------------------------------
            // creg
            // -------------------------------------------------------------
            if (stmt.startsWith("creg ")) {
                parseRegisterDecl(stmt, "creg");
                cregSize = extractRegisterSize(stmt, "creg");
                continue;
            }

            if (circuit == null)
                throw new QasmParseException("Operation before qreg: " + stmt);

            // -------------------------------------------------------------
            // measure
            // -------------------------------------------------------------
            if (stmt.startsWith("measure ")) {
                parseMeasure(stmt, circuit, cregSize);
                continue;
            }

            // -------------------------------------------------------------
            // GATES
            // -------------------------------------------------------------
            parseGate(stmt, circuit);
        }

        // -------------------------------------------------------------
        // If buffer contains leftover non-empty content → missing semicolon
        // -------------------------------------------------------------
        if (buffer.length() > 0 && !buffer.toString().trim().isEmpty()) {
            throw new QasmParseException("Missing semicolon: " + buffer.toString().trim());
        }

        return circuit;
    }

    // =========================================================================
    // Parsing helpers
    // =========================================================================

    private static void parseRegisterDecl(String stmt, String keyword) {
        // Example: qreg q[3]
        String trimmed = stmt.trim();
        if (!trimmed.startsWith(keyword + " "))
            throw new QasmParseException("Invalid register declaration: " + stmt);

        // Allowed pattern: keyword <name>[<size>]
        if (!trimmed.matches(keyword + "\\s+[A-Za-z_][A-Za-z_0-9]*\\s*\\[\\s*\\d+\\s*]"))
            throw new QasmParseException("Malformed register declaration: " + stmt);
    }

    private static int extractRegisterSize(String stmt, String keyword) {
        int lb = stmt.indexOf('[');
        int rb = stmt.indexOf(']');
        if (lb < 0 || rb < 0 || rb <= lb)
            throw new QasmParseException("Invalid register size: " + stmt);

        String inside = stmt.substring(lb + 1, rb).trim();
        try {
            return Integer.parseInt(inside);
        } catch (NumberFormatException e) {
            throw new QasmParseException("Invalid register size: " + inside);
        }
    }

    private static void parseMeasure(String stmt, QuantumCircuit circuit, int cregSize) {
        // measure q[0] -> c[1]
        String rest = stmt.substring("measure".length()).trim();

        String[] parts = rest.split("->");
        if (parts.length != 2)
            throw new QasmParseException("Malformed measure: " + stmt);

        String left = parts[0].trim();
        String right = parts[1].trim();

        int qIndex = parseIndexedRegister(left, "q");
        int cIndex = parseIndexedRegister(right, "c");

        if (cregSize >= 0 && cIndex >= cregSize)
            throw new QasmParseException("Classical index out of range: " + cIndex);

        circuit.measure(qIndex, cIndex);
    }

    private static void parseGate(String stmt, QuantumCircuit circuit) {
        String trimmed = stmt.trim();

        String gateName;
        String paramPart = null;
        String argPart;

        int parenOpen = trimmed.indexOf('(');
        int parenClose = trimmed.indexOf(')');

        // -------------------------------------------------------------
        // Parameterized gate: name(param,...) q[0],q[1]
        // -------------------------------------------------------------
        if (parenOpen >= 0 && parenClose > parenOpen) {
            gateName = trimmed.substring(0, parenOpen).trim();
            paramPart = trimmed.substring(parenOpen + 1, parenClose).trim();
            argPart = trimmed.substring(parenClose + 1).trim();

            if (argPart.isEmpty())
                throw new QasmParseException("Gate missing qubit args: " + stmt);

            if (!argPart.matches("q\\[\\d+](\\s*,\\s*q\\[\\d+])*$"))
                throw new QasmParseException("Malformed gate arguments: " + stmt);

        } else {
            // ---------------------------------------------------------
            // Non-parameterized gate: name q[a],q[b]
            // ---------------------------------------------------------
            int sp = trimmed.indexOf(' ');
            if (sp < 0)
                throw new QasmParseException("Gate missing arguments: " + stmt);

            gateName = trimmed.substring(0, sp).trim();
            argPart = trimmed.substring(sp + 1).trim();

            if (!argPart.matches("q\\[\\d+](\\s*,\\s*q\\[\\d+])*$"))
                throw new QasmParseException("Malformed gate arguments: " + stmt);
        }

        int[] qubits = parseQubitList(argPart);

        // Dispatch
        dispatchGate(stmt, gateName.toLowerCase(), paramPart, qubits, circuit);
    }

    private static int[] parseQubitList(String argPart) {
        String[] tokens = argPart.split(",");
        int[] out = new int[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
            out[i] = parseIndexedRegister(tokens[i].trim(), "q");
        }
        return out;
    }

    private static void dispatchGate(String stmt, String op, String paramPart, int[] q, QuantumCircuit c) {
        switch (op) {
            // ---------------------------------------------------------
            // Single-qubit (no params)
            // ---------------------------------------------------------
            case "x": ensureArity(op, 1, q.length, stmt); c.x(q[0]); return;
            case "y": ensureArity(op, 1, q.length, stmt); c.y(q[0]); return;
            case "z": ensureArity(op, 1, q.length, stmt); c.z(q[0]); return;
            case "h": ensureArity(op, 1, q.length, stmt); c.h(q[0]); return;
            case "s": ensureArity(op, 1, q.length, stmt); c.s(q[0]); return;
            case "t": ensureArity(op, 1, q.length, stmt); c.t(q[0]); return;

            // ---------------------------------------------------------
            // Single-qubit (1 param)
            // ---------------------------------------------------------
            case "rx": ensureArity(op, 1, q.length, stmt); c.rx(q[0], parseSingleParam(op, paramPart, stmt)); return;
            case "ry": ensureArity(op, 1, q.length, stmt); c.ry(q[0], parseSingleParam(op, paramPart, stmt)); return;
            case "rz": ensureArity(op, 1, q.length, stmt); c.rz(q[0], parseSingleParam(op, paramPart, stmt)); return;

            case "u1":
                ensureArity(op, 1, q.length, stmt);
                c.u1(q[0], parseSingleParam(op, paramPart, stmt));
                return;

            // ---------------------------------------------------------
            // Single-qubit (multi-param)
            // ---------------------------------------------------------
            case "u2": {
                ensureArity(op, 1, q.length, stmt);
                double[] p = parseParamList(op, paramPart, 2, stmt);
                c.u2(q[0], p[0], p[1]);
                return;
            }
            case "u3": {
                ensureArity(op, 1, q.length, stmt);
                double[] p = parseParamList(op, paramPart, 3, stmt);
                c.u3(q[0], p[0], p[1], p[2]);
                return;
            }

            // ---------------------------------------------------------
            // Two-qubit
            // ---------------------------------------------------------
            case "cx": ensureArity(op, 2, q.length, stmt); c.cx(q[0], q[1]); return;
            case "cz": ensureArity(op, 2, q.length, stmt); c.cz(q[0], q[1]); return;
            case "swap": ensureArity(op, 2, q.length, stmt); c.swap(q[0], q[1]); return;
            case "iswap": ensureArity(op, 2, q.length, stmt); c.iswap(q[0], q[1]); return;
            case "ch": ensureArity(op, 2, q.length, stmt); c.ch(q[0], q[1]); return;

            // ---------------------------------------------------------
            // Three-qubit
            // ---------------------------------------------------------
            case "ccx":
                ensureArity(op, 3, q.length, stmt);
                c.ccx(q[0], q[1], q[2]);
                return;
        }

        throw new QasmParseException("Unknown or unsupported gate: " + op);
    }

    private static void ensureArity(String gate, int expected, int found, String stmt) {
        if (found != expected)
            throw new QasmParseException("Gate '" + gate + "' takes " +
                expected + " qubits but found " + found + ": " + stmt);
    }

    // =========================================================================
    // Parameter parsing
    // =========================================================================

    private static double parseSingleParam(String gate, String param, String stmt) {
        if (param == null || param.isEmpty())
            throw new QasmParseException("Gate '" + gate + "' requires 1 parameter: " + stmt);

        try {
            return Double.parseDouble(param.trim());
        } catch (Exception e) {
            throw new QasmParseException("Invalid parameter for gate '" + gate + "': " + param);
        }
    }

    private static double[] parseParamList(String gate, String param, int count, String stmt) {
        if (param == null)
            throw new QasmParseException("Gate '" + gate + "' requires " + count + " parameters: " + stmt);

        String[] tokens = param.split(",");
        if (tokens.length != count)
            throw new QasmParseException("Gate '" + gate + "' requires " + count + " params but got " + tokens.length);

        double[] out = new double[count];
        for (int i = 0; i < count; i++) {
            try {
                out[i] = Double.parseDouble(tokens[i].trim());
            } catch (Exception e) {
                throw new QasmParseException("Invalid parameter " + i + " for gate '" + gate + "': " + tokens[i]);
            }
        }
        return out;
    }

    // =========================================================================
    // Core utilities
    // =========================================================================

    private static int parseIndexedRegister(String tok, String expectedName) {
        int lb = tok.indexOf('[');
        int rb = tok.indexOf(']');

        if (lb < 0 || rb < 0 || rb < lb)
            throw new QasmParseException("Invalid indexed register: " + tok);

        String name = tok.substring(0, lb).trim();
        if (!name.equals(expectedName))
            throw new QasmParseException("Expected register '" + expectedName + "' but found '" + name + "'");

        String idx = tok.substring(lb + 1, rb).trim();
        try {
            return Integer.parseInt(idx);
        } catch (NumberFormatException e) {
            throw new QasmParseException("Invalid index in register '" + tok + "'");
        }
    }

    private static String stripComments(String src) {
        StringBuilder sb = new StringBuilder();
        for (String line : src.split("\r?\n")) {
            int idx = line.indexOf("//");
            if (idx >= 0) line = line.substring(0, idx);
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    // =========================================================================
    // Exception
    // =========================================================================
    public static class QasmParseException extends RuntimeException {
        public QasmParseException(String msg) { super(msg); }
        public QasmParseException(String msg, Throwable c) { super(msg, c); }
    }
}

