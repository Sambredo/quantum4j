package io.quantum4j.core.examples;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.gates.StandardGates;
import io.quantum4j.transpile.PassManager;
import io.quantum4j.transpile.passes.BasisGateDecompositionPass;

public final class ExampleBasisDecomposition {

    public static void main(String[] args) {

        // Build a circuit
        QuantumCircuit circuit = QuantumCircuit.create(1)
                .h(0)
                .u3(0, Math.PI / 2, Math.PI / 4, Math.PI / 8)
                .measure(0, 0);

        System.out.println("Original circuit:");
        print(circuit);

        // Transpile
        QuantumCircuit out = new PassManager()
                .addPass(new BasisGateDecompositionPass())
                .run(circuit);

        System.out.println("\nAfter BasisGateDecompositionPass:");
        print(out);
    }

    private static void print(QuantumCircuit qc) {
        for (Instruction inst : qc.getInstructions()) {
            if (inst.getType() == Instruction.Type.MEASURE) {
                System.out.println("measure q[" + inst.getQubits()[0] + "] -> c[" + inst.getClassicalBits()[0] + "]");
                return;
            }

            var gate = inst.getGate();
            String name = gate.name().toLowerCase();

            if (gate instanceof StandardGates.RZGate) {
                System.out.println("rz(" + ((StandardGates.RZGate) gate).getTheta() + ") q[" + inst.getQubits()[0] + "]");
            } else if (gate instanceof StandardGates.RYGate) {
                System.out.println("ry(" + ((StandardGates.RYGate) gate).getTheta() + ") q[" + inst.getQubits()[0] + "]");
            } else {
                System.out.println(name + " q[" + inst.getQubits()[0] + "]");
            }
        }
    }
}
