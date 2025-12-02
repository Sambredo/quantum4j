package com.quantum4j.transpile.passes;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.transpile.TranspilerPass;

import java.util.ArrayList;
import java.util.List;

/**
 * Fuses consecutive same-axis rotations (RZ, RX, RY) on the same qubit by summing angles.
 * Even/zero net rotations within tolerance are removed.
 */
public final class RotationFusionPass implements TranspilerPass {

    private static final double EPS = 1e-12;

    /**
     * Create the rotation fusion pass.
     */
    public RotationFusionPass() {
    }

    @Override
    public String name() {
        return "RotationFusionPass";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        if (circuit == null) {
            throw new IllegalArgumentException("circuit must not be null");
        }

        List<Instruction> out = new ArrayList<>();

        for (Instruction inst : circuit.getInstructions()) {
            if (isRotation(inst)) {
                // Attempt to fuse with previous output rotation of same axis and qubit
                if (!out.isEmpty() && sameAxis(out.get(out.size() - 1), inst)
                        && sameQubit(out.get(out.size() - 1), inst)) {
                    Instruction prev = out.remove(out.size() - 1);
                    double fused = getTheta(prev) + getTheta(inst);
                    if (Math.abs(fused) < EPS) {
                        // cancels out
                        continue;
                    }
                    out.add(makeRotation(prev, fused));
                    continue;
                }
                out.add(inst.copy());
            } else {
                out.add(inst.copy());
            }
        }

        QuantumCircuit result = QuantumCircuit.create(circuit.getNumQubits());
        for (Instruction i : out) {
            result.addInstruction(i);
        }
        return result;
    }

    private boolean isRotation(Instruction inst) {
        return isRZ(inst) || isRX(inst) || isRY(inst);
    }

    private boolean isRZ(Instruction inst) {
        return inst.getType() == Instruction.Type.GATE && inst.getGate() instanceof StandardGates.RZGate;
    }

    private boolean isRX(Instruction inst) {
        return inst.getType() == Instruction.Type.GATE && inst.getGate() instanceof StandardGates.RXGate;
    }

    private boolean isRY(Instruction inst) {
        return inst.getType() == Instruction.Type.GATE && inst.getGate() instanceof StandardGates.RYGate;
    }

    private double getTheta(Instruction inst) {
        if (isRZ(inst)) return ((StandardGates.RZGate) inst.getGate()).getTheta();
        if (isRX(inst)) return ((StandardGates.RXGate) inst.getGate()).getTheta();
        if (isRY(inst)) return ((StandardGates.RYGate) inst.getGate()).getTheta();
        throw new IllegalArgumentException("Not a rotation instruction");
    }

    private boolean sameAxis(Instruction a, Instruction b) {
        return (isRZ(a) && isRZ(b)) || (isRX(a) && isRX(b)) || (isRY(a) && isRY(b));
    }

    private boolean sameQubit(Instruction a, Instruction b) {
        int[] qa = a.getQubits();
        int[] qb = b.getQubits();
        return qa.length == 1 && qb.length == 1 && qa[0] == qb[0];
    }

    private Instruction makeRotation(Instruction like, double theta) {
        int q = like.getQubits()[0];
        if (isRZ(like)) {
            return Instruction.gate(new StandardGates.RZGate(theta), q);
        }
        if (isRX(like)) {
            return Instruction.gate(new StandardGates.RXGate(theta), q);
        }
        if (isRY(like)) {
            return Instruction.gate(new StandardGates.RYGate(theta), q);
        }
        throw new IllegalArgumentException("Not a rotation instruction");
    }
}


