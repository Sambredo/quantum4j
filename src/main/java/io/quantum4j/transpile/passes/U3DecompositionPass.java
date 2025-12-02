package com.quantum4j.transpile.passes;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.transpile.TranspilerPass;

import java.util.List;

/**
 * Decomposes U1/U2/U3 gates into native rotations:
 * <pre>
 *   U3(θ, φ, λ) => RZ(φ) ; RX(π/2) ; RZ(θ) ; RX(-π/2) ; RZ(λ)
 *   U2(φ, λ)    => U3(π/2, φ, λ)
 *   U1(λ)       => RZ(λ)
 * </pre>
 */
public final class U3DecompositionPass implements TranspilerPass {

    private static final double HALF_PI = Math.PI / 2.0;

    /**
     * Create the U3 decomposition pass.
     */
    public U3DecompositionPass() {
    }

    @Override
    public String name() {
        return "U3DecompositionPass";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        if (circuit == null) {
            throw new IllegalArgumentException("circuit must not be null");
        }

        QuantumCircuit out = QuantumCircuit.create(circuit.getNumQubits());
        List<Instruction> instructions = circuit.getInstructions();

        for (Instruction inst : instructions) {
            if (isU3(inst)) {
                int q = inst.getQubits()[0];
                StandardGates.U3Gate u3 = (StandardGates.U3Gate) inst.getGate();
                double theta = u3.getTheta();
                double phi = u3.getPhi();
                double lambda = u3.getLambda();
                // RZ(φ) ; RX(π/2) ; RZ(θ) ; RX(-π/2) ; RZ(λ)
                out.rz(q, phi);
                out.rx(q, HALF_PI);
                out.rz(q, theta);
                out.rx(q, -HALF_PI);
                out.rz(q, lambda);
            } else if (isU2(inst)) {
                int q = inst.getQubits()[0];
                StandardGates.U2Gate u2 = (StandardGates.U2Gate) inst.getGate();
                double phi = u2.getPhi();
                double lambda = u2.getLambda();
                // Treat as U3(π/2, phi, lambda)
                out.rz(q, phi);
                out.rx(q, HALF_PI);
                out.rz(q, HALF_PI);
                out.rx(q, -HALF_PI);
                out.rz(q, lambda);
            } else if (isU1(inst)) {
                int q = inst.getQubits()[0];
                StandardGates.U1Gate u1 = (StandardGates.U1Gate) inst.getGate();
                out.rz(q, u1.getLambda());
            } else {
                out.addInstruction(inst.copy());
            }
        }

        return out;
    }

    private boolean isU3(Instruction inst) {
        return inst.getType() == Instruction.Type.GATE && inst.getGate() instanceof StandardGates.U3Gate;
    }

    private boolean isU2(Instruction inst) {
        return inst.getType() == Instruction.Type.GATE && inst.getGate() instanceof StandardGates.U2Gate;
    }

    private boolean isU1(Instruction inst) {
        return inst.getType() == Instruction.Type.GATE && inst.getGate() instanceof StandardGates.U1Gate;
    }
}



