package io.quantum4j.qasm;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.*;

/**
 * Utility class to export quantum circuits to OpenQASM 2.0 format.
 * <p>
 * OpenQASM (Open Quantum Assembly Language) is a standard format for representing quantum circuits. This exporter
 * converts a QuantumCircuit to valid OpenQASM code.
 * </p>
 */
public final class QasmExporter {

    private QasmExporter() {
    }

    /**
     * Convert a quantum circuit to OpenQASM 2.0 format.
     *
     * @param circuit
     *            the circuit to export
     *
     * @return a string containing the OpenQASM 2.0 code
     */
    public static String toQasm(QuantumCircuit circuit) {

        StringBuilder sb = new StringBuilder();

        sb.append("OPENQASM 2.0;\n");
        sb.append("include \"qelib1.inc\";\n\n");

        int nq = circuit.getNumQubits();
        sb.append("qreg q[").append(nq).append("];\n");
        sb.append("creg c[").append(nq).append("];\n\n");

        for (Instruction inst : circuit.getInstructions()) {

            switch (inst.getType()) {

            case GATE:
                appendGate(sb, inst);
                break;

            case MEASURE:
                int q = inst.getQubits()[0];
                int c = inst.getClassicalBits()[0];
                sb.append("measure q[").append(q).append("] -> c[").append(c).append("];\n");
                break;

            default:
                throw new IllegalStateException("Unknown instruction: " + inst.getType());
            }
        }

        return sb.toString();
    }

    // ----------------------------------------------------------------------
    // Gate Serialization
    // ----------------------------------------------------------------------

    private static void appendGate(StringBuilder sb, Instruction inst) {

        Gate g = inst.getGate();
        int[] qs = inst.getQubits();
        String name = g.name().toLowerCase();

        // -------- Single-qubit parameterized gates --------
        if (g instanceof RXGate) {
            sb.append("rx(").append(((RXGate) g).getTheta()).append(") q[").append(qs[0]).append("];\n");
            return;
        }

        if (g instanceof RYGate) {
            sb.append("ry(").append(((RYGate) g).getTheta()).append(") q[").append(qs[0]).append("];\n");
            return;
        }

        if (g instanceof RZGate) {
            sb.append("rz(").append(((RZGate) g).getTheta()).append(") q[").append(qs[0]).append("];\n");
            return;
        }

        // -------- Standard 1-qubit gates --------
        if (g instanceof SingleQubitGate) {
            sb.append(name).append(" q[").append(qs[0]).append("];\n");
            return;
        }

        // -------- 2-qubit gates --------
        if (g instanceof TwoQubitGate) {

            if (g instanceof CNOTGate)
                sb.append("cx q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");
            else if (g instanceof CZGate)
                sb.append("cz q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");
            else if (g instanceof SWAPGate)
                sb.append("swap q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");
            else if (g instanceof ISWAPGate)
                sb.append("iswap q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");
            else if (g instanceof CHGate)
                sb.append("ch q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");
            else
                sb.append(name).append(" q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");

            return;
        }

        // -------- 3-qubit gates --------
        if (g instanceof CCXGate) {
            sb.append("ccx q[").append(qs[0]).append("], q[").append(qs[1]).append("], q[").append(qs[2])
                    .append("];\n");
            return;
        }

        throw new UnsupportedOperationException("Unknown gate type: " + g.getClass());
    }
}
