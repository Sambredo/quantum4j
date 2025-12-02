package io.quantum4j.examples;

import io.quantum4j.core.circuit.QuantumCircuit;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demonstrates ASCII and SVG rendering of a simple circuit.
 */
public final class CircuitDrawingExample {
    public static void main(String[] args) throws Exception {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();

        System.out.println("ASCII diagram:");
        System.out.println(qc.drawAscii());

        String svg = qc.toSvg();
        Files.writeString(Path.of("bell.svg"), svg);
        System.out.println("SVG written to bell.svg");
    }
}
