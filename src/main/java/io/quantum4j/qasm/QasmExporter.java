package io.quantum4j.qasm;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.*;

/**
 * Exports QuantumCircuit objects to OpenQASM 2.0.
 */
public final class QasmExporter {

    private QasmExporter() {}

    /**
     * Convert a circuit to OpenQASM 2.0 format.
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
                    throw new IllegalStateException("Unknown instruction type: " + inst.getType());
            }
        }

        return sb.toString();
    }

    // ----------------------------------------------------------------------
    // Gate Serialization
    // ----------------------------------------------------------------------

    private static void appendGate(StringBuilder sb, Instruction inst) {

        Gate gate = inst.getGate();
        int[] qs = inst.getQubits();
        String name = gate.name().toLowerCase();

        // ------------------------------------------------------------------
        // Parameterized gates (RX, RY, RZ)
        // ------------------------------------------------------------------
        if (gate instanceof StandardGates.RXGate rx) {
            sb.append("rx(").append(rx.getTheta()).append(") q[").append(qs[0]).append("];\n");
            return;
        }

        if (gate instanceof StandardGates.RYGate ry) {
            sb.append("ry(").append(ry.getTheta()).append(") q[").append(qs[0]).append("];\n");
            return;
        }

        if (gate instanceof StandardGates.RZGate rz) {
            sb.append("rz(").append(rz.getTheta()).append(") q[").append(qs[0]).append("];\n");
            return;
        }

        // ------------------------------------------------------------------
        // U1 / U2 / U3 gates
        // ------------------------------------------------------------------
        if (gate instanceof StandardGates.U1Gate u1) {
            sb.append("u1(")
              .append(u1.getLambda())
              .append(") q[").append(qs[0]).append("];\n");
            return;
        }

        if (gate instanceof StandardGates.U2Gate u2) {
            sb.append("u2(")
              .append(u2.getPhi()).append(", ")
              .append(u2.getLambda())
              .append(") q[").append(qs[0]).append("];\n");
            return;
        }

        if (gate instanceof StandardGates.U3Gate u3) {
            sb.append("u3(")
              .append(u3.getTheta()).append(", ")
              .append(u3.getPhi()).append(", ")
              .append(u3.getLambda())
              .append(") q[").append(qs[0]).append("];\n");
            return;
        }

        // ------------------------------------------------------------------
        // Standard single-qubit gates (x, y, z, h, s, t, etc.)
        // ------------------------------------------------------------------
        if (gate instanceof SingleQubitGate) {
            sb.append(name)
              .append(" q[").append(qs[0]).append("];\n");
            return;
        }

        // ------------------------------------------------------------------
        // 2-qubit gates
        // ------------------------------------------------------------------
        if (gate instanceof TwoQubitGate) {

            if (gate instanceof StandardGates.CNOTGate) {
                sb.append("cx q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");
                return;
            }

            if (gate instanceof StandardGates.CZGate) {
                sb.append("cz q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");
                return;
            }

            if (gate instanceof StandardGates.SWAPGate) {
                sb.append("swap q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");
                return;
            }

            if (gate instanceof StandardGates.ISWAPGate) {
                sb.append("iswap q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");
                return;
            }

            if (gate instanceof StandardGates.CHGate) {
                sb.append("ch q[").append(qs[0]).append("], q[").append(qs[1]).append("];\n");
                return;
            }

            // Fallback for unknown 2-qubit gate
            sb.append(name).append(" q[").append(qs[0])
              .append("], q[").append(qs[1]).append("];\n");
            return;
        }

        // ------------------------------------------------------------------
        // 3-qubit gates (currently CCX)
        // ------------------------------------------------------------------
        if (gate instanceof StandardGates.CCXGate) {
            sb.append("ccx q[").append(qs[0]).append("], q[").append(qs[1])
              .append("], q[").append(qs[2]).append("];\n");
            return;
        }

        throw new UnsupportedOperationException("Unsupported gate type: " + gate.getClass().getName());
    }
}
