package io.quantum4j.transpile.passes;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.StandardGates;
import io.quantum4j.transpile.TranspilerPass;

import java.util.List;

public final class BasisGateDecompositionPass implements TranspilerPass {

    private static final double PI = Math.PI;

    @Override
    public String name() {
        return "BasisGateDecompositionPass";
    }

    @Override
    public QuantumCircuit apply(QuantumCircuit circuit) {
        if (circuit == null)
            throw new IllegalArgumentException("circuit must not be null");

        QuantumCircuit out = QuantumCircuit.create(circuit.getNumQubits());

        for (Instruction inst : circuit.getInstructions()) {

            // Preserve measurement
            if (inst.getType() == Instruction.Type.MEASURE) {
                out.measure(inst.getQubits()[0], inst.getClassicalBits()[0]);
                continue;
            }

            // Gate case:
            int q = inst.getQubits()[0];
            var gate = inst.getGate();

            if (gate instanceof StandardGates.U1Gate) {
                double lambda = ((StandardGates.U1Gate) gate).getLambda();
                out.rz(q, lambda);
                continue;
            }

            if (gate instanceof StandardGates.U2Gate) {
                double phi = ((StandardGates.U2Gate) gate).getPhi();
                double lambda = ((StandardGates.U2Gate) gate).getLambda();

                out.rz(q, phi);
                out.ry(q, PI / 2.0);
                out.rz(q, lambda);
                continue;
            }

            if (gate instanceof StandardGates.U3Gate) {
                double theta  = ((StandardGates.U3Gate) gate).getTheta();
                double phi    = ((StandardGates.U3Gate) gate).getPhi();
                double lambda = ((StandardGates.U3Gate) gate).getLambda();

                out.rz(q, phi);
                out.ry(q, theta);
                out.rz(q, lambda);
                continue;
            }

            // Other gates → copy unchanged
            out.addInstruction(inst);
        }

        return out;
    }
}
