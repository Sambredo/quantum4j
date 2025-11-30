package io.quantum4j.core.examples;

import io.quantum4j.core.circuit.Instruction;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.core.gates.StandardGates;
import io.quantum4j.transpile.PassManager;
import io.quantum4j.transpile.passes.CXToCZDecompositionPass;

public final class ExampleCXDecomposition {

    public static void main(String[] args) {

        QuantumCircuit circuit = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measure(0, 0)
                .measure(1, 1);

        System.out.println("Original circuit:");
        printCircuit(circuit);

        QuantumCircuit out = new PassManager()
                .addPass(new CXToCZDecompositionPass())
                .run(circuit);

        System.out.println("\nAfter CXToCZDecompositionPass:");
        printCircuit(out);
    }

    private static void printCircuit(QuantumCircuit qc) {
        for (Instruction inst : qc.getInstructions()) {
            if (inst.getType() == Instruction.Type.MEASURE) {
                System.out.println("measure q[" + inst.getQubits()[0] + "] -> c[" + inst.getClassicalBits()[0] + "]");
                continue;
            }

            var gate = inst.getGate();
            String name = gate.name();

            if (gate instanceof StandardGates.RZGate) {
                double theta = ((StandardGates.RZGate) gate).getTheta();
                System.out.println("rz(" + theta + ") q[" + inst.getQubits()[0] + "]");
            } else if (gate instanceof StandardGates.RYGate) {
                double theta = ((StandardGates.RYGate) gate).getTheta();
                System.out.println("ry(" + theta + ") q[" + inst.getQubits()[0] + "]");
            } else {
                // Default: show name + qubits
                int[] q = inst.getQubits();
                if (q.length == 1) {
                    System.out.println(name + " q[" + q[0] + "]");
                } else {
                    System.out.println(name + " q[" + q[0] + "], q[" + q[1] + "]");
                }
            }
        }
    }
}
