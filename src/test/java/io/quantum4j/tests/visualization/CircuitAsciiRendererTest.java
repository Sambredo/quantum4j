package io.quantum4j.tests.visualization;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.visualization.CircuitAsciiRenderer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CircuitAsciiRendererTest {

    @Test
    void asciiBellPair() {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();
        String ascii = CircuitAsciiRenderer.render(qc);
        assertTrue(ascii.contains("q0"));
        assertTrue(ascii.contains("┤ H ├"));
        assertTrue(ascii.contains("●"));
        assertTrue(ascii.contains("X"));
        assertTrue(ascii.contains("┤ M ├"));
    }

    @Test
    void swapAsciiShowsCrossing() {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .swap(0, 1)
                .measureAll();
        String ascii = CircuitAsciiRenderer.render(qc);
        assertTrue(ascii.contains("x"));
        assertTrue(ascii.contains("│"));
    }
}
