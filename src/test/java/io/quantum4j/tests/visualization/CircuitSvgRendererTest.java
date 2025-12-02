package io.quantum4j.tests.visualization;

import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.visualization.CircuitSvgRenderer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircuitSvgRendererTest {

    @Test
    void svgFormatContainsShapes() {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();
        String svg = CircuitSvgRenderer.render(qc);
        assertTrue(svg.startsWith("<svg"));
        assertTrue(svg.contains("<line"));
        assertTrue(svg.contains("<rect"));
    }

    @Test
    void deterministicSvg() {
        QuantumCircuit qc = QuantumCircuit.create(1).h(0).measureAll();
        String a = CircuitSvgRenderer.render(qc);
        String b = CircuitSvgRenderer.render(qc);
        assertEquals(a, b);
    }
}
