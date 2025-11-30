package io.quantum4j.examples;

import io.quantum4j.core.circuit.QuantumCircuit;

/**
 * Shows how Quantum4J reports illegal arguments (invalid qubit indices).
 */
public final class ErrorHandlingDemo {
    public static void main(String[] args) {
        try {
            QuantumCircuit qc = QuantumCircuit.create(1)
                    .cx(0, 1); // invalid target
            System.out.println(qc); // not reached
        } catch (IllegalArgumentException ex) {
            System.out.println("Caught expected error: " + ex.getMessage());
        }
    }
}

