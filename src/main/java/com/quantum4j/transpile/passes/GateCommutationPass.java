package com.quantum4j.transpile.passes;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.core.gates.TwoQubitGate;
import com.quantum4j.transpile.TranspilerPass;

import java.util.ArrayList;
import java.util.List;

/**
 * Local commutation optimizer. Safely bubbles commuting gates earlier in the circuit without crossing non-commuting
 * operations or measurements. Conservative rules:
 * <ul>
 *   <li>Single-qubit gates on different qubits commute.</li>
 *   <li>RZ on control commutes with CX(control,target).</li>
 *   <li>RX on target commutes with CX(control,target).</li>
 *   <li>Never move across measurements.</li>
 * </ul>
 */
public final class GateCommutationPass implements TranspilerPass {

    /**
     * Create the gate commutation pass.
     */
    public GateCommutationPass() {
    }

    @Override
    public String name() {
        return "GateCommutationPass";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        if (circuit == null) {
            throw new IllegalArgumentException("circuit must not be null");
        }

        List<Instruction> original = circuit.getInstructions();
        List<Instruction> out = new ArrayList<>();

        for (Instruction inst : original) {
            Instruction current = inst.copy();

            // Measurements are barriers: just append
            if (current.getType() == Instruction.Type.MEASURE) {
                out.add(current);
                continue;
            }

            // Append then bubble left as long as commutation holds and no measurement barrier
            out.add(current);
            for (int pos = out.size() - 1; pos > 0; pos--) {
                Instruction prev = out.get(pos - 1);
                Instruction cur = out.get(pos);
                if (prev.getType() == Instruction.Type.MEASURE) {
                    break; // barrier
                }
                if (commutes(cur, prev)) {
                    // swap positions
                    out.set(pos - 1, cur);
                    out.set(pos, prev);
                } else {
                    break;
                }
            }
        }

        QuantumCircuit result = QuantumCircuit.create(circuit.getNumQubits());
        for (Instruction i : out) {
            result.addInstruction(i);
        }
        return result;
    }

    /**
     * Directional commutation: can current move left across prev?
     */
    private boolean commutes(Instruction current, Instruction prev) {
        if (current.getType() != Instruction.Type.GATE || prev.getType() != Instruction.Type.GATE) {
            return false;
        }
        Gate gc = current.getGate();
        Gate gp = prev.getGate();

        // Single-qubit on different qubits
        if (isSingle(gc) && isSingle(gp)) {
            int qc = current.getQubits()[0];
            int qp = prev.getQubits()[0];
            return qc != qp && qc > qp; // deterministic ordering for stability
        }

        // RZ(control) can move left across CX(control, target)
        if (isRZ(gc) && isCX(prev)) {
            int control = prev.getQubits()[0];
            return current.getQubits()[0] == control;
        }

        // RX(target) can move left across CX(control, target)
        if (isRX(gc) && isCX(prev)) {
            int target = prev.getQubits()[1];
            return current.getQubits()[0] == target;
        }

        return false;
    }

    private boolean isSingle(Gate g) {
        return g instanceof StandardGates.HGate
                || g instanceof StandardGates.XGate
                || g instanceof StandardGates.YGate
                || g instanceof StandardGates.ZGate
                || g instanceof StandardGates.SGate
                || g instanceof StandardGates.TGate
                || g instanceof StandardGates.RXGate
                || g instanceof StandardGates.RYGate
                || g instanceof StandardGates.RZGate;
    }

    private boolean isCX(Instruction inst) {
        return inst.getGate() instanceof StandardGates.CNOTGate;
    }

    private boolean isRX(Gate g) {
        return g instanceof StandardGates.RXGate;
    }

    private boolean isRZ(Gate g) {
        return g instanceof StandardGates.RZGate;
    }
}


