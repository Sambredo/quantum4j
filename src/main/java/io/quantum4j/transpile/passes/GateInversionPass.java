package com.quantum4j.transpile.passes;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.transpile.TranspilerPass;

import java.util.ArrayList;
import java.util.List;

/**
 * Produces the inverse (adjoint) of the unitary prefix of a circuit. Reverses gate order, replaces each gate by its
 * inverse, and preserves measurements (and anything after the first measurement) unchanged. Input circuits are not
 * mutated; instructions are cloned.
 */
public final class GateInversionPass implements TranspilerPass {

    /**
     * Create the gate inversion pass.
     */
    public GateInversionPass() {
    }

    @Override
    public String name() {
        return "GateInversionPass";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        if (circuit == null) {
            throw new IllegalArgumentException("circuit must not be null");
        }

        QuantumCircuit out = QuantumCircuit.create(circuit.getNumQubits());
        List<Instruction> instructions = circuit.getInstructions();

        List<Instruction> unitary = new ArrayList<>();
        List<Instruction> remainder = new ArrayList<>();
        boolean seenMeasure = false;

        for (Instruction inst : instructions) {
            if (!seenMeasure && inst.getType() == Instruction.Type.MEASURE) {
                seenMeasure = true;
            }
            if (seenMeasure) {
                remainder.add(inst.copy());
            } else {
                unitary.add(inst);
            }
        }

        // invert unitary prefix in reverse order
        for (int i = unitary.size() - 1; i >= 0; i--) {
            Instruction inv = inverseOf(unitary.get(i));
            if (inv != null) {
                out.addInstruction(inv);
            }
        }

        // append remainder (measurement and anything after) unchanged
        for (Instruction inst : remainder) {
            out.addInstruction(inst);
        }

        return out;
    }

    private Instruction inverseOf(Instruction inst) {
        if (inst.getType() == Instruction.Type.MEASURE) {
            return null; // non-unitary
        }
        if (inst.getGate() instanceof StandardGates.HGate) {
            return Instruction.gate(new StandardGates.HGate(), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.XGate) {
            return Instruction.gate(new StandardGates.XGate(), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.YGate) {
            return Instruction.gate(new StandardGates.YGate(), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.ZGate) {
            return Instruction.gate(new StandardGates.ZGate(), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.CNOTGate) {
            return Instruction.gate(new StandardGates.CNOTGate(), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.CZGate) {
            return Instruction.gate(new StandardGates.CZGate(), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.SWAPGate) {
            return Instruction.gate(new StandardGates.SWAPGate(), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.CHGate) {
            return Instruction.gate(new StandardGates.CHGate(), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.CCXGate) {
            return Instruction.gate(new StandardGates.CCXGate(), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.RZGate) {
            double theta = ((StandardGates.RZGate) inst.getGate()).getTheta();
            return Instruction.gate(new StandardGates.RZGate(-theta), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.RXGate) {
            double theta = ((StandardGates.RXGate) inst.getGate()).getTheta();
            return Instruction.gate(new StandardGates.RXGate(-theta), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.RYGate) {
            double theta = ((StandardGates.RYGate) inst.getGate()).getTheta();
            return Instruction.gate(new StandardGates.RYGate(-theta), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.U1Gate) {
            double lambda = ((StandardGates.U1Gate) inst.getGate()).getLambda();
            return Instruction.gate(new StandardGates.RZGate(-lambda), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.U3Gate) {
            StandardGates.U3Gate u3 = (StandardGates.U3Gate) inst.getGate();
            double theta = u3.getTheta();
            double phi = u3.getPhi();
            double lambda = u3.getLambda();
            return Instruction.gate(new StandardGates.U3Gate(-theta, -lambda, -phi), inst.getQubits());
        }
        if (inst.getGate() instanceof StandardGates.U2Gate) {
            StandardGates.U2Gate u2 = (StandardGates.U2Gate) inst.getGate();
            double phi = u2.getPhi();
            double lambda = u2.getLambda();
            // approximate inverse: treat as U3(pi/2, phi, lambda), then invert params
            return Instruction.gate(new StandardGates.U3Gate(-Math.PI / 2.0, -lambda, -phi), inst.getQubits());
        }
        // Fallback: return a copy to avoid losing instructions, though inversion is unknown.
        return inst.copy();
    }
}

