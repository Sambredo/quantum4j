package io.quantum4j.examples;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.transpile.DefaultTranspiler;

/**
 * Runs the default transpiler pipeline on a sample circuit and prints before/after instruction counts.
 */
public final class DefaultTranspilerDemo {
    public static void main(String[] args) {
        QuantumCircuit circuit = QuantumCircuit.create(2)
                .swap(0, 1)
                .cx(0, 1)
                .cx(0, 1)
                .u3(0, 0.1, 0.2, 0.3)
                .measureAll();

        System.out.println("Before transpile: " + circuit.getInstructions().size() + " instructions");
        QuantumCircuit out = DefaultTranspiler.transpile(circuit);
        System.out.println("After transpile: " + out.getInstructions().size() + " instructions");
    }
}

